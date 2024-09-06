package inc.opsnow.xwing.admin.transfer.external.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
    @JsonProperty("Code")
    public Long code;
    @JsonProperty("Message")
    public String message;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
