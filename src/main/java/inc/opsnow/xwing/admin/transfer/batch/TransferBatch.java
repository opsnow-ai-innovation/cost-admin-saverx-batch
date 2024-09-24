package inc.opsnow.xwing.admin.transfer.batch;

import inc.opsnow.xwing.admin.transfer.external.EngineStartService;
import inc.opsnow.xwing.admin.transfer.external.ecs.EcsService;
import inc.opsnow.xwing.admin.transfer.service.DashboardService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.unchecked.Unchecked;
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
    private static final Duration HEALTH_CHECK_INTERVAL = Duration.ofSeconds(10); // 10초 간격으로 체크
    private static final Duration INITIAL_WAIT = Duration.ofMinutes(2); // 초기 1분 대기
    private static final Duration POST_WAIT = Duration.ofSeconds(10); // 추가 10초 대기

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
                        item -> Log.info("TransferBatch completed successfully"),
                        error -> Log.error("TransferBatch failed", error)
                );
    }

    @Retry(maxRetries = MAX_RETRIES, delay = 30000, retryOn = Exception.class)
    Uni<Void> startTransferBatch() {
        return startAdmEngine()
                .onItem().delayIt().by(INITIAL_WAIT) // startAdmEngine 후 1분 대기
                .chain(v -> waitForHealthyEngine()) // 엔진 상태 체크
                .onItem().delayIt().by(POST_WAIT) // 정상 확인 후 10초 대기
                .chain(v -> processDashboardAndTrigger()) // 대시보드 처리 및 트리거 호출
                .onItem().delayIt().by(POST_WAIT) // 처리 후 10초 대기
                .chain(v -> stopAdmEngine()) // 엔진 중지
                .onFailure().invoke(e -> {
                    Log.errorf("Error in startTransferBatch: %s. Retrying...", e.getMessage());
                });
    }

    private Uni<Void> startAdmEngine() {
        return ecsService.updateAdmEngineService(1)
                .onItem().transformToUni(response -> {
                    if (response.isSuccessful()) {
                        Log.info("EcsService updateAdmEngineService successful");
                        return Uni.createFrom().voidItem();
                    } else {
                        Log.errorf("EcsService updateAdmEngineService failed with status: %d",
                                response.getStatusCode());
                        return Uni.createFrom().failure(new RuntimeException("EcsService updateAdmEngineService failed: " + response.getMessage()));
                    }
                })
                .onFailure().invoke(e -> Log.errorf("Error in startAdmEngine: %s", e.getMessage()));
    }

    private Uni<Void> waitForHealthyEngine() {
        return Uni.createFrom().voidItem()
                .onItem().delayIt().by(HEALTH_CHECK_INTERVAL) // 10초 대기 후 상태 체크 시작
                .chain(v -> engineStartService.getHealth())
                .onItem().invoke(Unchecked.consumer(health -> {
                    Log.infof("EngineStartService getHealth result: %d", health.getStatusInfo().getStatusCode());
                    if (health.getStatusInfo().getStatusCode() != 200) {
                        throw new RuntimeException("Engine health check failed");
                    }
                }))
                .replaceWithVoid()
                .onFailure().retry().withBackOff(HEALTH_CHECK_INTERVAL).atMost(HEALTH_CHECK_TIMEOUT.dividedBy(HEALTH_CHECK_INTERVAL)); // 최대 5분간 재시도
    }

    private Uni<Void> processDashboardAndTrigger() {
        return dashboardService.getPayerAndTestTimeout(siteId)
                .onItem().transformToUni(v -> engineStartService.getTrigger())
                .onItem().invoke(trigger -> Log.infof("EngineStartService getTrigger result: %d", trigger.getStatusInfo().getStatusCode()))
                .replaceWithVoid();
    }

    private Uni<Void> stopAdmEngine() {
        return ecsService.updateAdmEngineService(0)
                .onItem().transformToUni(response -> {
                    if (response.isSuccessful()) {
                        Log.info("EcsService updateAdmEngineService (stop) successful");
                        return Uni.createFrom().voidItem();
                    } else {
                        Log.errorf("EcsService updateAdmEngineService (stop) failed with status: %d",
                                response.getStatusCode());
                        return Uni.createFrom().failure(new RuntimeException("EcsService updateAdmEngineService (stop) failed: " + response.getMessage()));
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