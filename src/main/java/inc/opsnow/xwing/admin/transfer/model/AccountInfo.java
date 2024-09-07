package inc.opsnow.xwing.admin.transfer.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
public class AccountInfo {
    private Long id;
    private String payerName;
    private String siteId;
    private String payerId;
    private Double targetCov;
    private String status;
    private String lastCollectionDay;
    private Double p1UtilPercent;
    private Double p1CovPercent;
    private Double p2UtilPercent;
    private Double p2CovPercent;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public Double getTargetCov() {
        return targetCov;
    }

    public void setTargetCov(Double targetCov) {
        this.targetCov = targetCov;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastCollectionDay() {
        return lastCollectionDay;
    }

    public void setLastCollectionDay(String lastCollectionDay) {
        this.lastCollectionDay = lastCollectionDay;
    }

    public Double getP1UtilPercent() {
        return p1UtilPercent;
    }

    public void setP1UtilPercent(Double p1UtilPercent) {
        this.p1UtilPercent = p1UtilPercent;
    }

    public Double getP1CovPercent() {
        return p1CovPercent;
    }

    public void setP1CovPercent(Double p1CovPercent) {
        this.p1CovPercent = p1CovPercent;
    }

    public Double getP2UtilPercent() {
        return p2UtilPercent;
    }

    public void setP2UtilPercent(Double p2UtilPercent) {
        this.p2UtilPercent = p2UtilPercent;
    }

    public Double getP2CovPercent() {
        return p2CovPercent;
    }

    public void setP2CovPercent(Double p2CovPercent) {
        this.p2CovPercent = p2CovPercent;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "XAccountInfo{" +
                "id=" + id +
                ", payerName='" + payerName + '\'' +
                ", siteId='" + siteId + '\'' +
                ", payerId='" + payerId + '\'' +
                ", targetCov=" + targetCov +
                ", status='" + status + '\'' +
                ", lastCollectionDay='" + lastCollectionDay + '\'' +
                ", p1UtilPercent=" + p1UtilPercent +
                ", p1CovPercent=" + p1CovPercent +
                ", p2UtilPercent=" + p2UtilPercent +
                ", p2CovPercent=" + p2CovPercent +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
