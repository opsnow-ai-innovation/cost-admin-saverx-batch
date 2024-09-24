package inc.opsnow.xwing.admin.transfer.external.ecs;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;

@ApplicationScoped
public class EcsProducer {

    @ConfigProperty(name = "aws.region")
    String region;

    @Produces
    @DefaultBean
    @ApplicationScoped
    public EcsClient createEcsClient() {
        return EcsClient.builder().region(Region.of(region)).credentialsProvider(DefaultCredentialsProvider.create()).build();
    }
}