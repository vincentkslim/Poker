import java.util.ArrayList;

/**
 * Created by vince on 4/20/2017.
 */
public class ThreeOfAKind extends PokerHand {
    private int rank;
    private ArrayList<Card> kickers;

    //the higher ranked three of a kind wins
    public ThreeOfAKind(int rank, ArrayList<Card> kickers){
        super(7);
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
        return "three of a kind, " + HandEvaluator.rankToString(rank) + "'s";
    }
}
