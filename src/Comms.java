import java.io.*;
import java.net.Socket;

/**
 * Created by Joe on 19/04/2016.
 */
public class Comms extends Thread{

    private Socket socket;
    //private BufferedReader in;
    private ObjectInputStream in;
    //private PrintWriter out;
    private ObjectOutputStream out;
    Boolean connected = false;

    public Comms(Socket socket) {this.socket = socket;}

    public void run(){
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream((socket.getInputStream()));

            while(true) {
                //System.out.println("Detected new client: Establishing connection!");
                if(!connected){out.writeObject(new Message().new ConnectionRequest());}
                try {
                    Message msg = (Message)in.readObject();

                    if(msg instanceof Message.ConnectionRequest) {
                        if (((Message.ConnectionRequest) msg).successful) {
                            System.out.println("Connected!");
                            connected = true;

                        }
                        //return;
                    }
                    else if(msg instanceof  Message.UserAuthRequest) {
                        System.out.println("Received authentication request...");
                        out.writeObject(Server.authenticateUser((Message.UserAuthRequest)msg));
                        System.out.println("Response sent...");
                        //return;
                    }
                    else if(msg instanceof Message.UserRegistrationRequest) {
                        System.out.println("Registration request received");
                        out.writeObject(Server.registerUser((Message.UserRegistrationRequest)msg));
                        System.out.println("Sent back reply");

                    }
                    else if(msg instanceof Message.ItemRequest) {
                        System.out.println("Item request received");
                        out.writeObject(Server.requestItems((Message.ItemRequest)msg));
                        System.out.println("Item reply sent");
                    }
                    else if(msg instanceof  Message.ItemBidRequest) {
                        System.out.println("Bid request received!");
                        out.writeObject(Server.processItemBid((Message.ItemBidRequest)msg));
                        System.out.println("Reply sent");
                    }
                    else if(msg instanceof Message.ItemRequestByUser) {
                        System.out.println("Item request by user received");
                        out.writeObject(Server.requestItemByUser((Message.ItemRequestByUser) msg));
                    }
                    else if(msg instanceof  Message.ItemListingRequest) {
                        out.writeObject(Server.listItem((Message.ItemListingRequest)msg));
                    }
                    //return;
                } catch (Exception e){
                    System.out.println("Connection Lost!");
                    break;
                }


            }

            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        catch(Exception e) {
            System.out.println(e);
        }


    }


}
