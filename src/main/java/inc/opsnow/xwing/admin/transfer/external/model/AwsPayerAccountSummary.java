package inc.opsnow.xwing.admin.transfer.external.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class AwsPayerAccountSummary {
    @Schema(description = "조회 시작일")
    private String startDate;

    @Schema(description = "조회 종료일")
    private String endDate;

    @Schema(description = "사용률")
    private AutoSavingsUtilization utilization;

    @Schema(description = "충당률")
    private AutoSavingsCoverage coverage;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public AutoSavingsUtilization getUtilization() {
        return utilization;
    }

    public void setUtilization(AutoSavingsUtilization autoSavingsUtilization) {
        this.utilization = autoSavingsUtilization;
    }

    public AutoSavingsCoverage getCoverage() {
        return coverage;
    }

    public void setCoverage(AutoSavingsCoverage autoSavingsCoverage) {
        this.coverage = autoSavingsCoverage;
    }

    @Override
    public String toString() {
        return "PeriodData{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", utilization=" + utilization +
                ", coverage=" + coverage +
                '}';
    }
}
