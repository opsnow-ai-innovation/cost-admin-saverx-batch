package inc.opsnow.xwing.admin.transfer.external.dto;

import inc.opsnow.xwing.admin.transfer.external.model.AwsPayerAccountSummary;
import inc.opsnow.xwing.admin.transfer.external.model.Error;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;

public class GetAwsPayerAccountSummaryResponse {
    public String status;
    public Error error;
    @Schema(description = "Site Code", example = "OPSNOW")
    public String siteCode;
    @Schema(description = "Payer Account ID")
    public String payerAccountId;
    @Schema(description = "Payer Account ID")
    public String lastUseDate;
    @Schema(description = "약정 데이터 요약")
    public Map<String, AwsPayerAccountSummary> data;

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

    public Map<String, AwsPayerAccountSummary> getData() {
        return data;
    }

    public void setData(Map<String, AwsPayerAccountSummary> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CostAnalysis{" +
                "status='" + status + '\'' +
                ", siteCode='" + siteCode + '\'' +
                ", payerAccountId='" + payerAccountId + '\'' +
                ", lastUseDate=" + lastUseDate +
                ", data=" + data +
                '}';
    }
}
