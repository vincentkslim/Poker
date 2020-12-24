/* Vincent Lim - Created: 4/17/17
 * PokerServer.java
 * This is the server for the poker game. The associated client is called
 * PokerClient.java
 * Run this server first, then run the client.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class PokerServer{

    //All the socket variables
    private ArrayList<Player> players;
    private int port;
    private ServerSocket listener;
    
    private ArrayList<Card> cards;
    private ArrayList<Card> communityCards;
    
    private PokerServerGUI gui;

    //Integer key codes-passed to each client, tells each client what is going on
    private final int ANOTHER_PLAYERS_TURN = 0;
    private final int YOUR_TURN = 1;
    private final int WAITING_FOR_PLAYERS_TO_CONNECT = 2;
    private final int PRE_FLOP = 3;
    private final int SECOND_BETTING_ROUND = 4;
    private final int THIRD_BETTING_ROUND = 5;
    private final int LAST_BETTING_ROUND = 6;

    private boolean waiting; 	//if true, the server continues to wait for players to connect
    public static int money; //Amount of money everyone starts with
    public static int smallBlind; //Small Blind amount
    public static int bigBlind; //Big Blind amount
    public static int maxPlayers; //max amount of players
    private int pot; //current pot size

    private int playerAfterBlind;

    public static String IPAddress;

    public static void main(String [] args){
        /**
        if(args.length != 1){
            System.err.println("Usage: java PokerServer <port>");
            System.exit(1);
        }*/
        try {
            //System.out.println(Inet4Address.getLocalHost().getHostAddress());
            IPAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
        PokerServer server = new PokerServer();
        server.run();
    }
    public PokerServer(){
        port = 6969;
        //defaults
        smallBlind = 5;
        bigBlind = 10;
        maxPlayers = 8;
        money = 500;

        playerAfterBlind = 1;
        try{
            listener = new ServerSocket(port); //Port will soon be changeable
        } catch (IOException e){
            System.out.println("I/O Error when creating ServerSocket");
            System.exit(1);
        }
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
        cards = new ArrayList<>();
        gui = new PokerServerGUI();

        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));

        //Checks if all the players are connected
        //Thread connectionChecker = new Thread(new ConnectionChecker());
        //connectionChecker.start();
    }
    public void run(){
		//This is the main method of the game
		//this loops through the entire game
		//step one-wait for players
		//two-start game
		//Game phases:
        // Pre-flop: blinds, and bets
        makeCards();
        waitForPlayers();

        sendSystemMessageToAll("---------------Betting Round---------------");

        while (true) {

            checkForLosers();

            playerAfterBlind++;
            if (playerAfterBlind == players.size()) playerAfterBlind = 0;

            reset();

            dealCards();
            sendKeyCode(PRE_FLOP, null);

            sendSystemMessageToAll("-----Pre-flop-----");
            preFlopBetting();
            if (bet(playerAfterBlind, false)) continue;//skips the rest of the loop if the round is over.

            sendSystemMessageToAll("-----Second Betting Round-----");
            addCommunityCards(3);
            sendKeyCode(SECOND_BETTING_ROUND, null);
            sendPlayerInfo();

            int smallBlindIndex = playerAfterBlind - 2;
            if (smallBlindIndex < 0) smallBlindIndex += players.size();

            if(bet(smallBlindIndex, true)) continue;

            sendSystemMessageToAll("-----Third Betting Round-----");
            addCommunityCards(1);
            sendKeyCode(THIRD_BETTING_ROUND, null);
            if(bet(smallBlindIndex, true)) continue;

            sendSystemMessageToAll("-----Final Betting Round-----");
            addCommunityCards(1);
            sendKeyCode(LAST_BETTING_ROUND, null);
            if(bet(smallBlindIndex, true)) continue;

            showdown();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e){
                //do nothing
            }
            sendSystemMessageToAll("---------------Next Betting Round---------------");
        }
    }
    
    public void reset(){
		for(int i=0; i<cards.size(); i++) cards.get(i).setDealt(false);

        for (int i = 0; i < players.size(); i++) {
            Player player =  players.get(i);
            player.setFolded(false);
            player.setHasBet(false);
            player.setAmountBet(0);
        }

        communityCards.clear();
        gui.setCommunityCards(communityCards);
        pot = 0;
        gui.setPot(pot);
    }

    public void checkForLosers(){
        for (int i = 0; i < players.size(); i++) {
            Player player =  players.get(i);
            if (player.getMoney() <= 0){
                player.getWriter().println("YOU_LOST");
                players.remove(player);
            }
        }
        gui.repaint();
    }
	
	public void dealCards(){
		for(int i=0; i<players.size(); i++){
			Card [] cards = {getRandomCard(), getRandomCard()};
			players.get(i).setCards(cards);
		}
		sendPlayerInfo();
	}

	public void showdown(){
	    //This is the last part of each round
        //This gets the highest hand in each of the player's hands
        //Then gets the all of their best hands
        //and determines which hand is the best.
        ArrayList<Player> notFolded = new ArrayList<>();
        for(int i=0; i<players.size(); i++) //puts all the players that have not folded into a new ArrayList
            if(!players.get(i).getFolded())
                notFolded.add(players.get(i));

        for (int i = 0; i < notFolded.size(); i++)
            notFolded.get(i).setBestHand(communityCards);

        ArrayList<Player> winners = HandEvaluator.returnBestPlayerHand(notFolded);
        for (int i=0; i<winners.size(); i++){
            sendSystemMessageToAll(winners.get(i).getName() + " won with a " + winners.get(i).getBestHand().toString());
            winners.get(i).setMoney(winners.get(i).getMoney() + (pot/winners.size()));
        }
    }
	
	public boolean bet(int startIndex, boolean resetAmountBet){
        sendPlayerInfo();
		if(resetAmountBet){ //resets betting round variables, needed because of the preflop blinds
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setAmountBet(0);
                players.get(i).setHasBet(false);
            }
        }

		int better = startIndex;
		
		int eachPlayerBet = 0; //How much each player has to bet to stay in the round

		for(int i=0; i<players.size(); i++)
			if(players.get(i).getAmountBet() > eachPlayerBet)
				eachPlayerBet = players.get(i).getAmountBet();
		
		boolean keepBetting = true;
		
		//System.out.println(eachPlayerBet);
                
        while(keepBetting){
            sendPlayerInfo();
			//System.out.println("Asking " + players.get(better).getName() + " for a bet");
            sendKeyCode(YOUR_TURN, players.get(better));
            if (players.get(better).getMoney() <= 0){
                players.get(better).getWriter().println("ALL_IN");
                players.get(better).setHasBet(true);
            }
			if(!players.get(better).getFolded()) {
                Player player = players.get(better);
                System.out.println("Asking " + players.get(better).getName() + " for a bet");

                System.out.println("Each player has to bet: " + eachPlayerBet);
                System.out.println(player.getName() + " has bet: " + player.getAmountBet());
                //the eachPlayerBet is == to how much the big blind has bet in the preflop, need to fix this
                if (eachPlayerBet == 0) {
                    System.out.println(player.getName() + " has the option to check, bet, or fold.");
                    player.getWriter().println("CHECK_BET_FOLD");
                    try {
                        String choice = player.getReader().readLine();
                        //System.out.println(choice);
                        if (choice.charAt(0) == 'C') {
                            System.out.println(player.getName() + " checked");
                            sendSystemMessageToAll(player.getName() + " checked");
                            player.setHasBet(true);
                        } else if (choice.charAt(0) == 'B') {
                            int bet = Integer.parseInt(choice.substring(2));
                            System.out.println(player.getName() + " bet " + bet);
                            sendSystemMessageToAll(player.getName() + " bet $" + bet);
                            player.betMoney(bet);
                            eachPlayerBet+=bet;
                            player.setHasBet(true);

                            pot+= bet;
                        } else if (choice.charAt(0) == 'F') {
							sendSystemMessageToAll(player.getName() + " folded");
                            player.setFolded(true);
                        }
                    } catch (IOException e) {
                        error(e, player);
                    }
                } else if (player.getAmountBet() < eachPlayerBet){
                    System.out.println(player.getName() + " has the option to raise, call, or fold.");
                    player.getWriter().println("RAISE_CALL_FOLD");
                    player.getWriter().println(eachPlayerBet);
                    player.getWriter().println(player.getAmountBet());
                    try{
                        String choice = player.getReader().readLine();
                        //System.out.println(choice);
                        if (choice.charAt(0) == 'R') {
                            int bet = Integer.parseInt(choice.substring(2).trim());
                            //System.out.println(player.getName() + " raised " + bet);
                            sendSystemMessageToAll(player.getName() + " raised to $" + bet);
                            player.betMoney(bet-player.getAmountBet());
                            player.setHasBet(true);

                            eachPlayerBet = bet;

                            pot+=bet;
                        } else if (choice.charAt(0) == 'C') {
                            //System.out.println(player.getName() + " called the bet");
                            sendSystemMessageToAll(player.getName() + " called and paid $" + (eachPlayerBet - player.getAmountBet()));
                            pot += (eachPlayerBet - player.getAmountBet());
                            player.betMoney(eachPlayerBet - player.getAmountBet());
                            player.setHasBet(true);
                        } else if (choice.charAt(0) == 'F') {
							sendSystemMessageToAll(player.getName() + " folded");
                            player.setFolded(true);
                        }
                    } catch (IOException e){
                        error(e, player);
                    }
                } else if (player.getAmountBet() == eachPlayerBet && eachPlayerBet != 0){
                    //only occurs when in the preflop, where the big blind ends the round. he/she can either, check to end the round,
                    //raise if he's confident, or fold if he's really stupid
                    System.out.println(player.getName() + " can either check, raise, or fold");
                    player.getWriter().println("CHECK_RAISE_FOLD");
                    try{
                        String choice = player.getReader().readLine();
                        if(choice.charAt(0) == 'C'){
                            sendSystemMessageToAll(player.getName() + " checked");
                            player.setHasBet(true);
                        } else if (choice.charAt(0) == 'R'){
                            int bet = Integer.parseInt(choice.substring(2).trim());
                            sendSystemMessageToAll(player.getName() + " raised to $" + bet);
                            eachPlayerBet = bet;
                            player.betMoney(bet);
                            player.setHasBet(true);

                            pot += bet;
                        } else if(choice.charAt(0) == 'F'){
                            sendSystemMessageToAll(player.getName() + " folded"); //why did I even include this as an option?
                            player.setFolded(true);
                        }
                    } catch (IOException e){
                        error(e, player);
                    }
                }
                gui.setPot(pot);
                sendPlayerInfo();
            }

            int condition = checkWinCondition();
            if(condition == 0) keepBetting = false;
            else if (condition == 1) keepBetting = true;
            else if (condition == 2) {
                for (int i = 0; i < players.size(); i++) {
                    if (!players.get(i).getFolded()) {
                        players.get(i).setMoney(players.get(i).getMoney() + pot);
                        sendSystemMessageToAll(players.get(i).getName() + " won and has received $" + pot);
                    }
                }
                return true; //The entire round is over as everyone has already folded, no need to continue it
            }
            sendKeyCode(ANOTHER_PLAYERS_TURN, players.get(better));

			better++;
			if(better == players.size()) better = 0;

            sendPlayerInfo();
        }
        
        //since the betting round is over, reset the hasbet variable
        for(int i=0; i<players.size(); i++) players.get(i).setHasBet(false);
        return false;
	}

	public void sendSystemMessageToAll(String s){
		for(int i=0; i<players.size(); i++){
			players.get(i).getWriter().println("SYSTEM_MESSAGE");
			players.get(i).getWriter().println(s);
			players.get(i).getWriter().println("END_SYSTEM_MESSAGE");
		}
	}

	public int checkWinCondition(){
	    //returns 0 if betting round is over, everyone has bet the same amount
        //returns 1 if the betting round needs to continue.
        //returns 2 if the entire round is over and somebody won

        //Checks if only one person hasn't folded yet by counting the amount of people who haven't folded yet.
        int folded = 0;
        for (int i = 0; i < players.size(); i++)
            if(!players.get(i).getFolded()) folded++;

        if (folded == 1) return 2;

        ArrayList<Player> notFolded = new ArrayList<>();
        for(int i=0; i<players.size(); i++) //puts all the players that have not folded into a new arraylist
            if(!players.get(i).getFolded())
                notFolded.add(players.get(i));

        for (int i=0; i<notFolded.size(); i++) {
            if (!notFolded.get(i).getHasBet()) {
                System.out.println(notFolded.get(i).getName() + " has not bet yet.");
                return 1;
            }
        }

        System.out.println("Everyone has bet, time to check for win condition");
        for (int j = 1; j < notFolded.size(); j++) //then checks if they have all bet the same amount
            if (notFolded.get(0).getAmountBet() != notFolded.get(j).getAmountBet())
                return 1; //If anyone's bet is different, then the betting round needs to continue.

        return 0;
    }

	public void preFlopBetting(){
	    gui.repaint();
	    sendPlayerInfo();
        sendKeyCode(PRE_FLOP, null);
        for (int i = 0; i < players.size(); i++)
            players.get(i).setAmountBet(0);

        int bigBlindIndex = playerAfterBlind-1;
        if(bigBlindIndex < 0) bigBlindIndex += players.size();
        int smallBlindIndex = playerAfterBlind-2;
        if(smallBlindIndex < 0) smallBlindIndex += players.size();

        System.out.println(players.get(smallBlindIndex).getName() + " is the small blind"); //get the small blind
        System.out.println(players.get(bigBlindIndex).getName() + " is the big blind"); //get the big blind

        players.get(smallBlindIndex).getWriter().println("YOU_ARE_SMALL_BLIND");
        players.get(smallBlindIndex).betMoney(smallBlind);
        players.get(smallBlindIndex).setAmountBet(smallBlind);
        //players.get(smallBlindIndex).setHasBet(true);
        pot += smallBlind;

        players.get(bigBlindIndex).getWriter().println("YOU_ARE_BIG_BLIND");
        players.get(bigBlindIndex).betMoney(bigBlind);
        players.get(bigBlindIndex).setAmountBet(bigBlind);
        //players.get(bigBlindIndex).setHasBet(true);
        pot += bigBlind;

        sendSystemMessageToAll(players.get(smallBlindIndex).getName() + " is the small blind.");
        sendSystemMessageToAll(players.get(bigBlindIndex).getName() + " is the big blind.");

        gui.repaint();
        sendPlayerInfo();
        gui.repaint();
    }
	
	public void makeCards(){
		for(int i=1; i<=13; i++) cards.add(new Card(i, 'c'));
		for(int i=1; i<=13; i++) cards.add(new Card(i, 'd'));
		for(int i=1; i<=13; i++) cards.add(new Card(i, 'h'));
		for(int i=1; i<=13; i++) cards.add(new Card(i, 's'));
	}
	
	public void addCommunityCards(int number){
		//adds the number of cards to the community cards
		for(int i=0; i<number; i++) communityCards.add(getRandomCard());

        sendPlayerInfo();
		gui.setCommunityCards(communityCards);
		gui.repaint();
	}
	
	
	public Card getRandomCard(){
		int index = (int)(Math.random()*52);
		if(cards.get(index).getDealt()) return getRandomCard();
		else{
			cards.get(index).setDealt(true);
			return cards.get(index);
		}
	}
	
	public void sendPlayerInfo(){
		for(int k=0; k<players.size(); k++){
			Player player = players.get(k);
			player.getWriter().println(">>PLAYER_INFO<<"); //Start player info heading

            //Basic easy to parse info first
            player.getWriter().println("M" + player.getMoney());
            player.getWriter().println("SB" + smallBlind);
            player.getWriter().println("BB" + bigBlind);
            player.getWriter().println("F" + player.getFolded());
            player.getWriter().println("S" + players.size());
            player.getWriter().println("P" + pot);

            player.getWriter().println(">>PC<<"); //PC stands for "player's cards"
            try {
                for (int i = 0; i < player.getCards().length; i++)
                    player.getWriter().println(player.getCards()[i].getComputerReadInfo());
            } catch (NullPointerException e){
                //System.err.println("Cards have not been dealt yet.");
            }
            player.getWriter().println(">>EPC<<"); //EPC stands for "end player's cards"

            player.getWriter().println(">>CC<<"); //CC stands for "Community Cards"
			for(int i=0; i<communityCards.size(); i++) 
				player.getWriter().println(communityCards.get(i).getComputerReadInfo());
            player.getWriter().println(">>ECC<<"); //ECC stands for "End Community Cards"

			player.getWriter().println(">>END_PLAYER_INFO<<");
		}
	}
    public void sendKeyCode(int keyCode, Player player){
        //All key codes are preceded by two forward slashes. For
        //example, the server might send "//2", which means key code 2
        if(player == null) {
            for (int i = 0; i < players.size(); i++)
                players.get(i).getWriter().println("//" + keyCode);
        } else{
            player.getWriter().println("//" + keyCode);
        }
    }

    public void waitForPlayers(){
	    //This method waits for players to connect to the server
        waiting = true;
        while(waiting){
            sendKeyCode(WAITING_FOR_PLAYERS_TO_CONNECT, null);
            try {
				Player tempPlayer = new Player(listener.accept(), money);
				//System.out.println("A Player has connected");
                players.add(tempPlayer);
            } catch (IOException e){
                e.printStackTrace();
            }
            gui.setPlayers(players);
            gui.repaint();
			if(players.size() == maxPlayers) waiting = false;
        }
        gui.setPlayers(players);
        gui.repaint();
    }

    public void error(Exception e, Player player){
        e.printStackTrace();
        players.remove(player);
        gui.repaint();
    }

    public void checkConnection(){
        //This checks if the players are connected. It tries to read and write from the socket.
        //If an exception is thrown, then that means that the player has disconnected and the socket has been closed.
        for (int i = 0; i < players.size(); i++) {
            Player player =  players.get(i);
            player.getWriter().println("CHECKING_CONNECTION");
            try{
                String response = player.getReader().readLine();
                //if(response.equals("CONNECTED")) System.out.println(player.getName() + " is still connected");
            } catch (IOException e){
                //e.printStackTrace();
                //players.remove(player);
                error(e, player);
            }
            gui.repaint();
        }
    }

    class ConnectionChecker implements ActionListener, Runnable{
        public void actionPerformed(ActionEvent e){
            //System.out.println("Checking connection");
            checkConnection();
        }
        public void run(){
            Timer timer = new Timer(5000, this);
            timer.start();
        }
    }

    class ShutdownHook implements Runnable{
        public void run(){
            for (int i = 0; i < players.size(); i++)
                players.get(i).getWriter().println("SERVER_SHUT_DOWN");
        }
    }
}

