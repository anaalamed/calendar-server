package calendar.entities.DTO;

import calendar.entities.enums.City;

public class LoginDataDTO {
    private Integer userId;
    private String token, name, email;
    private City city;

    public LoginDataDTO(Integer userId, String token, String name, City city, String email) {
        this.userId = userId;
        this.token = token;
        this.name = name;
        this.city = city;
        this.email = email;
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

    public City getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
