package inc.opsnow.xwing.admin.transfer.resource;

import inc.opsnow.xwing.admin.transfer.service.DashboardService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {
    @Inject
    DashboardService dashboardService;

    @GET
    @Path("/payer/{siteId}")
    public Uni<Integer> getPayer(@PathParam("siteId") String siteId) {
        return dashboardService.getPayerAndTestTimeout(siteId);
    }
}
