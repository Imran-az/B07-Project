package net.robertx.planeteze_b07.userLogin;

/**
 * Represents a user in the system.
 * This class contains user details such as user ID, first name, last name, email, and password.
 */
public class User {

    /**
     * The user's unique identifier.
     */
    String UserID;

    /**
     * The user's first name.
     */
    String FirstName;

    /**
     * The user's last name.
     */
    String LastName;

    /**
     * The user's email address.
     */
    String eMail;

    /**
     * The user's password.
     */
    String Password;

    /**
     * Default constructor for the User class.
     * Initializes a new instance of the User class with default values.
     */
    public User() {
    }

    public User(String userID, String firstName, String lastName, String eMail, String password) {
        UserID = userID;
        FirstName = firstName;
        LastName = lastName;
        this.eMail = eMail;
        Password = password;
    }

    /**
     * Constructs a new User with the specified user ID, email, first name, and last name.
     *
     * @param uid       The user's unique identifier.
     * @param email     The user's email address.
     * @param firstname The user's first name.
     * @param lastname  The user's last name.
     */
    public User(String uid, String email, String firstname, String lastname) {
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return The user's unique identifier.
     */
    public String getUserID() {
        return UserID;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param userID The user's unique identifier.
     */
    public void setUserID(String userID) {
        UserID = userID;
    }

    /**
     * Gets the user's first name.
     *
     * @return The user's first name.
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The user's first name.
     */
    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The user's last name.
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The user's last name.
     */
    public void setLastName(String lastName) {
        LastName = lastName;
    }

    /**
     * Gets the user's email address.
     *
     * @return The user's email address.
     */
    public String geteMail() {
        return eMail;
    }

    /**
     * Sets the user's email address.
     *
     * @param eMail The user's email address.
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return Password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The user's password.
     */
    public void setPassword(String password) {
        Password = password;
    }
}