/* Vincent Lim
 * PokerServerGUI.java
 * Graphics and Visuals for the PokerServer
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PokerServerGUI extends JFrame{
	
	private Image cardImage = new ImageIcon("cards.png").getImage();
	private ArrayList<Player> players;
	private CardLayout cardLayout;
	private ArrayList<Card> communityCards;
	private int pot;
	
	private final String PLAY_PANEL = "PlayPanel";
	private final String START_PANEL = "StartPanel";
	private final String SETTINGS_PANEL = "SettingsPanel";
    private final String HOW_TO_PLAY_PANEL = "HowToPlay";

	private StartPanel startPanel;
	private PlayPanel playPanel;
	private SettingsPanel settingsPanel;
	private HowToPlayPanel howToPlayPanel;

	private final Color DEFAULT_COLOR = new Color(13, 97 ,29);
		
	public static void main(String [] args){
		PokerServerGUI hugo = new PokerServerGUI();
	}
	public PokerServerGUI(){
		super("Poker");
		setSize(1600, 1000);
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(DEFAULT_COLOR);
		setVisible(true);
		addPanels();
	}
	
	public void addPanels(){
		startPanel = new StartPanel();
		playPanel = new PlayPanel();
		settingsPanel = new SettingsPanel();
		howToPlayPanel = new HowToPlayPanel();
		
		add(startPanel, START_PANEL);
		add(playPanel, PLAY_PANEL);
		add(settingsPanel, SETTINGS_PANEL);
		add(howToPlayPanel, HOW_TO_PLAY_PANEL);
		
		cardLayout.show(getContentPane(), START_PANEL);
	}

	public void setCommunityCards(ArrayList<Card> cards){
	    this.communityCards = cards;
	    repaint();
    }

    public void setPot(int pot){
	    this.pot = pot;
	    repaint();
    }
	
	public void setPlayers(ArrayList<Player> players){
		this.players = players;
		repaint();
	}

	class StartPanel extends JPanel implements ActionListener{
		private JButton startButton;
		private JButton settingsButton;
		private JButton instructionsButton;
		public StartPanel(){
			super(null);
			//setSize(PokerServerGUI.this.getWidth(), PokerServerGUI.this.getHeight());
			setBackground(DEFAULT_COLOR);

			startButton = new JButton("Start");
			startButton.addActionListener(this);
			startButton.setBounds(getWidth()/2-50, getHeight()/2-25, 150, 30);
			add(startButton);
			
			settingsButton = new JButton("Settings");
			settingsButton.addActionListener(this);
			settingsButton.setBounds(getWidth()/2-50, getHeight()/2+25, 150, 30);
			add(settingsButton);

            instructionsButton = new JButton("How To Play");
            instructionsButton.addActionListener(this);
            instructionsButton.setBounds(getWidth()/2-50, getHeight()/2+75, 150, 30);
            add(instructionsButton);
		}
		public void paintComponent(Graphics g){
			
			super.paintComponent(g);
			
			startButton.setBounds(getWidth()/2-50, getHeight()/2-25, 150, 30);
			settingsButton.setBounds(getWidth()/2-50, getHeight()/2+25, 150, 30);
            instructionsButton.setBounds(getWidth()/2-50, getHeight()/2+75, 150, 30);

			g.setColor(Color.BLACK);
			g.setFont(new Font("Helvetica", Font.BOLD, 60));
			g.drawString("Texas Hold'em", getWidth()/2 - g.getFontMetrics().stringWidth("Texas Hold'em")/2, getHeight()/2-60);
		}
		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand().equals("Start")) {
			    JOptionPane.showMessageDialog(this, PokerServer.IPAddress, "Give this IP Address to clients!", JOptionPane.INFORMATION_MESSAGE);
			    cardLayout.show(getContentPane(), PLAY_PANEL);
            }
			else if(e.getActionCommand().equals("Settings")) cardLayout.show(getContentPane(), SETTINGS_PANEL);
			else if (e.getActionCommand().equals("How To Play")) cardLayout.show(getContentPane(), HOW_TO_PLAY_PANEL);
		}
	}

	class SettingsPanel extends JPanel implements ActionListener{
	    private JButton discardChanges;
	    private JButton saveChanges;

		private JTextField maxPlayers;
		private JLabel maxPlayersLabel;

		private JTextField smallBlind;
		private JLabel smallBlindLabel;

		private JTextField bigBlind;
		private JLabel bigBlindLabel;

		private JTextField money;
		private JLabel moneyLabel;

		private JTextField port; //will decide whether to use this later
		private JLabel portLabel; //above

		private JLabel settingsLabel;

		private Font defaultFont;
	    public SettingsPanel(){
	        super(new GridLayout(6,2, 0, 100));

	        defaultFont = new Font("Helvetica", Font.PLAIN, 50);

	        add(settingsLabel = new JLabel("Settings"));
	        settingsLabel.setFont(defaultFont);
	        add(new JLabel());

	        maxPlayersLabel = new JLabel("Max Players");
	        add(maxPlayersLabel);
            maxPlayersLabel.setFont(defaultFont);

	        maxPlayers = new JTextField(String.valueOf(PokerServer.maxPlayers));
	        add(maxPlayers);

	        smallBlindLabel = new JLabel("Small Blind Amount");
	        add(smallBlindLabel);
            smallBlindLabel.setFont(defaultFont);

	        smallBlind = new JTextField(String.valueOf(PokerServer.smallBlind));
	        add(smallBlind);

	        bigBlindLabel = new JLabel("Big Blind Amount");
	        add(bigBlindLabel);
            bigBlindLabel.setFont(defaultFont);

	        bigBlind = new JTextField(String.valueOf(PokerServer.bigBlind));
	        add(bigBlind);

	        moneyLabel = new JLabel("Starting Money");
	        add(moneyLabel);
            moneyLabel.setFont(defaultFont);

	        money = new JTextField(String.valueOf(PokerServer.money));
	        add(money);

            saveChanges = new JButton("Save Changes");
            saveChanges.addActionListener(this);
            saveChanges.setFont(defaultFont);
            add(saveChanges);

            discardChanges = new JButton("Discard Changes");
            discardChanges.addActionListener(this);
            discardChanges.setFont(defaultFont);
            add(discardChanges);
        }
        public void actionPerformed(ActionEvent e){
	        if(e.getActionCommand().equals("Discard Changes")) {
	        	cardLayout.show(getContentPane(), START_PANEL);
                //cardLayout.previous(getContentPane());
	        	maxPlayers.setText(String.valueOf(PokerServer.maxPlayers));
	        	smallBlind.setText(String.valueOf(PokerServer.smallBlind));
                bigBlind.setText(String.valueOf(PokerServer.bigBlind));
                money.setText(String.valueOf(PokerServer.money));
			}
	        if (e.getActionCommand().equals("Save Changes")){
                try{
                    PokerServer.maxPlayers = Integer.parseInt(maxPlayers.getText());
                    PokerServer.smallBlind = Integer.parseInt(smallBlind.getText());
                    PokerServer.bigBlind = Integer.parseInt(bigBlind.getText());
                    PokerServer.money = Integer.parseInt(money.getText());
                    cardLayout.show(getContentPane(), START_PANEL);
                    //cardLayout.previous(getContentPane());
                } catch (NumberFormatException exception){
                    JOptionPane.showMessageDialog(this, "Make sure all values are numbers", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
	
	class PlayPanel extends JPanel implements ActionListener {

        private JButton back;
        private JButton instructionsButton;
        private JButton settingsButton;
		//I use variables for the card image dimensions so that I can use different card images in the future (better, higher quality card image)
		private final int CARD_IMAGE_WIDTH = 79; //width of the card image
		private final int CARD_IMAGE_HEIGHT = 123; //height of the card image

		public PlayPanel(){
			super(null);
			//setSize(PokerServerGUI.this.getWidth(),PokerServerGUI.this.getHeight());
			setBackground(DEFAULT_COLOR);

            instructionsButton = new JButton("How To Play");
            back = new JButton("Back");
            settingsButton = new JButton("Settings");
		}
		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand().equals("Back")) cardLayout.show(getContentPane(), START_PANEL);
            else if(e.getActionCommand().equals("Settings")) cardLayout.show(getContentPane(), SETTINGS_PANEL);
            else if (e.getActionCommand().equals("How To Play")) cardLayout.show(getContentPane(), HOW_TO_PLAY_PANEL);
		}
		public void paintComponent(Graphics g){

			super.paintComponent(g);

			int playerWidth = (int)(getWidth() * 0.2); //Width of the player's rectangles
			int playerHeight = (int)(getHeight()*0.35); //height of " " "
			int playerX = (int)(getWidth()*0.2); //X Coordinate of player's rectangle
			int playerY = (int)(getHeight()*0.35); //Y Coordinate, *0 for the first row of players

			int panelWidth = (int)(getWidth() * 0.2); //width of panel to the right
			int panelHeight = (int)(getHeight() * 0.7); //height of panel to the right
			int panelX = (int)(getWidth() * 0.8); //x coordinate of panel to the right
			int panelY = 0; //y coordinate of panel to the right

			//g.setColor(DEFAULT_COLOR);
			//g.fillRect(panelX, panelY, panelWidth, panelHeight); //panel to the right

            g.setFont(new Font("Helvetica", Font.BOLD, 24));

            g.setColor(Color.BLACK);
            String potSize = "Pot Size: $" + pot;
            g.drawString(potSize, panelX + panelWidth/2 - g.getFontMetrics().stringWidth(potSize)/2, panelY + panelHeight/2);

            String smallBlindSize = "Small Blind Amount: $" + PokerServer.smallBlind;
            g.drawString(smallBlindSize, panelX + panelWidth/2 - g.getFontMetrics().stringWidth(smallBlindSize)/2, panelY + panelHeight/2 + 30);

            String bigBlindSize = "Big Blind Amount: $" + PokerServer.bigBlind;
            g.drawString(bigBlindSize, panelX + panelWidth/2 - g.getFontMetrics().stringWidth(bigBlindSize)/2, panelY + panelHeight/2 + 60);

            String IPAddressSize = "IP Address: " + PokerServer.IPAddress;
            g.drawString(IPAddressSize, panelX + panelWidth/2 - g.getFontMetrics().stringWidth(IPAddressSize)/2, panelY + panelHeight/2 + 90);

            back.setBounds(panelX + panelWidth/2-75, 10, 150, 30);
            back.addActionListener(this);
            add(back);

            instructionsButton.setBounds(panelX + panelWidth/2-75, 50, 150, 30);
            instructionsButton.addActionListener(this);
            add(instructionsButton);

            settingsButton.setBounds(panelX + panelWidth/2-75, 90, 150, 30);
            settingsButton.addActionListener(this);
            add(settingsButton);

            //ALL OF THIS IS FOR DRAWING THE PLAYER'S CARDS AND INFORMATION
			Color color1;
			Color color2;
			for(int i=0; i<2; i++){
				if(i % 2 == 0){
					color1 = Color.WHITE;
					color2 = Color.LIGHT_GRAY;
				} else{
					color1 = Color.LIGHT_GRAY;
					color2 = Color.WHITE;
				}
				for(int j = 0; j<4; j++){
					if(j % 2 == 0) g.setColor(color1);
					else g.setColor(color2);

                    int x = playerX * j;
                    int y = playerY * i;

                    g.fillRect(x, y, playerWidth, playerHeight);

					int card1ImageXMultiplier = 2;
					int card1ImageYMultiplier = 4;
                    int card2ImageXMultiplier = 2;
                    int card2ImageYMultiplier = 4;

                    String name;
                    int money;
                    boolean folded;
                    Player player;
                    //This first try catch gets basic player info(money, name, and whether he/she has folded or not
					try{
                        player = players.get(j+(i*4));
						//System.out.println(player.getName()); //debugging

						name = player.getName();
						money = player.getMoney();
						folded = player.getFolded();
					} catch(IndexOutOfBoundsException e){
					    //System.err.println("IndexOutOfBoundsException, a total of 8 players have not connected yet.");
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Helvetica", Font.BOLD, 24));
                        g.drawString("DISCONNECTED", x+10, y+24);
                        continue;
					} catch(NullPointerException e){
                        //System.err.println("NullPointerException when getting the player's basic info. The server has just started and players have not connected yet.");
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Helvetica", Font.BOLD, 24));
                        g.drawString("DISCONNECTED", x+10, y+24);
                        continue;
                    }

                    //The second try catch gets the player's cards. The try catch is different because there needs to be
                    //a different reaction to the same error that is thrown
                    try{
                        Card[] cards = player.getCards();

                        //Debugging, will delete this later
                        //for(int k = 0; k<cards.length; k++)
                        //    System.out.println(cards[k].toString());
                        //---------------------------------

                        //cards[0] is card 1, cards[1] is card 2
                        card1ImageXMultiplier = cards[0].getRank()-1;

                        //determines cards suit to find which row of cards it is on in the cards.png image
                        if(cards[0].getSuit() == 'c') card1ImageYMultiplier = 0;
                        else if(cards[0].getSuit() == 'd') card1ImageYMultiplier = 1;
                        else if(cards[0].getSuit() == 'h') card1ImageYMultiplier = 2;
                        else if(cards[0].getSuit() == 's') card1ImageYMultiplier = 3;

                        card2ImageXMultiplier = cards[1].getRank()-1;

                        if(cards[1].getSuit() == 'c') card2ImageYMultiplier = 0;
                        else if(cards[1].getSuit() == 'd') card2ImageYMultiplier = 1;
                        else if(cards[1].getSuit() == 'h') card2ImageYMultiplier = 2;
                        else if(cards[1].getSuit() == 's') card2ImageYMultiplier = 3;
                    } catch (NullPointerException e){
					    //System.err.println("NullPointerException when getting the player's cards. This means that the players are still connecting.");

					    //these multipliers make it so that it draws the backside of a card
                        card1ImageXMultiplier = 2;
                        card1ImageYMultiplier = 4;

                        card2ImageXMultiplier = 2;
                        card2ImageYMultiplier = 4;
                    }

                    //These are all variables for drawing the image
					int card1X1 = x; //src_x1 of card 1
					int card1Y1 = y+(int)(playerHeight*0.5); //src_y1 of card 1
					int card1X2 = playerX * j + playerWidth-(int)(playerWidth*0.5); //src_x2 of card 1
					int card1Y2 = playerY * i + playerHeight; //src_y2 of card 1
					
					int card2X1 = x + (int)(playerWidth*0.5); //etc....
					int card2Y1 = y+(int)(playerHeight*0.5);
					int card2X2 = playerX * j + playerWidth;
					int card2Y2 = playerY * i + playerHeight;
					
					int imageCard1X1 = card1ImageXMultiplier*CARD_IMAGE_WIDTH; //should be value * the width of the card image
					int imageCard1Y1 = card1ImageYMultiplier*CARD_IMAGE_HEIGHT; //should be value * the height of the card image
					int imageCard1X2 = imageCard1X1 + CARD_IMAGE_WIDTH; //dest_x2 of card 1
					int imageCard1Y2 = imageCard1Y1 + CARD_IMAGE_HEIGHT; //dest_y2 of card 1
					
					int imageCard2X1 = card2ImageXMultiplier*CARD_IMAGE_WIDTH; //etc...
					int imageCard2Y1 = card2ImageYMultiplier*CARD_IMAGE_HEIGHT;
					int imageCard2X2 = imageCard2X1 + CARD_IMAGE_WIDTH;
					int imageCard2Y2 = imageCard2Y1 + CARD_IMAGE_HEIGHT;
					
					g.drawImage(cardImage, card1X1, card1Y1, card1X2, card1Y2, imageCard1X1, imageCard1Y1, imageCard1X2, imageCard1Y2, this);//card 1
					g.drawImage(cardImage, card2X1, card2Y1, card2X2, card2Y2, imageCard2X1, imageCard2Y1, imageCard2X2, imageCard2Y2, this);//card 2

                    g.setColor(Color.BLACK); //need to set the color to black so the string is not the same color as the background
                    g.setFont(new Font("Helvetica", Font.BOLD, 24));
                    g.drawString("Name: " + name, x+10, y+24);
                    g.drawString("Money: $" + Integer.toString(money), x+10, y+48);
                    g.drawString("Folded: " + Boolean.toString(folded), x+10, y+72);
				}
			}
			//END DRAWING PLAYER'S CARDS AND INFORMATION


            //BELOW IS FOR DRAWING THE COMMUNITY CARDS
			for(int i=0; i<5; i++){
				int x = getWidth()/24 + i * ((int)(getWidth()/6.6) + getWidth()/24);
				int y = (int)(getHeight()*0.7) + 20;
				//g.fillRect(getWidth()/24 + i * ((int)(getWidth()/6.6) + getWidth()/24), (int)(getHeight()*0.7) + 20, (int)(getWidth()/6.6), getHeight()); //community cards
                try{
                    int imageX1 = (communityCards.get(i).getRank()-1) * CARD_IMAGE_WIDTH;
                    int imageY1 = 0;

                    if(communityCards.get(i).getSuit() == 'c') imageY1 = 0;
                    else if(communityCards.get(i).getSuit() == 'd') imageY1 = CARD_IMAGE_HEIGHT;
                    else if(communityCards.get(i).getSuit() == 'h') imageY1 = 2*CARD_IMAGE_HEIGHT;
                    else if(communityCards.get(i).getSuit() == 's') imageY1 = 3*CARD_IMAGE_HEIGHT;

                    g.drawImage(cardImage, x, y, x + (int)(getWidth()/6.6), getHeight(), imageX1, imageY1, imageX1+CARD_IMAGE_WIDTH, imageY1+CARD_IMAGE_HEIGHT, this);
                    //possible exceptions: IndexOutOfBounds (when not all the community cards have been drawn)
                    //                      NullPointer (pre-flop, community cards have not been set yet)
                } catch (IndexOutOfBoundsException e){
                    //System.err.println("IndexOutOfBoundsException when drawing community cards. The game has not shown all the community cards yet, it is either in the second or third betting round.");
                    g.drawImage(cardImage, x, y, x + (int)(getWidth()/6.6), getHeight(), 2*CARD_IMAGE_WIDTH, 4*CARD_IMAGE_HEIGHT, 2*CARD_IMAGE_WIDTH+CARD_IMAGE_WIDTH, 4*CARD_IMAGE_HEIGHT+CARD_IMAGE_HEIGHT, this);
                } catch (NullPointerException e){
                    //System.err.println("NullPointerException when drawing the community cards. The game is either in pre-flop or the server has just started");
                    g.drawImage(cardImage, x, y, x + (int)(getWidth()/6.6), getHeight(), 2*CARD_IMAGE_WIDTH, 4*CARD_IMAGE_HEIGHT, 2*CARD_IMAGE_WIDTH+CARD_IMAGE_WIDTH, 4*CARD_IMAGE_HEIGHT+CARD_IMAGE_HEIGHT, this);
                }
			}
			//END DRAWING THE COMMUNITY CARDS
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

            g.drawString("As server host, you are able to see everyone's cards and statistics. Don't use this to cheat!!", 50, 750);
        }
        public void actionPerformed(ActionEvent e){
            if (e.getActionCommand().equals("Back")) cardLayout.show(getContentPane(), START_PANEL);
        }
    }
}