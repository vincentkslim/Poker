/* Vincent Lim
 * PokerClient.java
 * Client side program of the poker game
 */
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public class PokerClient extends JFrame implements ActionListener, Runnable {
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String HOST;
	private int PORT;
	
	private ArrayList<Card> hand;
	private ArrayList<Card> communityCards;
	private int smallBlind;
	private int bigBlind;
	private int money;
	private String name;
	private boolean folded;
	private int numberOfPlayers;
	private int pot;

    private final Color DEFAULT_COLOR = new Color(13, 97 ,29);
    private final String START_PANEL = "StartPanel";
    private final String PLAY_PANEL = "PlayPanel";
    private final String HOW_TO_PLAY_PANEL = "HowToPlayPanel";

    private CardLayout cardLayout;
    private StartPanel startPanel;
    private PlayPanel playPanel;
    private HowToPlayPanel howToPlayPanel;
    private Image cardImage = new ImageIcon("cards.png").getImage();

	public static void main(String [] args){
		PokerClient hugo = new PokerClient();
	}
	public PokerClient(){
		super("Poker Client");
		setSize(1600,1000);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cardLayout = new CardLayout();
        setLayout(cardLayout);
        startPanel = new StartPanel();
        playPanel = new PlayPanel();
        howToPlayPanel = new HowToPlayPanel();
        add(startPanel, START_PANEL);
        add(playPanel, PLAY_PANEL);
        add(howToPlayPanel, HOW_TO_PLAY_PANEL);

        cardLayout.show(getContentPane(), START_PANEL);

		HOST = "localhost";
		PORT = 6969;
		hand = new ArrayList<>();
		communityCards = new ArrayList<>();
        setVisible(true);
	}
	public void actionPerformed(ActionEvent e){

	}

	public String connectToServer(){
        try {
            socket = new Socket(HOST, PORT);
            writer = new PrintWriter(socket.getOutputStream(),true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            name = JOptionPane.showInputDialog(this, "Enter your name: ", "Name", JOptionPane.DEFAULT_OPTION);
            writer.println(name);

        } catch(UnknownHostException e){
            System.err.println("Was not able to connect to " + HOST);
            System.err.println("Please make sure the host's IP Address is correct. Please try again.");
            e.printStackTrace();
            return "Was not able to connect to " + HOST;
        } catch (IOException e){
            System.err.println("IOException");
            e.printStackTrace();
            return "I/O exception occurred when creating the socket. Please try again";
        }

        return null;
    }
	public void serverClientCommunication(){
		String input;
		try {
            while ((input = reader.readLine()) != null) {
                //This just checks the input against various codes that notify the client what the server is asking for
                if(input.equals("YOU_ARE_SMALL_BLIND")){ //Server is notifying the client that they are the small blind
                    playPanel.appendString("You are the small blind. You have automatically paid $" + smallBlind);
                } else if(input.equals("YOU_ARE_BIG_BLIND")){
                    playPanel.appendString("You are the big blind. You have automatically paid $" + bigBlind);
                } else if(input.equals(">>PLAYER_INFO<<")){
                    String tempInput;
                    while(!((tempInput = reader.readLine()).equals(">>END_PLAYER_INFO<<"))){
                        if(tempInput.charAt(0) == 'M'){
                            money = Integer.parseInt(tempInput.substring(1).trim());
                        } else if (tempInput.substring(0, 2).equals("SB")){
                            smallBlind = Integer.parseInt(tempInput.substring(2).trim());
                        } else if (tempInput.substring(0, 2).equals("BB")){
                            bigBlind = Integer.parseInt(tempInput.substring(2).trim());
                        } else if (tempInput.charAt(0) == 'F'){
                            folded = Boolean.parseBoolean(tempInput.substring(1).trim());
                        } else if(tempInput.charAt(0) == 'S'){
                            numberOfPlayers = Integer.parseInt(tempInput.substring(1).trim());
                        } else if (tempInput.charAt(0) == 'P') {
                            pot = Integer.parseInt(tempInput.substring(1).trim());
                        } else if (tempInput.equals(">>CC<<")){
                            communityCards.clear();
                            String tempCommunityCard;
                            while(!((tempCommunityCard = reader.readLine()).equals(">>ECC<<"))){
                                int rank = Integer.parseInt(tempCommunityCard.substring(tempCommunityCard.indexOf('R')+1, tempCommunityCard.indexOf('S')));
                                char suit = tempCommunityCard.charAt(tempCommunityCard.indexOf('S') + 1);
                                communityCards.add(new Card(rank, suit));
                            }
                        } else if(tempInput.equals(">>PC<<")){
                            hand.clear();
                            String tempPlayerCard;
                            while (!((tempPlayerCard = reader.readLine()).equals(">>EPC<<"))){
                                int rank = Integer.parseInt(tempPlayerCard.substring(tempPlayerCard.indexOf('R')+1, tempPlayerCard.indexOf('S')));
                                char suit = tempPlayerCard.charAt(tempPlayerCard.indexOf('S') + 1);
                                hand.add(new Card(rank, suit));
                            }
                        }
                        repaint();
                    }
                    repaint();
                } else if(input.equals("CHECKING_CONNECTION")){
                    writer.println("CONNECTED");
                } else if(input.equals("CHECK_BET_FOLD")){
                    checkBetFold();
                } else if(input.equals("RAISE_CALL_FOLD")){
					raiseCallFold();
				} else if (input.equals("CHECK_RAISE_FOLD")){
                    checkRaiseFold();
                } else if(input.equals("SYSTEM_MESSAGE")){
					String tempInput;
					while(!(tempInput = reader.readLine()).equals("END_SYSTEM_MESSAGE"))
						playPanel.appendString(tempInput);
				} else if (input.equals("SERVER_SHUT_DOWN")){
                    JOptionPane.showMessageDialog(this, "Server has shut down", "Error", JOptionPane.WARNING_MESSAGE);
                    System.exit(1);
                } else if (input.equals("RESET_COMMUNITY_CARDS")){
				    communityCards.clear();
                } else if (input.equals("ALL IN")){
                    JOptionPane.showMessageDialog(this, "You are all in. Good Luck!!!", "Good luck kid", JOptionPane.INFORMATION_MESSAGE);
                } else if (input.equals("YOU_LOST")){
                    JOptionPane.showMessageDialog(this, "You Lost!!!", "GG", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else if (input.substring(0, 2).equals("//")){
				    int keyCode = Integer.parseInt(input.substring(2));
				    decipherKeyCode(keyCode);
                } else {
                    System.out.println(input);
                    playPanel.appendString(input);
                }
                repaint();
            }
        } catch (IOException e){
            //System.err.println("IOException");
            //e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server has shut down", "Error", JOptionPane.WARNING_MESSAGE);
            System.exit(-1);
        }
	}

    public void checkRaiseFold() {
        int choice = -1;
        while (choice == -1) {
            Object[] options = {"Check", "Raise", "Fold"};
            choice = JOptionPane.showOptionDialog(this, "Choose an option: ", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if(choice == -1){
                JOptionPane.showMessageDialog(this, "Please choose an option.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            if(choice == 0) {
                writer.println("C");
            }
            else if(choice == 1) {
                JPanel message = new JPanel(new GridLayout(2, 0));
                final JSlider slider = new JSlider(bigBlind, money);
                slider.setValue(bigBlind);
                final JLabel label = new JLabel("$" + bigBlind);
                class SliderHandler implements ChangeListener{
                    public void stateChanged(ChangeEvent e){
                        label.setText("$" + slider.getValue());
                    }
                }
                slider.addChangeListener(new SliderHandler());
                message.add(label);
                message.add(slider);
                int option = JOptionPane.showOptionDialog(this, message, "How much do you want to raise?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if(option == 0){
                    writer.println("R " + slider.getValue());
                }
                else if(option == -1 || option == 2) {
                    choice = -1;
                    continue;
                }
            } else if(choice == 2){
                writer.println("F");
            }
        }
    }

    public void checkBetFold(){
		int choice = -1;
		while (choice == -1) {
			Object[] options = {"Check", "Bet", "Fold"};
			choice = JOptionPane.showOptionDialog(this, "Nobody has bet yet.", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if(choice == -1){
				JOptionPane.showMessageDialog(this, "Please choose an option.", "Error", JOptionPane.ERROR_MESSAGE);
			}

			if(choice == 0) {
			    writer.println("C");
            }
			else if(choice == 1) {
				JPanel message = new JPanel(new GridLayout(2, 0));
                final JSlider slider = new JSlider(bigBlind, money);
				slider.setValue(bigBlind);
                final JLabel label = new JLabel("$" + bigBlind);
                class SliderHandler implements ChangeListener{
					public void stateChanged(ChangeEvent e){
						label.setText("$" + slider.getValue());
					}
				}
				slider.addChangeListener(new SliderHandler());
				message.add(label);
				message.add(slider);
				int option = JOptionPane.showOptionDialog(this, message, "How much do you want to bet?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(option == 0){
					writer.println("B " + slider.getValue());
				}
				else if(option == -1 || option == 2) {
					choice = -1;
					continue;
				}
			} else if(choice == 2){
				writer.println("F");
			}
		}
	}

	public void decipherKeyCode(int keyCode){
	    if (keyCode == 0) setTitle("Waiting for another player");
        else if (keyCode == 1) setTitle("Your Turn");
        else if (keyCode == 2) setTitle("Waiting for players to connect...");
        else if (keyCode == 3) setTitle("Pre-flop");
        else if (keyCode == 4) setTitle("Second betting round");
        else if (keyCode == 5) setTitle("Third betting round");
        else if (keyCode == 6) setTitle("Last betting round");
    }

	public void raiseCallFold(){
		int currentBet = 0;
		int amountBet = 0;
		try{
			currentBet = Integer.parseInt(reader.readLine());
			amountBet = Integer.parseInt(reader.readLine());
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		int choice = -1;
		while (choice == -1) {
            JPanel panel = new JPanel(new GridLayout(3, 1));
            panel.add(new JLabel("Current bet: " + currentBet));
            panel.add(new JLabel("You have bet: " + amountBet));
            panel.add(new JLabel("Amount needed to call: " + (currentBet-amountBet)));
			Object[] options = {"Raise", "Call", "Fold"};
			choice = JOptionPane.showOptionDialog(this, panel, "Choose an option", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if(choice == -1){
				JOptionPane.showMessageDialog(this, "Please choose an option.", "Error", JOptionPane.ERROR_MESSAGE);
			}

			if(choice == 0) {
				JPanel message = new JPanel(new GridLayout(2, 0));
                final JSlider slider = new JSlider(currentBet+bigBlind, money);
				slider.setValue(currentBet+bigBlind);
                final JLabel label = new JLabel("$" + (currentBet+bigBlind));
				class SliderHandler implements ChangeListener{
					public void stateChanged(ChangeEvent e){
						label.setText("$" + slider.getValue());
					}
				}
				slider.addChangeListener(new SliderHandler());
				message.add(label);
				message.add(slider);
				int option = JOptionPane.showOptionDialog(this, message, "Raise?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(option == 0){
					writer.println("R " + slider.getValue());
				}
				else if(option == -1 || option == 2) {
					choice = -1;
					continue;
				}
			}
			else if(choice == 1) {
                writer.println("C");
            }
			else if(choice == 2) {
                writer.println("F");
            }
		}
	}

	public void run(){
	    serverClientCommunication();
    }

    class StartPanel extends JPanel implements ActionListener{

        private JButton start;
        private JButton howToPlay;

        public StartPanel(){
            super(null);
            setSize(PokerClient.this.getWidth(), PokerClient.this.getHeight());
            setBackground(DEFAULT_COLOR);

            start = new JButton("Start");
            start.setBounds(getWidth()/2 - 75, getHeight()/2 - 20, 150, 40);
            start.addActionListener(this);
            add(start);

            howToPlay = new JButton("How to Play");
            howToPlay.setBounds(getWidth()/2 - 75, getHeight()/2 + 40, 150, 40);
            howToPlay.addActionListener(this);
            add(howToPlay);
        }

        public void actionPerformed(ActionEvent e){
            if(e.getActionCommand().equals("Start")){
                HOST = JOptionPane.showInputDialog(this, "Enter the IP Address of the Server:", "Connect to Server", JOptionPane.QUESTION_MESSAGE);
                if(HOST == null){} //Do nothing if they press cancel
                else if(HOST.equals("")) JOptionPane.showMessageDialog(this, "Please input a hostname", "Error", JOptionPane.WARNING_MESSAGE);
                else {
                    String returnMessage;
                    if((returnMessage = connectToServer()) == null) {
                        cardLayout.show(getContentPane(), PLAY_PANEL);
                        new Thread(PokerClient.this).start();
                    }
                    else JOptionPane.showMessageDialog(this, returnMessage, "Error", JOptionPane.WARNING_MESSAGE);
                }
            } else if (e.getActionCommand().equals("How to Play")){
                cardLayout.show(getContentPane(), HOW_TO_PLAY_PANEL);
            }
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setFont(new Font("Helvetica", Font.PLAIN, 50));
            g.setColor(Color.WHITE);
            g.drawString("Poker", getWidth()/2 - g.getFontMetrics().stringWidth("Poker")/2, getHeight()/2 - 50);
            start.setBounds(getWidth()/2 - 75, getHeight()/2 - 20, 150, 40);
            howToPlay.setBounds(getWidth()/2 - 75, getHeight()/2 + 40, 150, 40);
        }
    }

    class PlayPanel extends JPanel implements ActionListener {

        //I use variables for the card image dimensions so that I can use different card images in the future (better, higher quality card image)
        private final int CARD_IMAGE_WIDTH = 79; //width of the card image
        private final int CARD_IMAGE_HEIGHT = 123; //height of the card image

        private JTextArea informationBox;
        private JScrollPane scrollPane;

        public PlayPanel(){
            super(new BorderLayout());
            setSize(PokerClient.this.getWidth(), PokerClient.this.getHeight());
            setBackground(DEFAULT_COLOR);
            informationBox = new JTextArea(5, 50);
            informationBox.setEditable(false);
            informationBox.setLineWrap(true);
            informationBox.setWrapStyleWord(true);
            scrollPane = new JScrollPane(informationBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setPreferredSize(new Dimension(3*getWidth()/10, 16*getHeight()/20));
            add(scrollPane, BorderLayout.EAST);
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setFont(new Font("Helvetica", Font.PLAIN, 40));
            g.setColor(Color.WHITE);
            g.drawString("Name: " + name, getWidth()/100, getHeight()/20);
            g.drawString("Money: $" + money, getWidth()/100, 2*getHeight()/20);
            g.drawString("Folded: " + folded, getWidth()/100, 3*getHeight()/20);
            g.drawString("Big Blind: $" + bigBlind, 4*getWidth()/10, getHeight()/20);
            g.drawString("Small Blind: $" + smallBlind, 4*getWidth()/10, 2*getHeight()/20);
            g.drawString("Number Of Players: " + numberOfPlayers, 4*getWidth()/10, 3*getHeight()/20);
            g.drawString("Pot Size: $" + pot, 2*getWidth()/10, getHeight()/20);

            int holeCard1X1 = getWidth()/10;
            int holeCard1X2 = holeCard1X1+getWidth()/4;

            int holeCardY1 = 5*getHeight()/10;
            int holeCardY2 = getHeight();

            int holeCard2X1 = holeCard1X2;
            int holeCard2X2 = holeCard2X1 + getWidth()/4;

            int card1ImageXMultiplier = 2;
            int card1ImageYMultiplier = 4;

            int card2ImageXMultiplier = 2;
            int card2ImageYMultiplier = 4;

            try{
                //cards[0] is card 1, cards[1] is card 2
                card1ImageXMultiplier = hand.get(0).getRank()-1;

                //determines cards suit to find which row of cards it is on in the cards.png image
                if(hand.get(0).getSuit() == 'c') card1ImageYMultiplier = 0;
                else if(hand.get(0).getSuit() == 'd') card1ImageYMultiplier = 1;
                else if(hand.get(0).getSuit() == 'h') card1ImageYMultiplier = 2;
                else if(hand.get(0).getSuit() == 's') card1ImageYMultiplier = 3;

                card2ImageXMultiplier = hand.get(1).getRank()-1;

                if(hand.get(1).getSuit() == 'c') card2ImageYMultiplier = 0;
                else if(hand.get(1).getSuit() == 'd') card2ImageYMultiplier = 1;
                else if(hand.get(1).getSuit() == 'h') card2ImageYMultiplier = 2;
                else if(hand.get(1).getSuit() == 's') card2ImageYMultiplier = 3;

            } catch (IndexOutOfBoundsException e){
                //System.err.println("No player cards yet");
                card1ImageXMultiplier = 2;
                card1ImageYMultiplier = 4;

                card2ImageXMultiplier = 2;
                card2ImageYMultiplier = 4;
            }

            int imageCard1X1 = card1ImageXMultiplier*CARD_IMAGE_WIDTH; //should be value * the width of the card image
            int imageCard1Y1 = card1ImageYMultiplier*CARD_IMAGE_HEIGHT; //should be value * the height of the card image
            int imageCard1X2 = imageCard1X1 + CARD_IMAGE_WIDTH; //dest_x2 of card 1
            int imageCard1Y2 = imageCard1Y1 + CARD_IMAGE_HEIGHT; //dest_y2 of card 1

            int imageCard2X1 = card2ImageXMultiplier*CARD_IMAGE_WIDTH; //etc...
            int imageCard2Y1 = card2ImageYMultiplier*CARD_IMAGE_HEIGHT;
            int imageCard2X2 = imageCard2X1 + CARD_IMAGE_WIDTH;
            int imageCard2Y2 = imageCard2Y1 + CARD_IMAGE_HEIGHT;

            g.drawImage(cardImage, holeCard1X1, holeCardY1, holeCard1X2, holeCardY2, imageCard1X1, imageCard1Y1, imageCard1X2, imageCard1Y2, this); //Hole card 1
            g.drawImage(cardImage, holeCard2X1, holeCardY1, holeCard2X2, holeCardY2, imageCard2X1, imageCard2Y1, imageCard2X2, imageCard2Y2, this); //Hole card 2

            //community cards
            for(int i=0; i<5; i++){
                int x = getWidth()/20 + i*getWidth()/8;
                int y = 4*getHeight()/20;
                int x2 = x+getWidth()/8;
                int y2 = y+3*getHeight()/12;
                //g.drawRect(x, y, getWidth()/8, 3*getHeight()/12);

                int imageX;
                int imageY;

                try {
                    imageX = (communityCards.get(i).getRank() - 1) * CARD_IMAGE_WIDTH;
                    imageY = 0;

                    if (communityCards.get(i).getSuit() == 'c') imageY = 0;
                    else if (communityCards.get(i).getSuit() == 'd') imageY = CARD_IMAGE_HEIGHT;
                    else if (communityCards.get(i).getSuit() == 'h') imageY = 2 * CARD_IMAGE_HEIGHT;
                    else if (communityCards.get(i).getSuit() == 's') imageY = 3 * CARD_IMAGE_HEIGHT;

                } catch (IndexOutOfBoundsException e){
                    imageX = 2 * CARD_IMAGE_WIDTH;
                    imageY = 4 * CARD_IMAGE_HEIGHT;
                }

                g.drawImage(cardImage, x, y, x2, y2, imageX, imageY, imageX+79, imageY+123, this);
            }
        }
        public void actionPerformed(ActionEvent e){

        }

        public void appendString(String s){
            informationBox.append(s + "\n");
        }
    }

    class HowToPlayPanel extends JPanel implements ActionListener{

	    private JButton back;

	    public HowToPlayPanel(){
	        super();
	        setBackground(DEFAULT_COLOR);

	        back = new JButton("Back");
            back.addActionListener(this);
            add(back);
        }

        public void paintComponent(Graphics g){
	        super.paintComponent(g);

	        g.setColor(Color.WHITE);
	        g.setFont(new Font("Helvetica", Font.PLAIN, 30));
	        g.drawString("The goal of the game is to eliminate all other players by getting all of their money. ", 50, 100);
            g.drawString("Each round, every player gets two cards, called 'hole' cards. These are the two largest cards on your screen.", 50, 150);
            g.drawString("Do not share them. They are used, in conjunction with the five cards in the middle, called 'community' cards,", 50, 200);
            g.drawString("to make the highest poker hand. At the end of each round, the player with the best poker hand wins and gets ", 50, 250);
            g.drawString("the pot. The pot increases as players bet. During each round, players have a chance to bet. This puts your money", 50, 300);
            g.drawString("at stake but also gives you a chance to win the pot. However, if you think you have an unplayable hand, you can", 50, 350);
            g.drawString("always fold, or bow out of the current betting round with, hopefully, minimal losses.", 50, 400);

            g.drawString("Order of poker hands (best to worst): Royal Flush, Straight Flush, Four of a Kind, Full House, Flush, Straight,", 50, 500);
            g.drawString("Three of a Kind, Two Pair, Pair, and finally, High Card", 50, 550);

            g.drawString("Ask the server host for the IP Address of the server.", 50, 750);
        }
	    public void actionPerformed(ActionEvent e){
            if (e.getActionCommand().equals("Back")) cardLayout.show(getContentPane(), START_PANEL);
        }
    }
}
