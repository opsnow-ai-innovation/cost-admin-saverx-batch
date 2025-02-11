package inc.opsnow.xwing.admin.transfer.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@RegisterForReflection
public class AccountInfo {

    private Long id;
    private String payerName;
    private String siteId;
    private String cmpnId;
    private String cmpnNm;
    private String payerId;
    private Double targetCov;
    private String fixYn;
    private String status;
    private String optimStatus;
    private String lastCollectionDay;
    private Double latestUtilPercent;
    private Double latestCovPercent;
    private Double avgUtilPercent;
    private Double avgCovPercent;
    private Double p1UtilPercent;
    private Double p1CovPercent;
    private Double p2UtilPercent;
    private Double p2CovPercent;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;



    // BigDecimal을 반환하는 함수
    BigDecimal truncateToBigDecimal(double number, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(String.valueOf(number));
        return bd.setScale(decimalPlaces, RoundingMode.DOWN);
    }

    // double을 반환하는 함수
    double truncateToDouble(double number, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(String.valueOf(number));
        return bd.setScale(decimalPlaces, RoundingMode.DOWN).doubleValue();
    }

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

    public String getCmpnId() {
        return cmpnId;
    }

    public void setCmpnId(String cmpnId) {
        this.cmpnId = cmpnId;
    }

    public String getCmpnNm() {
        return cmpnNm;
    }

    public void setCmpnNm(String cmpnNm) {
        this.cmpnNm = cmpnNm;
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

    public Double getLatestUtilPercent() {
        return latestUtilPercent;
    }

    public void setLatestUtilPercent(Double latestUtilPercent) {
        this.latestUtilPercent = truncateToDouble(latestUtilPercent,5);
    }

    public Double getLatestCovPercent() {
        return latestCovPercent;
    }

    public void setLatestCovPercent(Double latestCovPercent) {
        this.latestCovPercent = truncateToDouble(latestCovPercent,5);
    }

    public Double getAvgUtilPercent() {
        return avgUtilPercent;
    }

    public void setAvgUtilPercent(Double avgUtilPercent) {
        this.avgUtilPercent = truncateToDouble(avgUtilPercent,5);
    }

    public Double getAvgCovPercent() {
        return avgCovPercent;
    }

    public void setAvgCovPercent(Double avgCovPercent) {
        this.avgCovPercent = truncateToDouble(avgCovPercent,5);
    }

    public String getFixYn() {
        return fixYn;
    }

    public void setFixYn(String fixYn) {
        this.fixYn = fixYn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOptimStatus() {
        return optimStatus;
    }

    public void setOptimStatus(String optimStatus) {
        this.optimStatus = optimStatus;
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
        this.p1UtilPercent = truncateToDouble(p1UtilPercent,5);
    }

    public Double getP1CovPercent() {
        return p1CovPercent;
    }

    public void setP1CovPercent(Double p1CovPercent) {
        this.p1CovPercent = truncateToDouble(p1CovPercent,5);
    }

    public Double getP2UtilPercent() {
        return p2UtilPercent;
    }

    public void setP2UtilPercent(Double p2UtilPercent) {
        this.p2UtilPercent = truncateToDouble(p2UtilPercent,5);
    }

    public Double getP2CovPercent() {
        return p2CovPercent;
    }

    public void setP2CovPercent(Double p2CovPercent) {
        this.p2CovPercent = truncateToDouble(p2CovPercent,5);
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
        return "AccountInfo{" +
                "id=" + id +
                ", payerName='" + payerName + '\'' +
                ", siteId='" + siteId + '\'' +
                ", cmpnId='" + cmpnId + '\'' +
                ", cmpnNm='" + cmpnNm + '\'' +
                ", payerId='" + payerId + '\'' +
                ", targetCov=" + targetCov +
                ", fixYn='" + fixYn + '\'' +
                ", status='" + status + '\'' +
                ", optimStatus='" + optimStatus + '\'' +
                ", lastCollectionDay='" + lastCollectionDay + '\'' +
                ", latestUtilPercent=" + latestUtilPercent +
                ", latestCovPercent=" + latestCovPercent +
                ", avgUtilPercent=" + avgUtilPercent +
                ", avgCovPercent=" + avgCovPercent +
                ", p1UtilPercent=" + p1UtilPercent +
                ", p1CovPercent=" + p1CovPercent +
                ", p2UtilPercent=" + p2UtilPercent +
                ", p2CovPercent=" + p2CovPercent +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
