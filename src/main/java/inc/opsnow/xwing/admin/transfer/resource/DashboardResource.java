package inc.opsnow.xwing.admin.transfer.resource;

import inc.opsnow.xwing.admin.transfer.batch.TransferBatch;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {
    @Inject
    TransferBatch transferBatch;

    @GET
    @Path("/payer/{siteId}")
    public Uni<Response> getPayer(@PathParam("siteId") String siteId, @QueryParam("force") String force) {
        boolean forceBoolean = false;
        if(force != null && force.equals("true")) {
            forceBoolean = true;
        }
        return transferBatch.batchStart(siteId, forceBoolean);
    }
}
