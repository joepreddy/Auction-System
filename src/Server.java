import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

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
        try {
            PersistanceLayer.addItem(request.listingItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().new ItemListingRequestResponse(true, request.listingItem);
    }

    class ServerGUI extends JFrame {

    }



    }
