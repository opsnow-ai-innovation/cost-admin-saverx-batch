package inc.opsnow.xwing.admin.transfer.batch;

import inc.opsnow.xwing.admin.transfer.external.EngineStartService;
import inc.opsnow.xwing.admin.transfer.external.ecs.EcsService;
import inc.opsnow.xwing.admin.transfer.service.DashboardService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class TransferBatch {

    @ConfigProperty(name = "site_id", defaultValue = "OPSNOW")
    String siteId;

    @ConfigProperty(name = "batch.schedule")
    String batchSchedule;

    @Inject
    DashboardService dashboardService;

    @Inject
    EcsService ecsService;

    @RestClient
    EngineStartService engineStartService;

    private static final int MAX_RETRIES = 10;
    private static final Duration HEALTH_CHECK_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration HEALTH_CHECK_INTERVAL = Duration.ofSeconds(10); // 10초 간격으로 체크
    private static final Duration INITIAL_WAIT = Duration.ofMinutes(2); // 초기 2분 대기
    private static final Duration POST_WAIT = Duration.ofSeconds(10); // 추가 10초 대기

    // 엔진 수행 여부 체크
    // 매 30초마다 50분간 엔진 상태 체크
    private static final Duration CHECK_INTERVAL = Duration.ofSeconds(30);
    private static final Duration MAX_CHECK_DURATION = Duration.ofMinutes(50); // 최대 체크 시간 정의 (예: 50분)


    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    @Scheduled(cron = "{batch.schedule}")
    void schedule(ScheduledExecution execution) {
        if (isRunning.compareAndSet(false, true)) {
            Log.info("Schedule start requested");
            startTransferBatchAsync();
        } else {
            Log.info("TransferBatch is already running. Skipping this scheduled execution.");
        }
    }

    private void startTransferBatchAsync() {
        startTransferBatch()
                .subscribe().with(
                        item -> {
                            Log.info("TransferBatch completed successfully");
                            isRunning.set(false);
                        },
                        error -> {
                            Log.error("TransferBatch failed", error);
                            isRunning.set(false);
                        }
                );
    }

    @Retry(maxRetries = MAX_RETRIES, delay = 30000, retryOn = Exception.class)
    Uni<Void> startTransferBatch() {
        return startAdmEngine()
                .onItem().delayIt().by(INITIAL_WAIT) // startAdmEngine 후 2분 대기
                .chain(v -> waitForHealthyEngine()) // 엔진 상태 체크
                .onItem().delayIt().by(POST_WAIT) // 정상 확인 후 10초 대기
                .chain(v -> processDashboardAndTrigger()) // 대시보드 처리 및 트리거 호출
                .onItem().delayIt().by(INITIAL_WAIT) // 2분 대기
                .chain(expectedCount -> getAccountInfoCommitCount(siteId, expectedCount) // 매 30초마다 50분간 commit count==expectedCount 체크
                        .onItem().delayIt().by(POST_WAIT) // 처리 후 10초 대기
                        .chain(v -> processOptimize()) // 최적화 처리
                        .onItem().delayIt().by(POST_WAIT) // 처리 후 10초 대기
                        .chain(v -> tryGetAccountInfoOptimCommitCount(siteId, expectedCount))) // Optim commit count 체크
                .chain(v -> stopAdmEngine()) // 엔진 중지
                .onItem().delayIt().by(POST_WAIT) // 처리 후 10초 대기
                .chain(v -> dashboardService.updateStatus(siteId)) // 상태 업데이트
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

    private Uni<Integer> processDashboardAndTrigger() {
        return dashboardService.getPayerAndTestTimeout(siteId)
                .onItem().transformToUni(v -> engineStartService.getTrigger()
                        .chain(response -> {
                            if (response.getStatus() == 200) {
                                Log.info("EngineStartService getTrigger successful");
                                return Uni.createFrom().item(v);
                            } else {
                                Log.errorf("EngineStartService getTrigger failed with status: %d", response.getStatus());
                                return Uni.createFrom().failure(new RuntimeException("EngineStartService getTrigger failed"));
                            }
                        }))
                ;
    }

    private Uni<Void> processOptimize() {
        return engineStartService.getOptimize()
                        .chain(response -> {
                            if (response.getStatus() == 200) {
                                Log.info("EngineStartService getOptimize successful");
                                return Uni.createFrom().voidItem();
                            } else {
                                Log.errorf("EngineStartService getOptimize failed with status: %d", response.getStatus());
                                return Uni.createFrom().failure(new RuntimeException("EngineStartService getOptimize failed"));
                            }
                        })
                ;
    }

    private Uni<Void> getAccountInfoCommitCount(String siteId, Integer expectedCount) {
        return Uni.createFrom().voidItem()
                .chain(() -> tryGetAccountInfoNormalCommitCount(siteId, expectedCount)) // 실제 체크 로직으로 체이닝
                .onFailure().retry().withBackOff(CHECK_INTERVAL).atMost(MAX_CHECK_DURATION.dividedBy(CHECK_INTERVAL)); // 10초 간격으로 최대 5분간 재시도
    }

    private Uni<Void> tryGetAccountInfoNormalCommitCount(String siteId, Integer expectedCount) {
        return dashboardService.getAccountInfoNormalCommitCount(siteId)
                .chain(count -> {
                    Log.infof("AccountInfo Normal commit count: %d, expected: %d", count, expectedCount);
                    Log.infof("count class name: %s", count.getClass().getName());
                    Log.infof("expectedCount class name: %s", expectedCount.getClass().getName());

                    if (Objects.equals(count, expectedCount)) {
                        Log.infof("AccountInfo Normal commit count matched: %d", count);
                        return Uni.createFrom().voidItem(); // count가 일치하면 성공 처리
                    } else {
                        Log.infof("AccountInfo Normal commit count: %d, expected: %d. Retrying...", count, expectedCount);
                        return Uni.createFrom().failure(new RuntimeException("AccountInfo Normal commit count mismatch")); // 일치하지 않으면 실패 처리하여 재시도
                    }
                })
                .onFailure().invoke(e -> Log.warn("Retrying Normal commit count check..."));
    }

    private Uni<Void> tryGetAccountInfoOptimCommitCount(String siteId, Integer expectedCount) {
        return dashboardService.getAccountInfoOptimCommitCount(siteId)
                .chain(count -> {
                    Log.infof("AccountInfo Optim commit count: %d, expected: %d", count, expectedCount);
                    Log.infof("count class name: %s", count.getClass().getName());
                    Log.infof("expectedCount class name: %s", expectedCount.getClass().getName());

                    if (Objects.equals(count, expectedCount)) {
                        Log.infof("AccountInfo Optim commit count matched: %d", count);
                        return Uni.createFrom().voidItem(); // count가 일치하면 성공 처리
                    } else {
                        Log.infof("AccountInfo Optim commit count: %d, expected: %d. Retrying...", count, expectedCount);
                        return Uni.createFrom().failure(new RuntimeException("AccountInfo Optim commit count mismatch")); // 일치하지 않으면 실패 처리하여 재시도
                    }
                })
                .onFailure().invoke(e -> Log.warn("Retrying Optim commit count check..."));
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

    public Uni<Response> batchStart(String siteId, boolean force) {

        if (force) {
            Log.info("Force option is enabled. Starting batch process.");
            startTransferBatchAsync();
            return Uni.createFrom().item(Response.ok()
                    .entity("Batch process started successfully").build());
        }

        if (isRunning.get()) {
            Log.info("TransferBatch is already running. Rejecting this request.");
            return Uni.createFrom().item(Response.status(Response.Status.CONFLICT)
                    .entity("Batch is already running").build());
        }

        if (isRunning.compareAndSet(false, true)) {
            this.siteId = siteId;
            Log.info("HTTP batchStart requested. Starting batch process.");

            // 배치 프로세스를 백그라운드에서 시작
            startTransferBatchAsync();

            return Uni.createFrom().item(Response.ok()
                    .entity("Batch process started successfully").build());
        } else {
            // 극히 드문 경우지만, 다른 스레드가 먼저 실행을 시작했을 경우
            Log.info("TransferBatch was just started by another request. Rejecting this one.");
            return Uni.createFrom().item(Response.status(Response.Status.CONFLICT)
                    .entity("Batch was just started by another request").build());
        }
    }
}