package calendar.controller.request;

import calendar.entities.enums.ProviderType;

public class UserRequest {
    private String email, name, password;

//    private ProviderType provider;


    public UserRequest() {}

    public UserRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
//        this.provider = provider;
    }

    public UserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
