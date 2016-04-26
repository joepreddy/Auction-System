import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Joe on 19/04/2016.
 */
public class Client {

    public Client() {
        LoginScreen login = new LoginScreen();
    }
    //Java GUI goes here. Interaction generates message instances to send to the comms class to go to the server

    private class LoginScreen extends JFrame {

        private JTextField username;
        private JTextField password;
        private JButton login;

        public void init() {
            //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(600,400);
            getContentPane().setLayout(new FlowLayout());
            getContentPane().add(username = new JTextField(40));
            getContentPane().add(password = new JTextField(40));
            getContentPane().add(login = new JButton("Login"));

            login.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(new Message("USER_AUTH_REQ", username.getText()+password.getText()));
                }
            });

            this.setVisible(true);
        }
    }

    private void sendMessage(Message message) {
        Comms.recieveMessage(message);
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
