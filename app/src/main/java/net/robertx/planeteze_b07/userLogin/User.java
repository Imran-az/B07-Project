package net.robertx.planeteze_b07.userLogin;

public class User {

    String UserID, FirstName, LastName, eMail, Password;

    public User() {
    }

    public User(String userID, String firstName, String lastName, String eMail, String password) {
        UserID = userID;
        FirstName = firstName;
        LastName = lastName;
        this.eMail = eMail;
        Password = password;
    }

    public User(String uid, String email, String firstname, String lastname) {
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}