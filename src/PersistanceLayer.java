import com.google.gson.Gson;

import java.io.*;
import java.util.HashSet;

/**
 * Created by Joe on 28/04/2016.
 */
public class PersistanceLayer {

    private static Gson gson = new Gson();
    private static File users = new File("users.json");

    public static void addUser(User user) throws Exception{
        String userData = gson.toJson(user);
        System.out.println(userData);


        if(!users.exists()) {
            users.createNewFile();
        }

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
            return usersSet;

        } catch(Exception fe) {
            System.out.println(fe);
        }
        return usersSet;
    }

}
