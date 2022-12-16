package calendar.entities.DTO;


public class LoginDataDTO {
    private Integer userId;
    private String token;

    public LoginDataDTO(Integer userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
