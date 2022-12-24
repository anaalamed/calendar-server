package calendar.entities.DTO;


public class LoginDataDTO {
    private Integer userId;
    private String token, name;

    public LoginDataDTO(Integer userId, String token, String name) {
        this.userId = userId;
        this.token = token;
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
