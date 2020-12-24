/**
 * Created by vince on 4/18/2017.
 */
import java.util.ArrayList;
public class Flush extends PokerHand {
    private char suit;
    private ArrayList<Card> cards;
    //tiebreaker: the flush with the higher card wins. if the highest card is the same, then compare the second highest card
    //and so on until the fifth card. suit never comes into play
    public Flush(char suit, ArrayList<Card> cards){
        super(5);
        this.suit = suit;
        this.cards = cards;
    }

    public char getSuit(){
        return suit;
    }

    //this returns all the cards in the flush for tiebreakers
    public ArrayList<Card> getCards(){
        return cards;
    }

    public String toString(){
        String stringSuit = "";
        if (suit == 's') stringSuit = "spades";
        else if (suit == 'h') stringSuit = "hearts";
        else if (suit == 'c') stringSuit = "clubs";
        else if (suit == 'd') stringSuit = "diamonds";

        return HandEvaluator.rankToString(cards.get(0).getRank()) + " high flush of " + stringSuit;
    }
}
