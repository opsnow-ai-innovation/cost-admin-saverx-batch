package inc.opsnow.xwing.admin.transfer.model;

import inc.opsnow.xwing.admin.common.repository.Column;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AwsSitePayer {
    @Column("SITE_ID")
    String siteId;
    @Column("PAYR_ACC_ID")
    String payerAccId;
    @Column("ALIAS")
    String alias;
    @Column("TARGET_COV")
    double targetCov;
    @Column("FIX_YN")
    String fixYn;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPayerAccId() {
        return payerAccId;
    }

    public void setPayerAccId(String payerAccId) {
        this.payerAccId = payerAccId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public double getTargetCov() {
        return targetCov;
    }

    public void setTargetCov(double targetCov) {
        this.targetCov = targetCov;
    }

    public String getFixYn() {
        return fixYn;
    }

    public void setFixYn(String fixYn) {
        this.fixYn = fixYn;
    }

    @Override
    public String toString() {
        return "AwsSitePayer{" +
                "siteId='" + siteId + '\'' +
                ", payerAccId='" + payerAccId + '\'' +
                ", alias='" + alias + '\'' +
                ", targetCov=" + targetCov +
                ", fixYn='" + fixYn + '\'' +
                '}';
    }
}
