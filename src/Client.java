import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by Joe on 19/04/2016.
 */
public class Client {

    //BufferedReader in;
    ObjectInputStream in;
    ObjectOutputStream out;
    private int clientID;
    Container cont;
    JPanel loginPanel;
    JPanel registerPanel;
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
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());


        /*while(true) {
            System.out.println("Connecting to server...");
            String resp = in.readLine();
            //System.out.println(in.readLine());
            if(resp.equals("Ping!")) {
                System.out.println("Connection to server established!");
                out.println("Pong!");
                connected = true;
                return;
            }
        }*/

        while(true) {
            System.out.println("Connecting to server...");
            try {
                Message msg = (Message)in.readObject();
                if(msg instanceof Message.ConnectionRequest) {
                    System.out.println("Connection request received");
                    ((Message.ConnectionRequest) msg).successful = true;
                    out.writeObject(msg);
                    return;
                }
            }
            catch (Exception ex) {
                System.out.println("rip");
            }

        }
    }

    class LoginScreen extends JFrame {



        public LoginScreen() {
            super("Auction Login");
            init();
        }

        public void createLoginPanel(){

            JTextField username = new JTextField(40);
            JPasswordField password = new JPasswordField(40);
            JButton login = new JButton("Login");
            JButton register = new JButton("Register");

            loginPanel = new JPanel();
            loginPanel.setLayout(new GridBagLayout());

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
                    try {
                        loginUser(username.getText(), password.getPassword());
                    } catch(Exception loginException) {
                        System.out.println(loginException);
                    }
                }
            });
            buttons.add(register);
            register.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cont.remove(loginPanel);
                    cont.add(registerPanel);
                    pack();
                }
            });

            gc.gridx = 0;
            gc.gridy = 3;
            gc.gridwidth = 2;
            loginPanel.add(buttons, gc);
        }

        public void createRegisterPanel() {

            JTextField firstName = new JTextField(20);
            JTextField lastName = new JTextField(20);
            JTextField username = new JTextField(20);
            JPasswordField password = new JPasswordField(20);

            registerPanel = new JPanel();
            registerPanel.setLayout(new GridBagLayout());

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 10, 10, 10);



            gc.fill =GridBagConstraints.HORIZONTAL;
            gc.gridx = 0;
            gc.gridy = 0;
            registerPanel.add(new JLabel("First Name:"), gc);

            gc.gridx = 1;
            gc.gridy = 0;
            registerPanel.add(firstName, gc);

            gc.gridx = 2;
            gc.gridy = 0;
            registerPanel.add(new JLabel("Last Name:"), gc);

            gc.gridx = 3;
            gc.gridy = 0;
            registerPanel.add(lastName, gc);

            gc.gridx = 0;
            gc.gridy = 1;
            registerPanel.add(new JLabel("Username:"), gc);

            gc.gridx = 1;
            gc.gridy = 1;
            registerPanel.add(username, gc);

            gc.gridx = 2;
            gc.gridy = 1;
            registerPanel.add(new JLabel("Password:"), gc);

            gc.gridx = 3;
            gc.gridy = 1;
            registerPanel.add(password, gc);

            JPanel buttons = new JPanel();
            JButton register = new JButton("Register");
            JButton cancel = new JButton("Cancel");

            register.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });

            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cont.remove(registerPanel);
                    cont.add(loginPanel);
                    pack();
                }
            });

            gc.gridx = 1;
            gc.gridy = 2;
            gc.gridwidth = 2;
            buttons.add(register);
            buttons.add(cancel);
            registerPanel.add(buttons,gc);
        }


        public void init() {

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            createLoginPanel();
            createRegisterPanel();
            cont = getContentPane();
            cont.add(loginPanel);
            setVisible(true);
            pack();
        }


        public void loginUser(String username, char[] password) throws Exception{
            out.writeObject(new Message().new UserAuthRequest(username, password));
        }


    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
