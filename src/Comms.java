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
       /* try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while(true) {
                System.out.println("Detected new client: Establishing Connection!");
                out.println("Ping!");
                String resp = in.readLine();
                if(resp.equals("Pong!")){
                    System.out.println("Connection Established");
                    return;
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        finally
        {
            try {
                socket.close();
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }*/
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
                    //return;
                }
                catch(Exception ex) {
                    System.out.println("rip");
                }


            }
        }
        catch(Exception e) {
            System.out.println(e);
        }


    }


}
