import java.io.Serializable;

/**
 * Created by Joe on 12/05/2016.
 */
public class Bid implements Serializable{

    private int itemID;
    private int bidderID;
    private int amount;

    public Bid(int itemID, int bidderID, int amount) {
        this.itemID = itemID;
        this.bidderID = bidderID;
        this.amount = amount;

    }

    public int getItemID(){return itemID;}
    public int getBidderID(){return bidderID;}
    public int getAmount(){return amount;}

}
