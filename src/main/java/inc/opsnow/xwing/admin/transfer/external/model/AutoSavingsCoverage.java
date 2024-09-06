package inc.opsnow.xwing.admin.transfer.external.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class AutoSavingsCoverage {
    @Schema(description = "전체 충당률")
    private Double totalCoverage;

    @Schema(description = "고객사 구매 기준 충당률")
    private Double customerCoverage;

    @Schema(description = "OpsNow 구매 기준 충당률")
    private Double opsnowCoverage;

    @Schema(description = "OpsNow 구매 기준 적용된 약정 온디맨드 환산 금액")
    private Double opsnowOnDemandCostEquivalent;

    @Schema(description = "고객사 구매 기준 적용된 약정 온디맨드 환산 금액")
    private Double customerOnDemandCostEquivalent;

    @Schema(description = "온디맨드 사용 금액")
    private Double onDemandCost;

    @Schema(description = "전체 적용 약정 온디맨드 환산 금액")
    private Double commitmentOnDemandCostEquivalent;

    @Schema(description = "전체 합계 금액")
    private Double totalOnDemandCostEquivalent;


    public double getTotalCoverage() {
        return totalCoverage;
    }

    public void setTotalCoverage(double totalCoverage) {
        this.totalCoverage = totalCoverage;
    }

    public double getCustomerCoverage() {
        return customerCoverage;
    }

    public void setCustomerCoverage(double customerCoverage) {
        this.customerCoverage = customerCoverage;
    }

    public double getOpsnowCoverage() {
        return opsnowCoverage;
    }

    public void setOpsnowCoverage(double opsnowCoverage) {
        this.opsnowCoverage = opsnowCoverage;
    }

    public double getOpsnowOnDemandCostEquivalent() {
        return opsnowOnDemandCostEquivalent;
    }

    public void setOpsnowOnDemandCostEquivalent(double opsnowOnDemandCostEquivalent) {
        this.opsnowOnDemandCostEquivalent = opsnowOnDemandCostEquivalent;
    }

    public double getCustomerOnDemandCostEquivalent() {
        return customerOnDemandCostEquivalent;
    }

    public void setCustomerOnDemandCostEquivalent(double customerOnDemandCostEquivalent) {
        this.customerOnDemandCostEquivalent = customerOnDemandCostEquivalent;
    }

    public double getOnDemandCost() {
        return onDemandCost;
    }

    public void setOnDemandCost(double onDemandCost) {
        this.onDemandCost = onDemandCost;
    }

    public double getCommitmentOnDemandCostEquivalent() {
        return commitmentOnDemandCostEquivalent;
    }

    public void setCommitmentOnDemandCostEquivalent(double commitmentOnDemandCostEquivalent) {
        this.commitmentOnDemandCostEquivalent = commitmentOnDemandCostEquivalent;
    }

    public double getTotalOnDemandCostEquivalent() {
        return totalOnDemandCostEquivalent;
    }

    public void setTotalOnDemandCostEquivalent(double totalOnDemandCostEquivalent) {
        this.totalOnDemandCostEquivalent = totalOnDemandCostEquivalent;
    }

    @Override
    public String toString() {
        return "Coverage{" +
                "totalCoverage=" + totalCoverage +
                ", customerCoverage=" + customerCoverage +
                ", opsnowCoverage=" + opsnowCoverage +
                ", opsnowOnDemandCostEquivalent=" + opsnowOnDemandCostEquivalent +
                ", customerOnDemandCostEquivalent=" + customerOnDemandCostEquivalent +
                ", onDemandCost=" + onDemandCost +
                ", commitmentOnDemandCostEquivalent=" + commitmentOnDemandCostEquivalent +
                ", totalOnDemandCostEquivalent=" + totalOnDemandCostEquivalent +
                '}';
    }
}
