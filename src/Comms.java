import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Joe on 19/04/2016.
 */
public class Comms extends Thread{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Comms(Socket socket) {this.socket = socket;}

    public void run() {
        try {
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
        }
    }


}
