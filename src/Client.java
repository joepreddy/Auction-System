import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Joe on 19/04/2016.
 */
public class Client {

    //BufferedReader in;
    ObjectInputStream in;
    ObjectOutputStream out;
    private int clientID;

    Boolean connected = false;
    User loggedUser;

    ArrayList<Item> currentDispItems;

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

        while(true) {
            //System.out.println("Connecting to server...");
            try {
                Message msg = (Message)in.readObject();
                if(msg instanceof Message.ConnectionRequest) {
                    System.out.println("Connection request received");
                    ((Message.ConnectionRequest) msg).successful = true;
                    out.writeObject(msg);
                    //return;
                }
                else if(msg instanceof Message.UserAuthResponse) {
                    if(((Message.UserAuthResponse) msg).successful) {
                        System.out.println("Load client!");
                    }
                    else {
                        System.out.println(((Message.UserAuthResponse) msg).info);
                    }
                    //return;
                }
                else if(msg instanceof Message.UserRegistrationResponse) {
                    if(((Message.UserRegistrationResponse) msg).successful) {
                        System.out.println("User registered successfully");
                    }
                    else {
                        System.out.println(((Message.UserRegistrationResponse) msg).info);
                    }
                }
                else if(msg instanceof Message.ItemRequestResponse) {
                    if(((Message.ItemRequestResponse)msg).successful) {
                        currentDispItems = ((Message.ItemRequestResponse) msg).items;
                    }
                    else {
                        System.out.println(((Message.ItemRequestResponse) msg).info);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    class LoginScreen extends JFrame {

        Container cont;
        JPanel loginPanel;
        JPanel registerPanel;


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
                        loginException.printStackTrace();
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
                    try {
                        registerUser(firstName.getText(), lastName.getText(), username.getText(), password.getPassword());
                    } catch(Exception ex) {
                        System.out.println(e);
                    }


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
            MainWindow window = new MainWindow();
        }

        /*public void registrationUserTest() {
            User user = new User("Jerry", "Jackson", "jj", ("eagh".toCharArray()));
            try {
                PersistanceLayer.addUser(user);
            } catch (Exception e){
                System.out.println(e);
            }

        }*/

        public void registerUser(String firstName, String lastName, String username, char[] password) throws Exception{
            System.out.println("Sending registration request...");
            out.writeObject(new Message().new UserRegistrationRequest(firstName, lastName, username, password));
        }


    }

    class MainWindow extends JFrame {

        Container cont;
        JPanel mainFrame = new JPanel();

        JList<String> categories;
        JList<Item> itemList;

        public MainWindow() {
            super("XYZ Auction System");
            init();
        }

        public void init() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            createMainWindow();
            cont = getContentPane();
            cont.add(mainFrame);
            setVisible(true);
            pack();
        }

        public void createMainWindow() {


            JTabbedPane menu = new JTabbedPane();
            mainFrame.add(menu);

            JPanel dashboard = new JPanel();
            dashboard.setPreferredSize(new Dimension(1024, 768));
            dashboard.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();



            categories = new JList<String>(Item.getCategories());
            JScrollPane cat = new JScrollPane(categories);
            categories.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    populateList();
                }
            });
            gc.anchor = GridBagConstraints.NORTHWEST;
            gc.gridx = 0;
            gc.gridy = 0;
            dashboard.add(cat, gc);


            itemList = new JList();
            JScrollPane items = new JScrollPane(itemList);

            gc.gridx = 1;
            gc.gridy = 0;
            dashboard.add(items, gc);
            menu.add(dashboard, "Browse");

        }

        public void populateList() {
            try {
                out.writeObject(new Message().new ItemRequest(categories.getSelectedValue()));
                System.out.println("Attempting to get items");
                itemList.setModel(new ItemListModel(currentDispItems));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        class ItemListModel extends AbstractListModel<Item> {

            public ArrayList<Item> modelItem;

            public ItemListModel(ArrayList<Item> modelItem) {
                this.modelItem = modelItem;
            }

            public int getSize() {
                return modelItem.size();
            }

            public Item getElementAt(int index) {
                return modelItem.get(index);
            }

        }

    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
