import java.io.Serializable;
import java.util.*;

/**
 * Created by Joe on 01/05/2016.
 */
public class Item implements Serializable{
    private static String[] categories = {"Fashion", "Home and Garden", "Electronics", "Leisure", "Collectables", "Health and Beauty", "Motors"};

    private int id;
    private String title;
    private String description;
    private String category;
    private int sellerID;
    private int reservePrice;
    private Date startTime;
    private Date endTime;
    private int status; //0=Not Active Yet, 1 = Active, 2=Expired
    public ArrayList<Bid> bids = new ArrayList<Bid>();

    public Item(String title, String description, String category, int sellerID, int reservePrice, Date startTime, Date endTime) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.sellerID = sellerID;
        this.reservePrice = reservePrice;
        this.startTime = startTime;
        this.endTime = endTime;

        Date dateNow = new Date();
        if(dateNow.after(startTime) && dateNow.before(endTime)) {
            status = 1;
        } else if (dateNow.after(endTime)) {
            status = 2;
        } else {
            status = 0;
        }

        System.out.println("Created new item with status " + status);
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
    public ArrayList<Bid>  getBids() {return bids;}
    public int getStatus(){return status;}
    public int getID(){return id;}

    public void setID(int id) {
        this.id = id;
    }
    public void setDescription(String description) {this.description = description;}
    public void setStatus(int status){this.status = status;}

    public String toString() {
        if(bids.isEmpty()) {
            return title + "     |     No Current Bids!";
        }
        else {
            return title + "     |     Current Bid: £" + getHighestBid().getAmount();
        }

    }

    public Bid getHighestBid() {
        Bid max = null;
        for(Bid bid : bids) {
            if(max == null || bid.getAmount() > max.getAmount()) {
                max = bid;
            }
        }
        return max;
    }

    //
    public Bid getHighestBidByUser(int userID) {
        Bid max = null;
        for(Bid bid : bids) {
            if(bid.getBidderID() == userID) {
                if(max == null || bid.getAmount() > max.getAmount()) {
                    max = bid;
                }
            }
        }
        return max;
    }

    public Boolean addBid(int userID, int amount) {
        if(bids.isEmpty() || amount > getHighestBid().getAmount()) {
            bids.add(new Bid(id, userID, amount));
            return true;
        }
        else {
            return false;
        }
    }


}
