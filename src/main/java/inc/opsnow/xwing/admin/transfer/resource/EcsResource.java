package inc.opsnow.xwing.admin.transfer.resource;

import inc.opsnow.xwing.admin.transfer.external.ecs.EcsService;
import inc.opsnow.xwing.admin.transfer.external.ecs.ServiceUpdateResult;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/ecs")
public class EcsResource {

    @Inject
    EcsService ecsService;

    @POST
    @Path("/update-admengine")
    public Uni<ServiceUpdateResult> updateAdmEngineService(@QueryParam("desiredCount") int desiredCount) {
        return ecsService.updateAdmEngineService(desiredCount);
    }

}
