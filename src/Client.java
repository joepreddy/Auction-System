import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Joe on 19/04/2016.
 */
public class Client {

    BufferedReader in;
    PrintWriter out;
    private int clientID;
    Container cont;
    JPanel loginPanel;
    Boolean connected = false;

    public Client() {
        LoginScreen login = new LoginScreen();
        try {
            run();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private void run() throws IOException{
        Socket socket = new Socket("localhost", 1224);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        while(true) {
            System.out.println("Connecting to server...");
            String resp = in.readLine();
            //System.out.println(in.readLine());
            if(resp.equals("Ping!")) {
                System.out.println("Connection to server established!");
                out.println("Pong!");
                connected = true;
                return;
            }
        }
    }

    class LoginScreen extends JFrame {

        private JTextField username = new JTextField(40);
        private JPasswordField password = new JPasswordField(40);
        private JButton login = new JButton("Login");
        private JButton register = new JButton("Register");

        public LoginScreen() {
            super("Auction Login");
            init();
        }



        public void init() {
            loginPanel = new JPanel();
            loginPanel.setLayout(new GridBagLayout());

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //setLayout(new GridBagLayout());
            cont = getContentPane();

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 10, 10, 10);

            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 2;
            loginPanel.add(new JLabel("XYZ Auction System"), gc);

            gc.gridx = 0;
            gc.gridy = 1;
            gc.gridwidth = 1;
            loginPanel.add(new JLabel("Username:"), gc);

            gc.gridx = 1;
            gc.gridy = 1;
            loginPanel.add(username, gc);

            gc.gridx = 0;
            gc.gridy = 2;
            loginPanel.add(new JLabel("Password:"), gc);

            gc.gridx = 1;
            gc.gridy = 2;
            loginPanel.add(password, gc);

            JPanel buttons = new JPanel();
            buttons.setLayout(new FlowLayout());
            buttons.add(login);
            login.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loginUser(username.getText(), password.getPassword());
                }
            });
            buttons.add(register);
            register.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Open register prompt");
                    loginPanel.setVisible(false);
                }
            });

            gc.gridx = 0;
            gc.gridy = 3;
            gc.gridwidth = 2;
            loginPanel.add(buttons, gc);
            cont.add(loginPanel);

            setVisible(true);
            pack();
        }


        public void loginUser(String username, char[] password) {

        }


    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
