package inc.opsnow.xwing.admin.transfer.external.ecs;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;

@ApplicationScoped
public class EcsService {

    @Inject
    EcsClient ecsClient;



    @ConfigProperty(name = "engine.cluster")
    String cluster;
    @ConfigProperty(name = "engine.service")
    String service;

    public ServiceUpdateResult updateServiceDesiredCount(String cluster, String service, int desiredCount) {
        UpdateServiceRequest updateServiceRequest = UpdateServiceRequest.builder()
                .cluster(cluster)
                .service(service)
                .desiredCount(desiredCount)
                .build();

        UpdateServiceResponse response = ecsClient.updateService(updateServiceRequest);

        return new ServiceUpdateResult(
                response.sdkHttpResponse().isSuccessful(),
                response.sdkHttpResponse().statusText().orElse("No status text"),
                response.sdkHttpResponse().statusCode()
        );
    }

    public Uni<ServiceUpdateResult> updateAdmEngineService(int desiredCount) {
        Log.infof("Update service desired count. cluster: %s, service: %s, desiredCount: %s", cluster, service, desiredCount);

        ServiceUpdateResult result = updateServiceDesiredCount(cluster, service, desiredCount);
        Log.infof("Service update result: %s", result);
        return Uni.createFrom().item(result);
    }
}
