import java.util.ArrayList;

/**
 * Created by vince on 4/20/2017.
 */
public class Pair extends PokerHand {
    private int rank;
    private ArrayList<Card> kickers; //the kicker is the tiebreaker

    public Pair(int rank, ArrayList<Card> kickers){
        super(9);
        this.rank = rank;
        this.kickers = kickers;
    }

    public int getRank(){
        return rank;
    }

    public ArrayList<Card> getKickers(){
        return kickers;
    }

    public String toString(){
        return "pair of " + HandEvaluator.rankToString(rank) + "'s";
    }
}
