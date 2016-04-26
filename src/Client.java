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
            if(in.readLine().equals("Ping!")) {
                out.println("Pong!");
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
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLayout(new GridBagLayout());
                cont = getContentPane();

                GridBagConstraints gc = new GridBagConstraints();
                gc.fill = GridBagConstraints.HORIZONTAL;
                gc.insets = new Insets(10, 10, 10, 10);


                gc.gridx = 0;
                gc.gridy = 0;
                gc.gridwidth = 2;
                cont.add(new JLabel("XYZ Auction System"), gc);

                gc.gridx = 0;
                gc.gridy = 1;
                gc.gridwidth = 1;
                cont.add(new JLabel("Username:"), gc);

                gc.gridx = 1;
                gc.gridy = 1;
                cont.add(username, gc);

                gc.gridx = 0;
                gc.gridy = 2;
                cont.add(new JLabel("Password:"), gc);

                gc.gridx = 1;
                gc.gridy = 2;
                cont.add(password, gc);

                JPanel buttons = new JPanel();
                buttons.setLayout(new FlowLayout());
                buttons.add(login);
                login.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Send message data: " + username.getText() + " " + password.getPassword());
                    }
                });
                buttons.add(register);
                register.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Open register prompt");
                    }
                });

                gc.gridx = 0;
                gc.gridy = 3;
                gc.gridwidth = 2;
                cont.add(buttons, gc);

                setVisible(true);
                pack();
            }


        }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
