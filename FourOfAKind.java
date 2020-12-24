/**
 * Created by vince on 4/18/2017.
 */
public class FourOfAKind extends PokerHand {
    //If there are two four of a kind's, the higher ranked one wins
    private int rank;
    public FourOfAKind(int rank){ //The rank is the value of the card, e.g. the rank for jack would be 11
        super(3);
        this.rank = rank;
    }

    public int getRank(){
        return rank;
    }

    public String toString(){
        return "four of a kind, " + HandEvaluator.rankToString(rank) + "'s";
    }
}
