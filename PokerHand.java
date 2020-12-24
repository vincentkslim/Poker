/**
 * Created by vince on 4/18/2017.
 */
public class PokerHand {
    protected int ranking; //from 1-10, 1 being the best and 10 being the worst

    public PokerHand(int ranking){
        this.ranking = ranking;
    }

    public int getRanking(){
        return this.ranking;
    }

    public String toString(){
        return "Generic PokerHand";
    }
}
