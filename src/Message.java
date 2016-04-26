/**
 * Created by Joe on 19/04/2016.
 */
public class Message {

    private static final String[] messageTypes={"USER_REG_REQ","USER_AUTH_REQ"};
    private String messageType;
    private String data;

    public Message(String messageType, String data) {
        this.messageType = messageType;
        this.data = data;
    }
}
