package inc.opsnow.xwing.admin.transfer.batch;

import inc.opsnow.xwing.admin.transfer.external.EngineStartService;
import inc.opsnow.xwing.admin.transfer.service.DashboardService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class TransferBatch {

    @ConfigProperty(name = "site_id", defaultValue = "OPSNOW")
    String siteId;

    @Inject
    DashboardService dashboardService;

    @RestClient
    EngineStartService engineStartService;

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
        dashboardService.getPayerAndTestTimeout(siteId).subscribe().with(result -> {
            engineStartService.getTrigger().subscribe().with(isStart -> {
                Log.infof("EngineStartService getTrigger result: " + isStart.getStatusInfo().getStatusCode());
            });
            Log.infof("TransferBatch schedule end");
        });
    }

    public Uni<Response> batchStart(String siteId) {
        this.siteId = siteId;
        schedule();
        return Uni.createFrom().item(Response.ok().build());
    }
}
