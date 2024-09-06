package inc.opsnow.xwing.admin.transfer.external.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class AutoSavingsUtilization {

    @Schema(description = "전체 사용률")
    private Double totalUtilization;

    @Schema(description = "전체 약정 금액")
    private Double totalCommitmentCost;

    @Schema(description = "전체 사용된 약정 금액")
    private Double totalUsedCost;

    @Schema(description = "전체 사용된 약정의 온디맨드 환산 금액")
    private Double totalOnDemandCostEquivalent;

    @Schema(description = "전체 절감액")
    private Double totalNetSavings;

    @Schema(description = "전체 절감률")
    private Double totalNetSavingsRate;

    @Schema(description = "고객 구매 약정 사용률")
    private Double customerUtilization;

    @Schema(description = "고객 구매 약정 금액")
    private Double customerCommitmentCost;

    @Schema(description = "고객 구매 약정의 사용된 약정 금액")
    private Double customerUsedCost;

    @Schema(description = "고객 구매 약정의 사용된 약정 온디맨드 환산 금액")
    private Double customerOnDemandCostEquivalent;

    @Schema(description = "고객 구매 약정의 절감액")
    private Double customerNetSavings;

    @Schema(description = "고객 구매 약정의 절감률")
    private Double customerNetSavingsRate;

    @Schema(description = "OpsNow에서 적용 약정 사용률")
    private Double opsnowUtilization;

    @Schema(description = "OpsNow 적용 약정 금액")
    private Double opsnowCommitmentCost;

    @Schema(description = "OpsNow 적용 약정의 사용된 약정 금액")
    private Double opsnowUsedCost;

    @Schema(description = "OpsNow 적용 약정의 사용된 약정 온디맨드 환산 금액")
    private Double opsnowOnDemandCostEquivalent;

    @Schema(description = "OpsNow 적용 약정의 절감액")
    private Double opsnowNetSavings;

    @Schema(description = "OpsNow 적용 약정의 절감률")
    private Double opsnowNetSavingsRate;

    public double getTotalUtilization() {
        return totalUtilization;
    }

    public void setTotalUtilization(double totalUtilization) {
        this.totalUtilization = totalUtilization;
    }

    public double getTotalCommitmentCost() {
        return totalCommitmentCost;
    }

    public void setTotalCommitmentCost(double totalCommitmentCost) {
        this.totalCommitmentCost = totalCommitmentCost;
    }

    public double getTotalUsedCost() {
        return totalUsedCost;
    }

    public void setTotalUsedCost(double totalUsedCost) {
        this.totalUsedCost = totalUsedCost;
    }

    public double getTotalOnDemandCostEquivalent() {
        return totalOnDemandCostEquivalent;
    }

    public void setTotalOnDemandCostEquivalent(double totalOnDemandCostEquivalent) {
        this.totalOnDemandCostEquivalent = totalOnDemandCostEquivalent;
    }

    public double getTotalNetSavings() {
        return totalNetSavings;
    }

    public void setTotalNetSavings(double totalNetSavings) {
        this.totalNetSavings = totalNetSavings;
    }

    public double getTotalNetSavingsRate() {
        return totalNetSavingsRate;
    }

    public void setTotalNetSavingsRate(double totalNetSavingsRate) {
        this.totalNetSavingsRate = totalNetSavingsRate;
    }

    public double getCustomerUtilization() {
        return customerUtilization;
    }

    public void setCustomerUtilization(double customerUtilization) {
        this.customerUtilization = customerUtilization;
    }

    public double getCustomerCommitmentCost() {
        return customerCommitmentCost;
    }

    public void setCustomerCommitmentCost(double customerCommitmentCost) {
        this.customerCommitmentCost = customerCommitmentCost;
    }

    public double getCustomerUsedCost() {
        return customerUsedCost;
    }

    public void setCustomerUsedCost(double customerUsedCost) {
        this.customerUsedCost = customerUsedCost;
    }

    public double getCustomerOnDemandCostEquivalent() {
        return customerOnDemandCostEquivalent;
    }

    public void setCustomerOnDemandCostEquivalent(double customerOnDemandCostEquivalent) {
        this.customerOnDemandCostEquivalent = customerOnDemandCostEquivalent;
    }

    public double getCustomerNetSavings() {
        return customerNetSavings;
    }

    public void setCustomerNetSavings(double customerNetSavings) {
        this.customerNetSavings = customerNetSavings;
    }

    public double getCustomerNetSavingsRate() {
        return customerNetSavingsRate;
    }

    public void setCustomerNetSavingsRate(double customerNetSavingsRate) {
        this.customerNetSavingsRate = customerNetSavingsRate;
    }

    public double getOpsnowUtilization() {
        return opsnowUtilization;
    }

    public void setOpsnowUtilization(double opsnowUtilization) {
        this.opsnowUtilization = opsnowUtilization;
    }

    public double getOpsnowCommitmentCost() {
        return opsnowCommitmentCost;
    }

    public void setOpsnowCommitmentCost(double opsnowCommitmentCost) {
        this.opsnowCommitmentCost = opsnowCommitmentCost;
    }

    public double getOpsnowUsedCost() {
        return opsnowUsedCost;
    }

    public void setOpsnowUsedCost(double opsnowUsedCost) {
        this.opsnowUsedCost = opsnowUsedCost;
    }

    public double getOpsnowOnDemandCostEquivalent() {
        return opsnowOnDemandCostEquivalent;
    }

    public void setOpsnowOnDemandCostEquivalent(double opsnowOnDemandCostEquivalent) {
        this.opsnowOnDemandCostEquivalent = opsnowOnDemandCostEquivalent;
    }

    public double getOpsnowNetSavings() {
        return opsnowNetSavings;
    }

    public void setOpsnowNetSavings(double opsnowNetSavings) {
        this.opsnowNetSavings = opsnowNetSavings;
    }

    public double getOpsnowNetSavingsRate() {
        return opsnowNetSavingsRate;
    }

    public void setOpsnowNetSavingsRate(double opsnowNetSavingsRate) {
        this.opsnowNetSavingsRate = opsnowNetSavingsRate;
    }

    @Override
    public String toString() {
        return "Utilization{" +
                "totalUtilization=" + totalUtilization +
                ", totalCommitmentCost=" + totalCommitmentCost +
                ", totalUsedCost=" + totalUsedCost +
                ", totalOnDemandCostEquivalent=" + totalOnDemandCostEquivalent +
                ", totalNetSavings=" + totalNetSavings +
                ", totalNetSavingsRate=" + totalNetSavingsRate +
                ", customerUtilization=" + customerUtilization +
                ", customerCommitmentCost=" + customerCommitmentCost +
                ", customerUsedCost=" + customerUsedCost +
                ", customerOnDemandCostEquivalent=" + customerOnDemandCostEquivalent +
                ", customerNetSavings=" + customerNetSavings +
                ", customerNetSavingsRate=" + customerNetSavingsRate +
                ", opsnowUtilization=" + opsnowUtilization +
                ", opsnowCommitmentCost=" + opsnowCommitmentCost +
                ", opsnowUsedCost=" + opsnowUsedCost +
                ", opsnowOnDemandCostEquivalent=" + opsnowOnDemandCostEquivalent +
                ", opsnowNetSavings=" + opsnowNetSavings +
                ", opsnowNetSavingsRate=" + opsnowNetSavingsRate +
                '}';
    }
}
