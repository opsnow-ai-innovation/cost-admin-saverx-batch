package inc.opsnow.xwing.admin.transfer.external;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/ai-saverx-engine")
@RegisterRestClient(configKey = "engine-start-api")
@RegisterClientHeaders
public interface EngineStartService {

    @GET
    @Path("/trigger")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Response> getTrigger();

}
