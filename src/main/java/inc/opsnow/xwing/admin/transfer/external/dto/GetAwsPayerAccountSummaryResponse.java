package inc.opsnow.xwing.admin.transfer.external.dto;

import inc.opsnow.xwing.admin.transfer.external.model.AwsPayerAccountSummary;
import inc.opsnow.xwing.admin.transfer.external.model.Error;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;

public class GetAwsPayerAccountSummaryResponse {
    private String status;
    private Error error;
    @Schema(description = "Site Code", example = "OPSNOW")
    private String siteCode;
    @Schema(description = "Payer Account ID")
    private String payerAccountId;
    @Schema(description = "최종 유효 수집일")
    private String lastUseDate;
    @Schema(description = "최종 유효 수집일 데이터")
    private AwsPayerAccountSummary latest;
    @Schema(description = "약정 데이터 요약")
    private Map<String, AwsPayerAccountSummary> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getPayerAccountId() {
        return payerAccountId;
    }

    public void setPayerAccountId(String payerAccountId) {
        this.payerAccountId = payerAccountId;
    }

    public String getLastUseDate() {
        return lastUseDate;
    }

    public void setLastUseDate(String lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public AwsPayerAccountSummary getLatest() {
        return latest;
    }

    public void setLatest(AwsPayerAccountSummary latest) {
        this.latest = latest;
    }

    public Map<String, AwsPayerAccountSummary> getData() {
        return data;
    }

    public void setData(Map<String, AwsPayerAccountSummary> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GetAwsPayerAccountSummaryResponse{" +
                "status='" + status + '\'' +
                ", error=" + error +
                ", siteCode='" + siteCode + '\'' +
                ", payerAccountId='" + payerAccountId + '\'' +
                ", lastUseDate='" + lastUseDate + '\'' +
                ", latest=" + latest +
                ", data=" + data +
                '}';
    }
}
