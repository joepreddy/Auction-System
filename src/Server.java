import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Joe on 19/04/2016.
 */
public class Server {

    private static HashSet<User> users = new HashSet<User>();
    private static HashSet<Client> clients = new HashSet<Client>();
    private static ArrayList<Item> items = new ArrayList<Item>();
    private static ServerSocket listener;



    public HashSet getClients() {return clients;}


    public Server() throws Exception{
        System.out.println("Starting server...");
        users = PersistanceLayer.loadAllUsers();
        //items.add(new Item());
        items = PersistanceLayer.loadAllItems();
        //PersistanceLayer.addItem(new Item());
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
                if(Arrays.equals(u.getPassword(), request.password)) {
                    return new Message().new UserAuthResponse(u, true);
                }
                else {
                    return new Message().new UserAuthResponse(false, "Incorrect Password!");
                }
            }
        }
        return new Message().new UserAuthResponse(false, "Unknown User!");
    }

    public static Message.UserRegistrationResponse registerUser(Message.UserRegistrationRequest request) {
        for(User u : users) {
            if(u.getUsername().equals(request.username)) {
                return new Message().new UserRegistrationResponse(false, "Username already in use!");
            }
        }
        User user = new User(request.firstName, request.lastName, request.username, request.password, users.size()+1);
        users.add(user);
        try {
            PersistanceLayer.addUser(user);
        } catch (Exception e) {
            System.out.println(e);
        }

        return new Message().new UserRegistrationResponse(true, user);
        //return new Message().new UserRegistrationResponse(false, "Unknown Error!");
    }

    public static Message.ItemRequestResponse requestItems(Message.ItemRequest request) {
        if(request.category == "All") {
            return new Message().new ItemRequestResponse(true, items);
        }
        else {
            ArrayList<Item> tempItems = new ArrayList<Item>();
            for(Item item : items) {
                if(item.getCategory().equals(request.category)) {
                    tempItems.add(item);
                }
            }
            return new Message().new ItemRequestResponse(true, tempItems);
        }
    }


    }
