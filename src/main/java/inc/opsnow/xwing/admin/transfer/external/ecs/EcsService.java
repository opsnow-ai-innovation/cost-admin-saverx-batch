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

    public UpdateServiceResponse updateServiceDesiredCount(String cluster, String service, int desiredCount) {
        UpdateServiceRequest updateServiceRequest = UpdateServiceRequest.builder()
                .cluster(cluster)
                .service(service)
                .desiredCount(desiredCount)
                .build();

        return ecsClient.updateService(updateServiceRequest);
    }

    public Uni<UpdateServiceResponse> updateAdmEngineService(int desiredCount) {

        Log.infof("Update service desired count. cluster: %s, service: %s, desiredCount: %s", cluster, service, desiredCount);

        UpdateServiceResponse response = updateServiceDesiredCount(cluster, service, desiredCount);
        System.out.println(response);
        return Uni.createFrom().item(response);
    }
}
