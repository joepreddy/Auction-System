import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

/**
 * Created by Joe on 19/04/2016.
 */
public class Server {

    private static HashSet<User> users = new HashSet<User>();
    private static HashSet<Client> clients = new HashSet<Client>();
    private static ArrayList<Item> items = new ArrayList<Item>();
    private static ServerSocket listener;
    private static ServerGUI gui = new ServerGUI();



    public HashSet getClients() {return clients;}


    public Server() throws Exception{
        System.out.println("Starting server...");
        gui.log("Starting server...");
        users = PersistanceLayer.loadAllUsers();
        //items.add(new Item());
        items = PersistanceLayer.loadAllItems();
        //PersistanceLayer.addItem(new Item());
        listener = new ServerSocket(1224);
        try {
            while (true) {
                System.out.println("Server listening...");
                gui.log("New comms thread launched, looking for a client to connect to...");
                new Comms(listener.accept()).start();
                System.out.println("Connection established...");
                gui.log("Connection to client established...");
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
                    gui.log("Authenticated user: " + u.getUserID() + ", " + u.getUsername());
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
        gui.log("Registered new user: " + user.getUserID() + ", "  + user.getUsername());
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

    public static Message.ItemBidRequestResponse processItemBid(Message.ItemBidRequest request) {

        Item currentItem = null;
        /*for(Item item : items) {
            if(item.getID() == request.bidItem.getID()) {
                currentItem = item;
            }
        }*/
        for(Iterator<Item> it = items.iterator(); it.hasNext();){
            Item item = it.next();
            if(item.getID() == request.bidItem.getID()) {
                currentItem = item;
                it.remove();
            }
        }



        if(currentItem.getBids().isEmpty()) {
            if(request.bidAmount > currentItem.getReservePrice()) {
                if(currentItem.addBid(request.bidder.getUserID(), request.bidAmount)) {
                    if(request.bidder.getUserID() != currentItem.getSellerID()) {
                        try {
                            items.add(currentItem);
                            gui.log("Bid placed on item " + currentItem.getID() + " by user " + request.bidder.getUserID() + " of amount " + request.bidAmount);
                            PersistanceLayer.addItem(currentItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new Message().new ItemBidRequestResponse(true, currentItem);
                    }else {
                        return new Message().new ItemBidRequestResponse(false, "You may not bid on your own item!");
                    }
                } else {
                    return new Message().new ItemBidRequestResponse(false, "There was a problem authenticating this bid with the server!");
                }
            } else {
                return new Message().new ItemBidRequestResponse(false, "Your bid must be above the reserve price!");
            }
        } else if(request.bidAmount > currentItem.getHighestBid().getAmount()) {
            if(request.bidder.getUserID() != currentItem.getSellerID()) {
                if(currentItem.getStatus() == 1) {
                    if(currentItem.addBid(request.bidder.getUserID(), request.bidAmount)){
                        try {
                            items.add(currentItem);
                            gui.log("Bid placed on item " + currentItem.getID() + " by user " + request.bidder.getUserID() + " of amount " + request.bidAmount);
                            PersistanceLayer.addItem(currentItem);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        System.out.println("Returning " + currentItem.toString());
                        return new Message().new ItemBidRequestResponse(true, currentItem);
                    } else {
                        return new Message().new ItemBidRequestResponse(false, "There was a problem processing your bid on the server.");
                    }
                } else {
                    return new Message().new ItemBidRequestResponse(false, "This listing has either expired, or has not yet started!");
                }
            } else {
                return new Message().new ItemBidRequestResponse(false, "You can not bid on your own listing!");
            }
        } else {
            return new Message().new ItemBidRequestResponse(false, "Your bid must be higher than the current bid!");
        }
    }

    public static Message.ItemRequestByUserResponse requestItemByUser(Message.ItemRequestByUser request) {
        ArrayList<Item> userItems = new ArrayList<>();
        for(Item item : items) {
            if(item.getSellerID() == request.userID) {
                userItems.add(item);
            }
        }
        if(userItems.isEmpty()) {
            return new Message().new ItemRequestByUserResponse(false, "No items for sale by this user!");
        } else {
            return new Message().new ItemRequestByUserResponse(true, userItems);
        }
    }

    public static Message.ItemListingRequestResponse listItem(Message.ItemListingRequest request) {
        request.listingItem.setID(items.size()+1);
        items.add(request.listingItem);
        gui.log("New listing created (" + request.listingItem.getID() + "), by user " + request.listingItem.getSellerID() + " with a reserve of £" + request.listingItem.getReservePrice());
        try {
            PersistanceLayer.addItem(request.listingItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().new ItemListingRequestResponse(true, request.listingItem);
    }

    static class ServerGUI extends JFrame {

        Container cont;
        JTextArea log;
        JLabel timer;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


        public ServerGUI() {
            init();
        }

        public void init(){
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(600,800));
            cont = getContentPane();
            cont.setLayout(new FlowLayout());
            log = new JTextArea();
            log.setPreferredSize(new Dimension(580, 700));
            cont.add(timer = new JLabel("Current Time:"));
            JScrollPane logPane = new JScrollPane(log);
            cont.add(logPane);




            pack();
            setVisible(true);

            new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Calendar now = Calendar.getInstance();
                    timer.setText("Current Time: " + dateFormat.format(now.getTime()));
                }
            }).start();
        }
        public void log(String string) {
            Calendar now = Calendar.getInstance();
            log.append(dateFormat.format(now.getTime()) + ": " + string + "\n");
        }
    }





    }
