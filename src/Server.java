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

public class Server {

    //Server keeps track of all users and items at all times
    private static HashSet<User> users = new HashSet<User>();
    private static ArrayList<Item> items = new ArrayList<Item>();
    private static ServerSocket listener;
    private static ServerGUI gui = new ServerGUI();

    public Server() throws Exception{
        System.out.println("Starting server...");
        gui.log("Starting server...");
        //Server uses the PersistanceLayer to load users and itoms from files into memory
        users = PersistanceLayer.loadAllUsers();
        items = PersistanceLayer.loadAllItems();
        //Launches a listener onto the same port that the client will connect to
        listener = new ServerSocket(1224);
        try {
            while (true) {
                System.out.println("Server listening...");
                gui.log("New comms thread launched, looking for a client to connect to...");
                //Runs a threaded comms class to run between server and client
                new Comms(listener.accept()).start();
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
                //User is authenticated using a username in and password
                //I hate the fact that the password is in plain text, but didn't have time to hash it
                //Goes without saying, don't use any real password for the system yet
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
            //Preventing duplicate usernames
            if(u.getUsername().equals(request.username)) {
                return new Message().new UserRegistrationResponse(false, "Username already in use!");
            }
        }
        //The user ID is always the number of users +1 for simplicity sake
        User user = new User(request.firstName, request.lastName, request.username, request.password, users.size()+1);
        users.add(user);
        try {
            //User is added to file for persistance
            PersistanceLayer.addUser(user);
        } catch (Exception e) {
            System.out.println(e);
        }
        gui.log("Registered new user: " + user.getUserID() + ", "  + user.getUsername());
        //Notifies the client that the user has successfully been registered
        return new Message().new UserRegistrationResponse(true, user);
    }

    public static Message.ItemRequestResponse requestItems(Message.ItemRequest request) {
        if(request.category == "All") {
            return new Message().new ItemRequestResponse(true, items);
        }
        else {
            ArrayList<Item> tempItems = new ArrayList<Item>();
            for(Item item : items) {
                //Only sends the items in the category requested
                if(item.getCategory().equals(request.category)) {
                    tempItems.add(item);
                }
            }
            return new Message().new ItemRequestResponse(true, tempItems);
        }
    }

    public static Message.ItemBidRequestResponse processItemBid(Message.ItemBidRequest request) {

        Item currentItem = null;
        //Using an iterator prevents concurrency exceptions
        //This loop removes the item from the list so that the bid can be added
        //then the item added back
        for(Iterator<Item> it = items.iterator(); it.hasNext();){
            Item item = it.next();
            if(item.getID() == request.bidItem.getID()) {
                currentItem = item;
                it.remove();
            }
        }

        //Bids need server-side authentication in case the user is not seeing an updated version of the item
        //If there are no bids, then the reserve price acts as the current highest bid
        if(currentItem.getBids().isEmpty()) {
            if(request.bidAmount > currentItem.getReservePrice()) {
                if(currentItem.getStatus() == 1) {
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
                    return new Message().new ItemBidRequestResponse(false, "This listing has either expired, or has not yet started!");
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

    //Sends the client all items listed by the requested user
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
        gui.log("New listing created (" + request.listingItem.getID() + "), by user " + request.listingItem.getSellerID() + " with a reserve of £" + request.listingItem.getReservePrice() + " (Status " + request.listingItem.getStatus() + ")");
        try {
            PersistanceLayer.addItem(request.listingItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().new ItemListingRequestResponse(true, request.listingItem);
    }

    public static Message.BiddedItemRequestResponse getBiddedItems(Message.BiddedItemRequest request) {
        //Gets all the items a specific user has bidded on

        ArrayList<Item> temp = new ArrayList<Item>();

        for(Item item : items) {
            for(Bid bid : item.getBids()) {
                if(bid.getBidderID() == request.userID) {
                    temp.add(item);
                    break;
                }
            }
        }

        return new Message().new BiddedItemRequestResponse(temp);
    }

    //Handles the opening/expiration of items. Called every second
    public static void verifyItemStatus() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar now = Calendar.getInstance();
        for(Item item : items) {
            if(item.getStatus() == 0) {
                if(now.getTime().after(item.getStartTime())) {
                    if(now.getTime().before(item.getEndTime())) {
                        item.setStatus(1);
                        try {
                            PersistanceLayer.addItem(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        gui.log("Bidding for item " + item.getID() + " has commenced. It will close at " + dateFormat.format(item.getEndTime()));
                    }
                }
            } else if(item.getStatus() == 1) {
                if(now.getTime().after(item.getEndTime())) {
                    item.setStatus(2);
                    try {
                        PersistanceLayer.addItem(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(item.bids.isEmpty()) {
                        gui.log("Bidding on the item " + item.getID() + "has ended. There were no winning bids.");
                    } else {
                        gui.log("Bidding for the item " + item.getID() + " has ended. The winning bid was user" + item.getHighestBid().getBidderID() + " who bid £" + item.getHighestBid().getAmount());
                    }

                }
            }
        }
    }

    public static void showWonItemReport() {
        JFrame frame = new JFrame("Won Items Report");
        JTextArea reportArea;
        frame.setPreferredSize(new Dimension(600, 600));
        frame.getContentPane().setLayout(new FlowLayout());
        JScrollPane reportPane = new JScrollPane(reportArea = new JTextArea());
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setPreferredSize(new Dimension(580, 580));
        frame.getContentPane().add(reportPane);

        for(Item item : items) {
            if(item.getStatus() == 2) {
                for(User user : users) {
                    if(item.getHighestBid().getBidderID() == user.getUserID()) {
                        reportArea.append(String.format("Item ID:%d (%s) Won by %s %s on %s with a bid of £%d \n", item.getID(), item.getTitle(), user.getFirstName(), user.getLastName(), String.valueOf(item.getEndTime()), item.getHighestBid().getAmount()));
                    }
                }
            }
        }

        frame.pack();
        frame.setVisible(true);
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
            JButton report = new JButton("Show Item Report");
            report.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showWonItemReport();
                }
            });
            cont.add(report);




            pack();
            setVisible(true);

            //Timer runs the snazzy little clock on the server gui, as well as verifying item statuses
            new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Calendar now = Calendar.getInstance();
                    timer.setText("Current Time: " + dateFormat.format(now.getTime()));
                    verifyItemStatus();
                }
            }).start();
        }
        public void log(String string) {
            Calendar now = Calendar.getInstance();
            log.append(dateFormat.format(now.getTime()) + ": " + string + "\n");
        }
    }





    }
