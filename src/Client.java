import jdk.nashorn.internal.scripts.JO;

//TODO WORK OUT HOW TO GET THE JLIST TO REFRESH - ALSO CREATE GUI AND ACTIVITY LOGGING FOR SERVER





import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;

public class Client {

    //BufferedReader in;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    //Boolean connected = false;
    private User loggedUser;
    private Item selectedBrowseItem;

    private ArrayList<Item> currentDispItems;
    private MainWindow mainWindow;

    private ArrayList<Item> requestedUserItems;

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
                        loggedUser = ((Message.UserAuthResponse) msg).user;
                        MainWindow window = new MainWindow();
                    }
                    else {
                        System.out.println(((Message.UserAuthResponse) msg).info);
                        JOptionPane.showMessageDialog(null, ((Message.UserAuthResponse) msg).info);
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
                else if(msg instanceof Message.ItemBidRequestResponse) {
                    if(((Message.ItemBidRequestResponse) msg).successful) {
                        out.writeObject(new Message().new ItemRequest(mainWindow.brCategories.getSelectedValue()));
                        //mainWindow.populateBrowseItemsList();
                        //mainWindow.brItemList.getSelectedValue().setBids(((Message.ItemBidRequestResponse) msg).item.getBids());
                        //mainWindow.displayItemInfo();

                        System.out.println("Bid was successful");
                    }
                    else {
                        JOptionPane.showMessageDialog(null, ((Message.ItemBidRequestResponse) msg).info);
                    }
                }
                else if(msg instanceof Message.ItemRequestByUserResponse) {
                    if(((Message.ItemRequestByUserResponse) msg).successful) {
                        requestedUserItems = ((Message.ItemRequestByUserResponse) msg).items;


                    } else {
                        System.out.println(((Message.ItemRequestByUserResponse) msg).info);
                    }
                }
                else if (msg instanceof Message.ItemListingRequestResponse) {
                    System.out.println("Item successfully added");
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
        //LOGIN SCREEN VARIABLES
        JTextField lgUsername = new JTextField(40);
        JPasswordField lgPassword = new JPasswordField(40);
        JButton lgLogin = new JButton("Login");
        JButton lgRegister = new JButton("Register");
        //REGISTER SCREEN VARIABLES
        JTextField rgFirstName = new JTextField(20);
        JTextField rgLastName = new JTextField(20);
        JTextField rgUsername = new JTextField(20);
        JPasswordField rgPassword = new JPasswordField(20);

        public LoginScreen() {
            super("Auction Login");
            init();
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

        public void createLoginPanel(){

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
            loginPanel.add(lgUsername, gc);

            gc.gridx = 0;
            gc.gridy = 2;
            loginPanel.add(new JLabel("Password:"), gc);

            gc.gridx = 1;
            gc.gridy = 2;
            loginPanel.add(lgPassword, gc);

            JPanel buttons = new JPanel();
            buttons.setLayout(new FlowLayout());
            buttons.add(lgLogin);
            lgLogin.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        loginUser(lgUsername.getText(), lgPassword.getPassword());
                    } catch(Exception loginException) {
                        loginException.printStackTrace();
                    }
                }
            });
            buttons.add(lgRegister);
            lgRegister.addActionListener(new ActionListener() {
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
            registerPanel.add(rgFirstName, gc);

            gc.gridx = 2;
            gc.gridy = 0;
            registerPanel.add(new JLabel("Last Name:"), gc);

            gc.gridx = 3;
            gc.gridy = 0;
            registerPanel.add(rgLastName, gc);

            gc.gridx = 0;
            gc.gridy = 1;
            registerPanel.add(new JLabel("Username:"), gc);

            gc.gridx = 1;
            gc.gridy = 1;
            registerPanel.add(rgUsername, gc);

            gc.gridx = 2;
            gc.gridy = 1;
            registerPanel.add(new JLabel("Password:"), gc);

            gc.gridx = 3;
            gc.gridy = 1;
            registerPanel.add(rgPassword, gc);

            JPanel buttons = new JPanel();
            JButton register = new JButton("Register");
            JButton cancel = new JButton("Cancel");

            register.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        registerUser(rgFirstName.getText(), rgLastName.getText(), rgUsername.getText(), rgPassword.getPassword());
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

        public void loginUser(String username, char[] password) throws Exception{
            out.writeObject(new Message().new UserAuthRequest(username, password));
        }

        public void registerUser(String firstName, String lastName, String username, char[] password) throws Exception{
            System.out.println("Sending registration request...");
            out.writeObject(new Message().new UserRegistrationRequest(firstName, lastName, username, password));
        }

    }


    class MainWindow extends JFrame {

        Container cont;
        JPanel mainFrame = new JPanel();

        //BROWSE TAB VARIABLES
        JList<String> brCategories;
        JList<Item> brItemList;
        JPanel brDetails;
        JPanel brBidOptions;
        JTextField brTitle;
        JTextField brCategory;
        JTextField brCurrBid;
        JTextField brStartTime;
        JTextField brEndTime;
        JTextArea brDescription;
        JTextField brSeller;
        JTextField brBidAmount;

        //ITEMS WINDOW VARIABLES
        JList<Item> iwItems;
        JScrollPane iwItemsPane;
        JPanel iwDetails;
        JTextField iwTitle;
        JComboBox<String> iwCategory;
        JTextField iwReserve;
        JTextArea iwDescription;
        JSpinner iwStartTime;
        JSpinner iwEndTime;
        JButton iwNew;
        JButton iwEdit;
        JButton iwSave;
        Boolean editingItem;
        Item iwCurrEdit;
        JCheckBox iwTimeNow;
        UserItemListModel iwItemsModel;

        public MainWindow() {
            super("XYZ Auction System");
            init();
        }

        public void init() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            createMainWindow();
            cont = getContentPane();
            cont.add(mainFrame);
            pack();
            setVisible(true);

            mainWindow = this;
            //getUserItems(loggedUser.getUserID());
        }

        public void createMainWindow() {
            JTabbedPane menu = new JTabbedPane();
            mainFrame.add(menu);
            menu.add(createBrowseWindow(), "Browse");
            menu.add(createItemsWindow(), "My Items");

            menu.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if(menu.getSelectedIndex() == 1) {
                        System.out.println("My Items Pressed");
                        getUserItems(loggedUser.getUserID());
                    }
                }
            });
            pack();

        }

        public JPanel createBrowseWindow() {

            JPanel dashboard = new JPanel();
            dashboard.setPreferredSize(new Dimension(1024, 768));
            dashboard.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();

            brCategories = new JList<String>(Item.getCategories());
            JScrollPane cat = new JScrollPane(brCategories);
            brCategories.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    populateBrowseItemsList();
                }
            });
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            cat.setPreferredSize(new Dimension(176, 760));
            gc.gridx = 0;
            gc.gridy = 0;
            dashboard.add(cat, gc);


            brItemList = new JList();
            JScrollPane items = new JScrollPane(brItemList);
            brItemList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectedBrowseItem = brItemList.getSelectedValue();
                    displayItemInfo();
                }
            });
            items.setPreferredSize(new Dimension(300, 760));
            gc.gridx = 1;
            gc.gridy = 0;
            dashboard.add(items, gc);

            brDetails = new JPanel();
            //brDetails.setBackground(Color.red );
            brDetails.setPreferredSize(new Dimension(548,768));
            brDetails.setLayout(new GridBagLayout());
            GridBagConstraints cgc = new GridBagConstraints();
            cgc.anchor = GridBagConstraints.FIRST_LINE_START;
            cgc.fill = GridBagConstraints.HORIZONTAL;
            cgc.insets = new Insets(10,10,10,10);
            cgc.gridx = 0;
            cgc.gridy = 0;
            cgc.gridwidth = 2;
            brDetails.add(new JLabel("Title:"), cgc);

            cgc.gridx = 0;
            cgc.gridy = 1;
            brDetails.add(brTitle = new JTextField(60), cgc);
            brTitle.setEditable(false);

            cgc.gridx = 0;
            cgc.gridy = 2;
            cgc.gridwidth = 1;
            brDetails.add(new JLabel("Category:"), cgc);

            cgc.gridx = 1;
            cgc.gridy = 2;

            brDetails.add(new JLabel("Reserve/Current Bid:"), cgc);

            cgc.gridx = 0;
            cgc.gridy = 3;
            cgc.weightx = 1;
            brDetails.add(brCategory = new JTextField(10), cgc);
            brCategory.setEditable(false);

            cgc.gridx = 1;
            cgc.gridy = 3;
            brDetails.add(brCurrBid = new JTextField(10), cgc);
            brCurrBid.setEditable(false);

            cgc.gridx = 0;
            cgc.gridy = 4;
            cgc.gridwidth = 2;
            cgc.weightx = 1;
            cgc.weighty = 1;
            cgc.fill = GridBagConstraints.BOTH;
            brDescription = new JTextArea();
            brDescription.setEditable(false);
            brDescription.setLineWrap(true);
            brDescription.setWrapStyleWord(true);
            brDescription.setText("Select an item to see more details!");
            JScrollPane brDescPane = new JScrollPane(brDescription);
            brDescPane.setPreferredSize(new Dimension(435, 250));
            brDetails.add(brDescPane, cgc);

            cgc.fill = GridBagConstraints.HORIZONTAL;
            cgc.weightx = 0;
            cgc.weighty = 0;
            cgc.gridx = 0;
            cgc.gridy  = 5;
            cgc.gridwidth = 1;
            brDetails.add(new JLabel("Start Time:"), cgc);

            cgc.gridx = 1;
            cgc.gridy = 5;
            brDetails.add(new JLabel("End Time:"), cgc);

            cgc.gridx = 0;
            cgc.gridy = 6;
            brDetails.add(brStartTime = new JTextField(15), cgc);
            brStartTime.setEditable(false);

            cgc.gridx = 1;
            cgc.gridy = 6;
            brDetails.add(brEndTime = new JTextField(15), cgc);
            brEndTime.setEditable(false);

            cgc.gridx = 0;
            cgc.gridy = 7;
            cgc.gridwidth = 2;
            brBidOptions = new JPanel();
            brBidOptions.setBorder(BorderFactory.createLineBorder(Color.black));
            brBidOptions.add(new JLabel("Bid Amount:"));
            brBidOptions.add(brBidAmount = new JTextField(10));
            JButton bid = new JButton("Place Bid");
            bid.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        out.writeObject(new Message().new ItemBidRequest(loggedUser, Integer.valueOf(brBidAmount.getText()), brItemList.getSelectedValue()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            brBidOptions.add(bid);
            cgc.anchor = GridBagConstraints.LAST_LINE_END;
            brDetails.add(brBidOptions, cgc);

            gc.gridx = 2;
            gc.gridy = 0;
            dashboard.add(brDetails, gc);
            return dashboard;
        }

        public JPanel createItemsWindow() {


            JPanel itemsWindow = new JPanel();
            itemsWindow.setPreferredSize(new Dimension(1024, 768));
            itemsWindow.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();

            iwItems = new JList<Item>();

            iwItemsPane = new JScrollPane(iwItems);
            iwItems.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    displaySelectedUserItemInfo(iwItems.getSelectedValue());
                }
            });
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            iwItemsPane.setPreferredSize(new Dimension(300, 760));
            gc.gridx = 0;
            gc.gridy = 0;
            itemsWindow.add(iwItemsPane, gc);


            /*brItemList = new JList();
            JScrollPane items = new JScrollPane(brItemList);
            brItemList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectedBrowseItem = brItemList.getSelectedValue();
                    displayItemInfo();
                }
            });
            items.setPreferredSize(new Dimension(300, 760));
            gc.gridx = 1;
            gc.gridy = 0;
            dashboard.add(items, gc);*/

            iwDetails = new JPanel();
            iwDetails.setPreferredSize(new Dimension(724,768));
            iwDetails.setLayout(new GridBagLayout());
            GridBagConstraints igc = new GridBagConstraints();
            igc.insets = new Insets(10,0,0,0);
            igc.anchor = GridBagConstraints.FIRST_LINE_START;
            //igc.weightx = 1.0;
            //igc.weighty = 1.0;
            igc.gridx = 0;
            igc.gridy = 0;
            igc.gridwidth = 2;
            iwDetails.add(new JLabel("Title:"), igc);

            igc.gridx = 0;
            igc.gridy = 1;
            igc.gridwidth = 2;
            //igc.weightx = 1;
            //igc.fill = GridBagConstraints.HORIZONTAL;
            iwDetails.add(iwTitle = new JTextField(60), igc);
            iwTitle.setEditable(false);

            igc.gridx = 0;
            igc.gridy = 2;
            igc.gridwidth = 1;
            iwDetails.add(new JLabel("Category:"), igc);

            igc.gridx = 1;
            igc.gridy = 2;
            iwDetails.add(new JLabel("Reserve Price:"), igc);

            igc.gridx = 0;
            igc.gridy = 3;
            iwDetails.add(iwCategory = new JComboBox<String>(Item.getCategories()), igc);
            iwCategory.setEnabled(false);

            igc.gridx = 1;
            igc.gridy = 3;
            iwDetails.add(iwReserve = new JTextField(25), igc);
            iwReserve.setEditable(false);

            igc.gridx = 0;
            igc.gridy = 4;
            igc.gridwidth = 2;
            iwDescription = new JTextArea();
            iwDescription.setEditable(false);
            iwDescription.setLineWrap(true);
            iwDescription.setWrapStyleWord(true);
            iwDescription.setText("Select a bid to edit it, or list a new item!");
            JScrollPane iwDescPane = new JScrollPane(iwDescription);
            iwDescPane.setPreferredSize(new Dimension(720, 300));
            iwDetails.add(iwDescPane, igc);

            igc.gridx = 0;
            igc.gridy = 5;
            igc.gridwidth = 1;
            iwDetails.add(new JLabel("Start Time:"), igc);

            igc.gridx = 1;
            igc.gridy = 5;
            iwDetails.add(new JLabel("End Time:"), igc);

            igc.gridx = 0;
            igc.gridy = 6;
            iwStartTime = new JSpinner();
            iwStartTime.setModel(new SpinnerDateModel());
            iwDetails.add(iwStartTime, igc);
            iwStartTime.setEnabled(false);

            igc.gridx = 1;
            igc.gridy = 6;
            iwEndTime = new JSpinner();
            iwEndTime.setModel(new SpinnerDateModel());
            iwDetails.add(iwEndTime, igc);
            iwEndTime.setEnabled(false);

            igc.gridx = 0;
            igc.gridy = 7;
            JPanel startNowChk = new JPanel();
            startNowChk.add(new JLabel("Start Now:"));
            startNowChk.add(iwTimeNow = new JCheckBox());
            iwTimeNow.setEnabled(false);
            iwTimeNow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(iwStartTime.isEnabled()) {
                        iwStartTime.setEnabled(false);
                    } else {
                        iwStartTime.setEnabled(true);
                    }
                }
            });
            iwDetails.add(startNowChk, igc);

            igc.gridx = 0;
            igc.gridy = 8;
            igc.gridwidth = 2;
            igc.anchor = GridBagConstraints.LAST_LINE_END;
            JPanel iwButtons = new JPanel();
            iwButtons.setBorder(BorderFactory.createLineBorder(Color.black));
            iwButtons.add(iwNew = new JButton("New Item"));
            iwNew.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newItemSetup();
                }
            });
            iwButtons.add(iwEdit = new JButton("Edit Selected Item"));
            iwEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    iwDescription.setEditable(true);
                    iwEndTime.setEnabled(true);
                }
            });
            iwEdit.setEnabled(false);
            iwButtons.add(iwSave = new JButton("Save Changes"));
            iwSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(iwItems.getSelectedValue() == null) {
                        submitNewItem();
                    } else {
                        editExistingItem(iwItems.getSelectedValue());
                    }


                }
            });
            iwSave.setEnabled(false);
            iwDetails.add(iwButtons, igc);

            gc.gridx = 1;
            gc.gridy = 0;
            itemsWindow.add(iwDetails, gc);

            return itemsWindow;

        }

        public void populateBrowseItemsList() {
            try {
                out.writeObject(new Message().new ItemRequest(brCategories.getSelectedValue()));
                System.out.println("Attempting to get items");
                brItemList.setModel(new ItemListModel(currentDispItems));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void editExistingItem(Item item) {
            item.setDescription(iwDescription.getText());
            try {
                PersistanceLayer.addItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void getUserItems(int userID) {
            try {
                out.writeObject(new Message().new ItemRequestByUser(userID));
                System.out.println("Getting items by user");
                sleep(1000);
                iwItems.setModel(new UserItemListModel(requestedUserItems));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void refreshListings() {

        }

        public void displayItemInfo() {
            if(selectedBrowseItem != null) {
                brTitle.setText(selectedBrowseItem.getTitle());
                if(!selectedBrowseItem.getBids().isEmpty()) {
                    brCurrBid.setText("Current Bid: " + String.valueOf(selectedBrowseItem.getHighestBid().getAmount()));
                }
                else {
                    brCurrBid.setText("Reserve Price: " + String.valueOf(selectedBrowseItem.getReservePrice()));
                }
                brStartTime.setText(selectedBrowseItem.getStartTime().toString());
                brEndTime.setText(selectedBrowseItem.getEndTime().toString());
                brDescription.setText(selectedBrowseItem.getDescription());
                //brBidOptions.setVisible(true);
                //seller.setText(selectedBrowseItem.getSeller().getUsername());

            }
        }

        public void placeBid() {
            try {
                out.writeObject(new Message().new ItemBidRequest(loggedUser, Integer.valueOf(brBidAmount.getText()), brItemList.getSelectedValue()));
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        public void displaySelectedUserItemInfo(Item item){
            iwTitle.setText(item.getTitle());
            iwCategory.setSelectedItem(item.getCategory());
            iwReserve.setText(String.valueOf(item.getReservePrice()));
            iwDescription.setText(item.getDescription());
            iwStartTime.setValue(item.getStartTime());
            iwEndTime.setValue(item.getEndTime());
            iwEdit.setEnabled(true);
        }

        public void newItemSetup() {
            editingItem = true;
            iwCurrEdit = null;

            iwTitle.setText("");
            iwReserve.setText("");
            iwDescription.setText("");

            iwTitle.setEditable(true);
            iwCategory.setEnabled(true);
            iwReserve.setEditable(true);
            iwDescription.setEditable(true);
            iwStartTime.setEnabled(true);
            iwEndTime.setEnabled(true);
            iwSave.setEnabled(true);
            iwTimeNow.setEnabled(true);
        }

        public void submitNewItem(){
            if(iwTitle.getText() != null && !iwTitle.getText().equals("")) {
                if(!iwCategory.getSelectedItem().equals("All")) {
                    if(isStringInteger(iwReserve.getText())) {
                        if(Integer.parseInt(iwReserve.getText()) > 0) {
                            if(iwDescription.getText().length() > 40) {
                                Date nowDate = new Date();
                                if(nowDate.before((Date)iwStartTime.getValue()) || iwTimeNow.isSelected()) {
                                    if(nowDate.before((Date)iwEndTime.getValue())) {
                                        try {
                                            out.writeObject(new Message().new ItemListingRequest(new Item(iwTitle.getText(), iwDescription.getText(), iwCategory.getSelectedItem().toString(),
                                                    loggedUser.getUserID(), Integer.parseInt(iwReserve.getText()), (Date)iwStartTime.getValue(), (Date)iwEndTime.getValue())));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "The end date must be in the future!");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Start time must be in the future (or tick the 'Start Now' box)!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Your description must be at least 40 characters long!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "The reserve price must be more than 0!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Reserve price must be a number!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please choose a valid category!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "A title is required!");
            }
        }



        public Boolean isStringInteger(String string) {
            try {
                Integer.parseInt(string);
                return true;
            } catch (Exception e) {

            }
            return false;
        }

        class ItemListModel extends AbstractListModel<Item> {

            public ArrayList<Item> modelItem;

            public ItemListModel(ArrayList<Item> modelItem) {
                this.modelItem = modelItem;
            }

            public int getSize() {
                if(modelItem == null){
                    return 0;
                } else {
                    return modelItem.size();
                }

            }

            public Item getElementAt(int index) {
                return modelItem.get(index);
            }

        }

        class UserItemListModel extends AbstractListModel<Item> {
            public ArrayList<Item> modelItem;

            public UserItemListModel(ArrayList<Item> modelItem) {this.modelItem = modelItem;}
            public int getSize() {
                if(modelItem == null) {
                    return 0;
                } else {
                    return modelItem.size();
                }
            }
            public Item getElementAt(int index) {return modelItem.get(index);}
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
