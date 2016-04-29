import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

/**
 * Created by Joe on 19/04/2016.
 */
public class Server {

    private static HashSet<User> users = new HashSet<User>();
    private static HashSet<Client> clients = new HashSet<Client>();
    private static ServerSocket listener;

    public HashSet getClients() {return clients;}
    public Server() throws Exception{
        System.out.println("Starting server...");
        users = PersistanceLayer.loadAllUsers();
        listener = new ServerSocket(1224);
        try {
            while (true) {
                System.out.println("Server listening...");
                new Comms(listener.accept()).start();
                System.out.println("Connection established...");
            }

        }catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                listener.close();
            } catch(IOException i) {
                System.out.println(i);}
            }
        }

    public static void main(String[] args) throws Exception{
        Server server = new Server();
    }

    public static Message.UserAuthResponse authenticateUser(Message.UserAuthRequest request) {
        for(User u : users) {
            if(u.getUsername().equals(request.username)) {
                if(u.getPassword().equals(request.password)) {
                    return new Message().new UserAuthResponse(u, true);
                }
                else {
                    return new Message().new UserAuthResponse(false, "Incorrect Password!");
                }
            }
            else {
                return new Message().new UserAuthResponse(false, "Unknown User!");
            }
        }
        return new Message().new UserAuthResponse(false, "Unknown User!");
    }


    }
