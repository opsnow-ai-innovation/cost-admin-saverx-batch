package inc.opsnow.xwing.admin.transfer.external.ecs;

public class ServiceUpdateResult {
    private boolean isSuccessful;
    private String message;
    private int statusCode;

    public ServiceUpdateResult(boolean isSuccessful, String message, int statusCode) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    @Override
    public String toString() {
        return "ServiceUpdateResult{" +
                "isSuccessful=" + isSuccessful +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
