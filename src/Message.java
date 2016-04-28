/**
 * Created by Joe on 19/04/2016.
 */
public class Message {


    class UserAuthRequest extends Message {
        String username;
        char[] password;

        public UserAuthRequest(String username, char[] password) {
            this.username = username;
            this.password = password;
        }

    }
}
