import java.io.Serializable;

/**
 * Created by Joe on 19/04/2016.
 */
public class Message implements Serializable{
    class UserAuthRequest extends Message {
        String username;
        char[] password;

        public UserAuthRequest(String username, char[] password) {
            this.username = username;
            this.password = password;
        }

    }

    class UserAuthResponse extends Message {
        User user;
        Boolean successful;
        String info;

        public UserAuthResponse(User user, Boolean successful) {
            this.user = user;
            this.successful = successful;
        }

        public UserAuthResponse(Boolean successful, String info) {
            this.successful = successful;
            this.info = info;

        }

    }

    class ConnectionRequest extends Message {
        Boolean successful = false;
    }
}
