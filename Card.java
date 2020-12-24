//Vincent Lim
//Card.java
//A card object created by the player
public class Card {

    private int rank;
    private char suit;
    private boolean dealt;

    public Card(int rank, char suit){
        this.rank = rank;
        this.suit = suit;
        dealt = false;
    }

    public int getRank(){
        return rank;
    }

    public char getSuit(){
        return suit;
    }

    public boolean getDealt(){
        return dealt;
    }

    public void setDealt(boolean b){
        dealt = b;
    }

    public String toString(){
        return "{Rank/Value: " + rank + ", Suit: " + suit + "}";
    }

    //This makes it a bit easier for the computer to parse it, the rank is after 'R' and before 'S', and the suit is
    //the character after 'S'
    public String getComputerReadInfo(){
        return "R" + rank + "S" + suit;
    }
}
