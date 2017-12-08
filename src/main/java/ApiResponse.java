/**
 * @author
 */
public class ApiResponse {
    private Integer status;

    private String body;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", body='" + body + '\'' +
                '}';
    }
}
