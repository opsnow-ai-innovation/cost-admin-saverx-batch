package inc.opsnow.xwing.admin.transfer.resource;

import io.smallrye.mutiny.Uni;
import inc.opsnow.xwing.admin.transfer.external.ecs.EcsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;

@Path("/ecs")
public class EcsResource {

    @Inject
    EcsService ecsService;

    @POST
    @Path("/update-admengine")
    public Uni<UpdateServiceResponse> updateAdmEngineService(@QueryParam("desiredCount") int desiredCount) {
        return ecsService.updateAdmEngineService(desiredCount);
    }

}
