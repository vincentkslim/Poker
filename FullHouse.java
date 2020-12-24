/**Vincent Lim
 * Created on 4/18/2017.
 */
public class FullHouse extends PokerHand {
    //If there are two full houses, the one with the higher three of kind wins, but if those are the same, then the
    //higher pair wins. Otherwise, the plot is split
    private ThreeOfAKind triple;
    private Pair pair;
    public FullHouse(ThreeOfAKind triple, Pair pair){ //triple is the value of the three of a kind, pair is the value of the pair
        super(4);
        this.triple = triple;
        this.pair = pair;
    }

    public ThreeOfAKind getTriple(){
        return this.triple;
    }

    public Pair getPair(){
        return this.pair;
    }

    public String toString(){
        return "full house, " + HandEvaluator.rankToString(triple.getRank()) + "'s over " + HandEvaluator.rankToString(pair.getRank()) + "'s";
    }
}
