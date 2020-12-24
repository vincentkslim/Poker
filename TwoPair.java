import java.util.ArrayList;

/**
 * Created by vince on 4/20/2017.
 */
public class TwoPair extends PokerHand{
    private Pair pair1;
    private Pair pair2;
    private ArrayList<Card> kickers;
    //tiebreaker is based on who has the better pair1, then pair2.
    //pair 1 is always the better pair, e.g. two pair of jacks and threes, pair1 would equal jacks(11)
    public TwoPair(Pair pair1, Pair pair2, ArrayList<Card> kickers){
        super(8);
        this.pair1 = pair1;
        this.pair2 = pair2;
        this.kickers = kickers;
    }

    public Pair getPair1(){
        return pair1;
    }

    public Pair getPair2(){
        return pair2;
    }

    public ArrayList<Card> getKickers(){
        return kickers;
    }

    public String toString(){
		
        return "two pair, " + HandEvaluator.rankToString(pair1.getRank()) + "'s and " + HandEvaluator.rankToString(pair2.getRank()) + "'s";
    }
}
