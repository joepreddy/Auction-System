/**
 * Created by Joe on 27/04/2016.
 */
public class User {

    private String firstName;
    private String lastName;
    private int userID;
    private String username;
    private char[] password;

    public String getUsername(){return username;}
    public char[] getPassword(){return password;}

    public User(String firstName, String lastName, String username, char[] password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

}
