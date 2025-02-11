package inc.opsnow.xwing.admin.transfer.external;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "engine-start-api")
@RegisterClientHeaders
public interface EngineStartService {

    @GET
    @Path("/ai-saverx-engine/trigger")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Response> getTrigger();

    @GET
    @Path("/ai-saverx-engine/optimize")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Response> getOptimize();

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Response> getHealth();

}
