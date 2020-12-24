//Vincent Lim
//Player.java
//Player object made for each player.
//Houses:
//	Socket, PrintWriter/Reader, Cards
import java.net.*;
import java.io.*;
import java.util.*;
public class Player{
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private Card [] cards;
    private PokerHand bestHand;

    private int money;
    private String name;
    private boolean folded;
    private int amountBet; //amount bet in the current betting round
    private boolean hasBet;

    public Player(Socket socket, int money){
        this.socket = socket;
        try {
            this.writer = new PrintWriter(this.socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("I/O Error when creating input and output stream writers");
        } catch (NullPointerException e){ //DELETE THIS LATER

        } //DELETE THIS LATER

        try{
            this.name = reader.readLine();
            writer.println("Hi "+ name + ", Welcome to Poker!");
        } catch (IOException e){
            e.printStackTrace();
            //System.err.println("IO Error when during readline. Connection is probably closed or reset");
        } catch (NullPointerException e){ //DELETE THIS LATER

        } //DELETE THIS LATER
        this.money = money;
    }

    //DELETE THIS LATER
    public void setName(String s){
        name = s;
    }

    public int getMoney(){
        return this.money;
    }

    public void setMoney(int money){
        this.money = money;
    }
    
    public void betMoney(int x){
		if(x>=money){ //player goes all in
		    x=money;
		    amountBet+=money;
		    money=0;
        }

		money-=x;
		amountBet+=x;
	}

	public void setAmountBet(int x){
        amountBet = x;
    }

	public int getAmountBet(){
        return this.amountBet;
    }
	
	public boolean getFolded(){
		return folded;
	}
	
	public void setFolded(boolean x){
		this.folded = x;
	}

    public Socket getSocket(){
        return this.socket;
    }

    public PrintWriter getWriter(){
        return this.writer;
    }

    public BufferedReader getReader(){
        return this.reader;
    }
    
    public boolean getHasBet(){
		return hasBet;
	}
	
	public void setHasBet(boolean b){
		hasBet = b;
	}

    public void setBestHand(ArrayList<Card> communityCards){ //Community cards are the cards in the center that everyone shares
        ArrayList<Card> temp = new ArrayList<>(communityCards);
        for(int i=0; i<cards.length; i++) temp.add(cards[i]);
        //System.out.println("Combined cards: "+temp.toString());
        //now the ArrayList "temp" has both this player's cards and the community cards
        bestHand = HandEvaluator.returnHighestHand(temp);
        System.out.println(name + " has a hand ranking of " + bestHand.getRanking());
    }

    public PokerHand getBestHand(){
        return bestHand;
    }

    public String getName(){
        return this.name;
    }

    public Card[] getCards(){
        return this.cards;
    }

    public void setCards(Card [] cards){
        this.cards = cards;
    }
}
