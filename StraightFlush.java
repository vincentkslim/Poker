/**
 * Created by vince on 4/20/2017.
 */
public class StraightFlush extends PokerHand {
    private char suit;
    private int highCard;
    //tiebreaker: if there are two straight flushes with the same highest card, then the pot is split. suit never comes into play
    public StraightFlush(char suit, int highCard){
        super(2);
        this.suit = suit;
        this.highCard = highCard;
    }

    public char getSuit(){
        return suit;
    }

    public int getHighCard(){
        return highCard;
    }

    public String toString(){
        String stringSuit = "";
        if (suit == 's') stringSuit = "spades";
        else if (suit == 'h') stringSuit = "hearts";
        else if (suit == 'c') stringSuit = "clubs";
        else if (suit == 'd') stringSuit = "diamonds";

        return HandEvaluator.rankToString(highCard) + " high flush of " + stringSuit;
    }
}
