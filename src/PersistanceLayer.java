import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Joe on 28/04/2016.
 */
public class PersistanceLayer {

    //I use the gson library to turn my objects in json format. The resulting files do not follow
    //json format precisely, but can easily be changed back into objects using the library.

    private static Gson gson = new Gson();
    private static File users = new File("users.json");
    private static File items = new File("items.json");
    private static File bids = new File("bids.json");

    public static void addUser(User user) throws Exception{
        String userData = gson.toJson(user);
        System.out.println(userData);


        users.createNewFile();

        FileWriter fw = new FileWriter(users, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(userData+"\n");
        bw.close();
    }

    public static HashSet<User> loadAllUsers() {
        HashSet<User> usersSet = new HashSet<User>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(users));

            String line;
            while((line = br.readLine()) != null) {
                usersSet.add(gson.fromJson(line, User.class));
            }

        } catch(Exception fe) {
            System.out.println(fe);
        }
        return usersSet;
    }

    public static void addItem(Item item) throws Exception {
        String itemData = gson.toJson(item);
        System.out.println(itemData);

        items.createNewFile();

        FileWriter fw = new FileWriter(items, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(itemData+"\n");
        bw.close();
    }


    public static ArrayList<Item> loadAllItems() {
        ArrayList<Item> itemsSet = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(items));

            String line;

            //Whenever an item is updated, it is added to the file. This results in duplicates
            //in the file, which are handled on load by casting out all but the most recent object

            while((line = br.readLine()) != null) {
                Item newItem = gson.fromJson(line, Item.class);
                for(Iterator<Item> it = itemsSet.iterator(); it.hasNext();) {
                    Item item = it.next();
                    if(item.getID() == newItem.getID()) {
                        it.remove();
                    }
                }
                itemsSet.add(gson.fromJson(line, Item.class));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }


        return itemsSet;
    }

    public static void addBid(Bid bid) throws Exception {
        String bidData = gson.toJson(bid);
        System.out.println(bidData);

        bids.createNewFile();

        FileWriter fw = new FileWriter(bids, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(bidData+"\n");
        bw.close();
    }


}
