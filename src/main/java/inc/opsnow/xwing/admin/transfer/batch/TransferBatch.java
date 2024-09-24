package inc.opsnow.xwing.admin.transfer.batch;

import inc.opsnow.xwing.admin.transfer.external.EngineStartService;
import inc.opsnow.xwing.admin.transfer.external.ecs.EcsService;
import inc.opsnow.xwing.admin.transfer.service.DashboardService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.faulttolerance.Retry;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class TransferBatch {

    @ConfigProperty(name = "site_id", defaultValue = "OPSNOW")
    String siteId;

    @Inject
    DashboardService dashboardService;

    @Inject
    EcsService ecsService;

    @RestClient
    EngineStartService engineStartService;


    private static final int MAX_RETRIES = 10;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(30);
    private static final Duration HEALTH_CHECK_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration HEALTH_CHECK_INTERVAL = Duration.ofMinutes(1);


    // batch 스케줄러
    // cron 초, 분, 시, 일, 월
    //@Scheduled(cron = "0 45 1 * * ?", identity = "transfer-init-daily-job")
    @Scheduled(cron = "0 0 */3 * * ?", identity = "transfer-init-daily-job")
    void schedule() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Log.infof("Daily job executed at: " + formattedDateTime);
        Log.infof("TransferBatch schedule start");

        startTransferBatch()
                .onItem().invoke(() -> Log.info("TransferBatch schedule completed successfully"))
                .onFailure().invoke(error -> Log.error("TransferBatch schedule failed", error))
                .subscribe().with(
                        item -> {
                            processDashboardAndTrigger()
                                    .chain(v -> stopAdmEngine())
                                    .subscribe().with(
                                            res -> Log.info("TransferBatch processDashboardAndTrigger completed successfully"),
                                            error -> Log.error("TransferBatch processDashboardAndTrigger failed", error)
                                    );
                        }, // 성공 시 추가 작업이 필요 없으면 빈 람다
                        error -> {} // 실패 시 추가 작업이 필요 없으면 빈 람다
                );
    }

    @Retry(maxRetries = MAX_RETRIES, delay = 30000, retryOn = Exception.class)
    Uni<Void> startTransferBatch() {
        return Uni.createFrom().item(() -> {
                    return startAdmEngine()
                            .chain(v -> waitForHealthyEngine());
                })
                .onFailure().invoke(e -> {
                    Log.errorf("Error in startTransferBatch: %s. Retrying...", e.getMessage());
                })
                .onFailure().retry().withBackOff(RETRY_DELAY).atMost(MAX_RETRIES)
                .onFailure().invoke(e -> {
                    Log.errorf("All retries failed for startTransferBatch. Final error: %s", e.getMessage());
                }).replaceWithVoid();
    }

    private Uni<Void> startAdmEngine() {
        return ecsService.updateAdmEngineService(1)
                .onItem().transformToUni(response -> {
                    if (response.isSuccessful()) {
                        Log.info("EcsService updateAdmEngineService successful");
                        return Uni.createFrom().voidItem();
                    } else {
                        Log.errorf("EcsService updateAdmengineService failed with status: %d",
                                response.getStatusCode());
                        return Uni.createFrom().failure(new RuntimeException("EcsService updateAdmengineService failed"+response.getMessage()));
                    }
                })
                .onFailure().invoke(e -> Log.errorf("Error in startAdmEngine: %s", e.getMessage()));
    }

    private Uni<Void> waitForHealthyEngine() {
        return Uni.createFrom().voidItem()
                .onItem().delayIt().by(HEALTH_CHECK_INTERVAL)
                .onItem().transformToUni(v -> engineStartService.getHealth())
                .onItem().invoke(health -> {
                    Log.infof("EngineStartService getHealth result: %d", health.getStatusInfo().getStatusCode());
                    if (health.getStatusInfo().getStatusCode() != 200) {
                        throw new RuntimeException("Engine health check failed");
                    }
                })
                .replaceWithVoid()
                .onFailure().retry().withBackOff(HEALTH_CHECK_INTERVAL).atMost(HEALTH_CHECK_TIMEOUT.dividedBy(HEALTH_CHECK_INTERVAL));
    }

    private Uni<Void> processDashboardAndTrigger() {
        return dashboardService.getPayerAndTestTimeout("siteId") // siteId should be properly set
                .onItem().transformToUni(v -> engineStartService.getTrigger())
                .onItem().invoke(trigger -> Log.infof("EngineStartService getTrigger result: %d", trigger.getStatusInfo().getStatusCode()))
                .replaceWithVoid();
    }
    private Uni<Void> stopAdmEngine() {
        return ecsService.updateAdmEngineService(0)
                .onItem().transformToUni(response -> {
                    if (response.isSuccessful()) {
                        Log.info("EcsService updateAdmengineService (stop) successful");
                        return Uni.createFrom().voidItem();
                    } else {
                        Log.errorf("EcsService updateAdmengineService (stop) failed");
                        return Uni.createFrom().failure(new RuntimeException("EcsService updateAdmengineService (stop) failed" + response.getMessage()));
                    }
                })
                .onFailure().invoke(e -> Log.errorf("Error in stopAdmEngine: %s", e.getMessage()));
    }

    public Uni<Response> batchStart(String siteId) {
        this.siteId = siteId;
        schedule();
        return Uni.createFrom().item(Response.ok().build());
    }
}
