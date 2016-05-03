import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Joe on 01/05/2016.
 */
public class Item implements Serializable{
    private static String[] categories = {"All", "Fashion", "Home and Garden", "Electronics", "Leaisure", "Collectables", "Health and Beauty", "Motors"};

    private int id;
    private String title;
    private String description;
    private String category;
    private int sellerID;
    private User seller;
    private Date startTime;
    private Date endTime;
    private ArrayList<Bid> bids = new ArrayList<Bid>();

    public Item(String title, String description, String category){
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public static String[] getCategories() {
        return categories;
    }

    public String getCategory() {
        return category;
    }

    public String toString() {
        if(bids.isEmpty()) {
            return title + "\t Be the first to bid on this item!";
        }
        else {
            return title + "\t Current Bid:" + bids.get(bids.size()-1).bidAmount;
        }

    }

    class Bid {
        private int bidderID;
        private int bidAmount;
    }

}
