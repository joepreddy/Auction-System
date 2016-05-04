import java.io.Serializable;
import java.util.ArrayList;

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

    class UserRegistrationRequest extends Message {
        String firstName;
        String lastName;
        String username;
        char[] password;

        public UserRegistrationRequest(String firstName, String lastName, String username, char[] password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
        }
    }

    class UserRegistrationResponse extends Message {
        User user;
        Boolean successful;
        String info;

        public UserRegistrationResponse(Boolean successful, User user) {
            this.successful = successful;
            this.user = user;
        }

        public UserRegistrationResponse(Boolean successful, String info) {
            this.info = info;
            this.successful = successful;
        }
    }

    class ItemRequest extends Message {
        String category;

        public ItemRequest(String category) {
            this.category = category;
        }
    }

    class ItemRequestResponse extends Message {
        Boolean successful;
        String info;
        ArrayList<Item> items;

        public ItemRequestResponse(Boolean successful, String info) {
            this.successful = successful;
            this.info = info;
        }

        public ItemRequestResponse(Boolean successful, ArrayList<Item> items) {
            this.successful = successful;
            this.items = items;
        }
    }

    class ItemBidRequest extends Message {
        User bidder;
        int bidAmount;
        Item bidItem;

        public ItemBidRequest(User bidder, int bidAmount, Item bidItem){
            this.bidder = bidder;
            this.bidAmount = bidAmount;
            this.bidItem = bidItem;
        }
    }

    class ItemBidRequestResponse extends Message {
        Boolean successful;
        String info;

        public ItemBidRequestResponse(Boolean successful) {
            this.successful = successful;
        }

        public ItemBidRequestResponse(Boolean successful, String info) {
            this.successful = successful;
            this.info = info;
        }
    }

    class ConnectionRequest extends Message {
        Boolean successful = false;
    }
}
