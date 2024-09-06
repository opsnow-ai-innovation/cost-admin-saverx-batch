package inc.opsnow.xwing.admin.transfer.external;

import inc.opsnow.xwing.admin.transfer.external.dto.GetAwsPayerAccountSummaryResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/admin")
@RegisterRestClient(configKey = "aws-payer-account-summary-service")
@RegisterClientHeaders
public interface AwsPayerAccountSummaryService {

    @GET
    @Path("/payer-account/savings-plans/summary")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<GetAwsPayerAccountSummaryResponse> getByIdAsync(@QueryParam("siteCode") String siteCode,  // mandatory
                                                        @QueryParam("payerAccountId") String payerAccountId,  // mandatory
                                                        @QueryParam("analyzeTerm") Integer analyzeTerm    //  "분석 기간 (day)", mandatory
    );

    @GET
    @Path("/payer-account/savings-plans/summary")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<GetAwsPayerAccountSummaryResponse> getByIdAsync(@QueryParam("siteCode") String siteCode,  // mandatory
                                                        @QueryParam("payerAccountId") String payerAccountId,  // mandatory
                                                        @QueryParam("analyzeTerm") Integer analyzeTerm,    //  "분석 기간 (day)", mandatory
                                                        @QueryParam("periodCount") Integer periodCount   // "분석 기간 기준 구간 조회 횟수, optional, default 2"
    );
}
