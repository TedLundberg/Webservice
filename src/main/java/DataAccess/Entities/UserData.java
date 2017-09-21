package DataAccess.Entities;

public class UserData {
    private String username;
    private String pwd;
    private String cookieCode;
    private int attempts;

    public String getUserName() {
        return username;
    }
    public String getPwd() {
        return pwd;
    }
    public String getCookieCode() {
        return cookieCode;
    }
    public int getAttempts() {
        return attempts;
    }

    
    public void setUserName(String username) {
        this.username = username;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public void setCookieCode(String cookieCode) {
        this.cookieCode = cookieCode;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

}
