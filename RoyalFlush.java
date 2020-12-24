/**
 * Created by vince on 4/18/2017.
 */
public class RoyalFlush extends PokerHand {
    //There will never be two royal flushes at a time, unless there is a wild card. In that case, the pot is split.

    private char suit;

    public RoyalFlush(char suit){
        super(1);
        this.suit = suit;
    }

    public char getSuit(){
        return this.suit;
    }

    public String toString(){
        String stringSuit = "";
        if (suit == 's') stringSuit = "SPADES";
        else if (suit == 'h') stringSuit = "HEARTS";
        else if (suit == 'c') stringSuit = "CLUBS";
        else if (suit == 'd') stringSuit = "DIAMONDS";

        return "ROYAL FLUSH OF " + stringSuit + "!!!";
    }
}
