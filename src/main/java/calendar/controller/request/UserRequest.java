package calendar.controller.request;

public class UserRequest {
    private String email, name, password;

    public UserRequest() {
    }

    public UserRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;

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
