package calendar.entities.DTO;


import calendar.entities.enums.City;

public class LoginDataDTO {
    private Integer userId;
    private String token, name;
    private City city;

    public LoginDataDTO(Integer userId, String token, String name, City city) {
        this.userId = userId;
        this.token = token;
        this.name = name;
        this.city = city;
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


}
