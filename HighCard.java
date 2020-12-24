import java.util.ArrayList;

/**
 * Created by vince on 4/20/2017.
 */
public class HighCard extends PokerHand {
    private int rank;
    private ArrayList<Card> kickers; //kicker is used in tiebreakers

    public HighCard(int rank, ArrayList<Card> kickers){
        super(10);
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
        return HandEvaluator.rankToString(rank) + " high card";
    }
}
