import java.io.Serializable;
import java.util.*;

/**
 * Created by Joe on 01/05/2016.
 */
public class Item implements Serializable{
    private static String[] categories = {"All", "Fashion", "Home and Garden", "Electronics", "Leisure", "Collectables", "Health and Beauty", "Motors"};

    private int id;
    private String title;
    private String description;
    private String category;
    private int sellerID;
    private int reservePrice;
    private Date startTime;
    private Date endTime;
    //private ArrayList<Bid> bids = new ArrayList<Bid>();
    private LinkedHashMap<Integer, Integer> bids = new LinkedHashMap<>();

    public Item(String title, String description, String category){
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public Item() {
        title = "Test Item";
        description = "Test Description";
        category = "Home and Garden";
        sellerID = 1;
        startTime = new Date();
        endTime = new Date();
        bids = new LinkedHashMap<>();
        bids.put(2, 200);
        bids.put(3, 300);

    }

    public static String[] getCategories() {
        return categories;
    }



    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getCategory() {
        return category;
    }
    public int getSellerID(){return sellerID;}
    public int getReservePrice() {return reservePrice;}
    public Date getStartTime(){return startTime;}
    public Date getEndTime() {return endTime;}
    //public ArrayList<Bid>  getBids() {return bids;}
    public LinkedHashMap<Integer, Integer> getBids(){return bids;}

    public String toString() {
        if(bids.isEmpty()) {
            return title + "        Be the first to bid on this item!";
        }
        else {
            return title + "Current Bid: " + getHighestBid().getValue();
        }

    }

    public Map.Entry<Integer, Integer> getHighestBid() {
        Map.Entry<Integer, Integer> max = null;
        for(Map.Entry<Integer, Integer> entry : bids.entrySet()) {
            if(max == null || entry.getValue() > max.getValue()) {
                max = entry;
            }
        }
        return max;
    }

    class Bid {
        private int bidderID;
        private int bidAmount;

        public int getBidAmount(){return bidAmount;}
    }

}
