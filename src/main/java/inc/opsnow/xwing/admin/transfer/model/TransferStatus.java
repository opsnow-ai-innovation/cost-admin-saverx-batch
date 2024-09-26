package inc.opsnow.xwing.admin.transfer.model;

import inc.opsnow.xwing.admin.common.repository.Column;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TransferStatus {

    @Column("SEND_PAYER_ID")
    String sendPayerId;
    @Column("RECV_PAYER_ID")
    String recvPayerId;
    @Column("LNKD_ACC_ID")
    String lnkdAccId;
    @Column("RESULT")
    String result;

    public String getSendPayerId() {
        return sendPayerId;
    }

    public void setSendPayerId(String sendPayerId) {
        this.sendPayerId = sendPayerId;
    }

    public String getRecvPayerId() {
        return recvPayerId;
    }

    public void setRecvPayerId(String recvPayerId) {
        this.recvPayerId = recvPayerId;
    }

    public String getLnkdAccId() {
        return lnkdAccId;
    }

    public void setLnkdAccId(String lnkdAccId) {
        this.lnkdAccId = lnkdAccId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TransferStatus{" +
                "sendPayerId='" + sendPayerId + '\'' +
                ", recvPayerId='" + recvPayerId + '\'' +
                ", lnkdAccId='" + lnkdAccId + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
