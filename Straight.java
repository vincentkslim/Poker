/**
 * Created by vince on 4/20/2017.
 */
public class Straight extends PokerHand {
    private int highCard;

    //tiebreaker: same as straight flush, higher end card wins, if the same, then split the pot
    public Straight(int highCard){
        super(6);
        this.highCard = highCard;
    }

    public int getHighCard(){
        return highCard;
    }

    public String toString(){
        return HandEvaluator.rankToString(highCard) + " high straight";
    }
}
