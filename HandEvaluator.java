//Vincent Lim
//HandEvaluator.java
//This class has two static methods
//returnHighestHand()
//	This, given an arraylist of cards, returns the highest poker hand
//returnBestPlayerHand()
//	This, given an ArrayList of hands, returns the player with the highest poker hand
import java.util.*;
public class HandEvaluator{

    public static PokerHand returnHighestHand(ArrayList<Card> cards){
        //return highest possible poker hand
        /*
         * This algorithm computes the highest hand by checking if the hand meets the possible types of poker hands,
         * from highest value to lowest. It checks in the following order:
         * 1. Royal Flush
         * 2. Straight Flush
         * 3. Four of a kind
         * 4. Full house
         * 5. Flush
         * 6. Straight
         * 7. Three of a kind
         * 8. Two pair
         * 9. Pair
         * 10. High card
         * If any of these are met, it immediately stops checking.
         */
        PokerHand highestHand;
        if ((highestHand = isRoyalFlush(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isStraightFlush(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isFourOfAKind(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isFullHouse(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isFlush(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isStraight(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isThreeOfAKind(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isTwoPair(new ArrayList<>(cards))) != null) return highestHand;
        else if ((highestHand = isPair(new ArrayList<>(cards))) != null) return highestHand;
        else return isHighCard(new ArrayList<>(cards));
    }

    public static ArrayList<Player> returnBestPlayerHand(ArrayList<Player> players){
        //return player with best poker hand
        //First it sorts the players based on their hand rank, from least to greatest(the lowest rank(1) is the best hand)
        for(int i=0; i<players.size(); i++){
            for(int j=i+1; j<players.size(); j++){
                if (players.get(i).getBestHand().getRanking() > players.get(j).getBestHand().getRanking()) {
                    Player temp = players.get(i);
                    players.set(i, players.get(j));
                    players.set(j, temp);
                }
            }
        }

        for (int i = 0; i < players.size(); i++)
            System.out.println(players.get(i).getName() + " has a hand with rank " + players.get(i).getBestHand().getRanking());

        ArrayList<Player> tiebreakers = new ArrayList<>();
        int highestRank = players.get(0).getBestHand().getRanking();
        tiebreakers.add(players.get(0));
        for (int i = 1; i < players.size(); i++)
            if(players.get(i).getBestHand().getRanking() == highestRank)
                tiebreakers.add(players.get(i));

        if(tiebreakers.size() == 1) return tiebreakers;
        else{
            if(highestRank == 2 && tiebreakers.get(0).getBestHand() instanceof StraightFlush){ //just to be sure that
                ArrayList<Player> winners = new ArrayList<>();
                //If there is more than one straight flush, the player with the highest straight flush wins.
                for (int i=0; i<tiebreakers.size(); i++){ //This just sorts the players based based on the straightflush high cards, from greatest to least
                    for (int j=i+1; j<tiebreakers.size(); j++){
                        if(((StraightFlush)tiebreakers.get(i).getBestHand()).getHighCard() < ((StraightFlush)tiebreakers.get(j).getBestHand()).getHighCard()){
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                int highestCard = ((StraightFlush)tiebreakers.get(0).getBestHand()).getHighCard();
                for (int i = 0; i < tiebreakers.size(); i++) {
                    Player player =  tiebreakers.get(i);
                    if (((StraightFlush)player.getBestHand()).getHighCard() == highestCard){
                        winners.add(player);
                    }
                }

                return winners;
            } else if (highestRank == 3 && tiebreakers.get(0).getBestHand() instanceof FourOfAKind){
                ArrayList<Player> winners = new ArrayList<>();

                for (int i = 0; i < tiebreakers.size(); i++) { //immediately checks for a four of a kind, aces. If there is one, then
                    Player player =  tiebreakers.get(i);        //it returns it because four of a kind aces is the highest possible
                    if(((FourOfAKind)player.getBestHand()).getRank() == 1){ //four of a kind
                        winners.add(player);
                        return winners;
                    }
                }

                for (int i=0; i<tiebreakers.size(); i++){ //This just sorts the players based based on the rank of the four of a kind, from greatest to least
                    for (int j=i+1; j<tiebreakers.size(); j++){
                        if(((FourOfAKind)tiebreakers.get(i).getBestHand()).getRank() < ((FourOfAKind)tiebreakers.get(j).getBestHand()).getRank()){
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                //Since the highest four of a kind is the first index, we can just return that.
                winners.add(tiebreakers.get(0));
                return winners;
            } else if (highestRank == 4 && tiebreakers.get(0).getBestHand() instanceof FullHouse){
                ArrayList<Player> highestTriples = new ArrayList<>();

                for (int i=0; i<tiebreakers.size(); i++){ //This just sorts the players based based on the rank of the triple, from greatest to least
                    for (int j=i+1; j<tiebreakers.size(); j++){
                        if (((FullHouse)tiebreakers.get(i).getBestHand()).getTriple().getRank() == 1){ //hardcoded in the case if the triple is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if(((FullHouse)tiebreakers.get(i).getBestHand()).getTriple().getRank() < ((FullHouse)tiebreakers.get(j).getBestHand()).getTriple().getRank()){
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                highestTriples.add(tiebreakers.get(0));
                for (int i=1; i<tiebreakers.size(); i++)
                    if(((FullHouse) tiebreakers.get(i).getBestHand()).getTriple().getRank() == ((FullHouse)tiebreakers.get(0).getBestHand()).getTriple().getRank())
                        highestTriples.add(tiebreakers.get(i));

                if (highestTriples.size() == 1) return highestTriples;
                else {

                    for (int i = 0; i < highestTriples.size(); i++) { //If there is more than one person with the same triple, the pair should be compared.
                        for (int j = i+1; j < highestTriples.size(); j++) {
                            if (((FullHouse)highestTriples.get(i).getBestHand()).getPair().getRank() == 1){ //hardcoded in the case if the triple is an ace
                                Player temp = highestTriples.get(i);
                                highestTriples.set(i, highestTriples.get(0));
                                highestTriples.set(0, temp);
                                i = highestTriples.size();
                                break;
                            }
                            if(((FullHouse)highestTriples.get(i).getBestHand()).getPair().getRank() < ((FullHouse)highestTriples.get(j).getBestHand()).getPair().getRank()){
                                Player temp = highestTriples.get(i);
                                highestTriples.set(i, highestTriples.get(j));
                                highestTriples.set(j, temp);
                            }
                        }
                    }

                    ArrayList<Player> highestPair = new ArrayList<>();

                    highestPair.add(highestTriples.get(0)); //adds the person with the highest pair and triple, then checks if others have the same
                    for (int i=1; i<highestPair.size(); i++){
                        if(((FullHouse) highestPair.get(i).getBestHand()).getPair().getRank() == ((FullHouse)highestPair.get(0).getBestHand()).getPair().getRank()) {
                            highestTriples.add(highestPair.get(i));
                        }
                    }

                    return highestPair;
                }
            } else if (highestRank == 5 && tiebreakers.get(0).getBestHand() instanceof Flush){
                for (int k = 0; k<5; k++) {
                    for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based based on the highest card, from greatest to least
                        for (int j = i+1; j < tiebreakers.size(); j++) {
                            if (((Flush) tiebreakers.get(i).getBestHand()).getCards().get(k).getRank() == 1) { //hardcoded in the case if the highest card is an ace
                                Player temp = tiebreakers.get(i);
                                tiebreakers.set(i, tiebreakers.get(0));
                                tiebreakers.set(0, temp);
                                i = tiebreakers.size();
                                break;
                            }
                            if (((Flush) tiebreakers.get(i).getBestHand()).getCards().get(k).getRank() < ((Flush) tiebreakers.get(j).getBestHand()).getCards().get(k).getRank()) {
                                Player temp = tiebreakers.get(i);
                                tiebreakers.set(i, tiebreakers.get(j));
                                tiebreakers.set(j, temp);
                            }
                        }
                    }

                    for (int i = 0; i < tiebreakers.size(); i++)
                        if (((Flush) tiebreakers.get(i).getBestHand()).getCards().get(k).getRank() != ((Flush) tiebreakers.get(0).getBestHand()).getCards().get(k).getRank())
                            tiebreakers.remove(tiebreakers.get(i));

                    for (int i = 0; i < tiebreakers.size(); i++) {
                        Player player =  tiebreakers.get(i);
                        System.out.println(player.getName() + " has a " + player.getBestHand().toString());
                    }

                    if (tiebreakers.size() == 1) return tiebreakers;
                }

                return tiebreakers;
            } else if (highestRank == 6 && tiebreakers.get(0).getBestHand() instanceof Straight){
                for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based based on the highest card, from greatest to least
                    for (int j = i+1; j < tiebreakers.size(); j++) {
                        if (((Straight) tiebreakers.get(i).getBestHand()).getHighCard() == 1) { //hardcoded in the case if the highest card is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if (((Straight) tiebreakers.get(i).getBestHand()).getHighCard() < ((Straight) tiebreakers.get(j).getBestHand()).getHighCard()) {
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                for (int i = 0; i < tiebreakers.size(); i++) {
                    if (((Straight)tiebreakers.get(i).getBestHand()).getHighCard() != ((Straight)tiebreakers.get(0).getBestHand()).getHighCard()){
                        tiebreakers.remove(tiebreakers.get(i));
                    }
                }

                return tiebreakers;
            } else if (highestRank == 7 && tiebreakers.get(0).getBestHand() instanceof ThreeOfAKind){
                for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based based on the highest card, from greatest to least
                    for (int j = i+1; j < tiebreakers.size(); j++) {
                        if (((ThreeOfAKind) tiebreakers.get(i).getBestHand()).getRank() == 1) { //hardcoded in the case if the highest card is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if (((ThreeOfAKind) tiebreakers.get(i).getBestHand()).getRank() < ((ThreeOfAKind) tiebreakers.get(j).getBestHand()).getRank()) {
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                for (int i = 0; i < tiebreakers.size(); i++) {
                    if (((ThreeOfAKind)tiebreakers.get(i).getBestHand()).getRank() != ((ThreeOfAKind)tiebreakers.get(0).getBestHand()).getRank()){
                        tiebreakers.remove(tiebreakers.get(i));
                    }
                }

                if (tiebreakers.size() == 1) return tiebreakers;
                else{//need to compare kickers
                    for (int i=0; i<2; i++){
                        for (int k = 0; k < tiebreakers.size(); k++) { //This just sorts the players based based on the highest card in the kickers, from greatest to least
                            for (int j = k+1; j < tiebreakers.size(); j++) {
                                if (((ThreeOfAKind) tiebreakers.get(k).getBestHand()).getKickers().get(i).getRank() == 1) { //hardcoded in the case if the highest card is an ace
                                    Player temp = tiebreakers.get(k);
                                    tiebreakers.set(k, tiebreakers.get(0));
                                    tiebreakers.set(0, temp);
                                    k = tiebreakers.size();
                                    break;
                                }
                                if (((ThreeOfAKind) tiebreakers.get(k).getBestHand()).getKickers().get(i).getRank() < ((ThreeOfAKind) tiebreakers.get(j).getBestHand()).getKickers().get(i).getRank()) {
                                    Player temp = tiebreakers.get(k);
                                    tiebreakers.set(k, tiebreakers.get(j));
                                    tiebreakers.set(j, temp);
                                }
                            }
                        }

                        for (int k = 0; k < tiebreakers.size(); k++) {
                            if (((ThreeOfAKind)tiebreakers.get(k).getBestHand()).getKickers().get(i).getRank() != ((ThreeOfAKind)tiebreakers.get(0).getBestHand()).getKickers().get(i).getRank()){
                                tiebreakers.remove(tiebreakers.get(k));
                            }
                        }

                        if (tiebreakers.size() == 1) return tiebreakers;
                    }
                    return tiebreakers;
                }
            } else if (highestRank == 8 && tiebreakers.get(0).getBestHand() instanceof TwoPair){
                for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the first pair, greatest to least
                    for (int j = i+1; j < tiebreakers.size(); j++) {
                        if (((TwoPair) tiebreakers.get(i).getBestHand()).getPair1().getRank() == 1) { //hardcoded in the case if the highest card is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if (((TwoPair) tiebreakers.get(i).getBestHand()).getPair1().getRank() < ((TwoPair) tiebreakers.get(j).getBestHand()).getPair1().getRank()) {
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }
                //remove any two pair that doesn't have the same pair1 rank as the best one
                for (int i = 0; i < tiebreakers.size(); i++) {
                    if (((TwoPair)tiebreakers.get(i).getBestHand()).getPair1().getRank() != ((TwoPair)tiebreakers.get(0).getBestHand()).getPair1().getRank()){
                        tiebreakers.remove(tiebreakers.get(i));
                    }
                }

                if (tiebreakers.size() == 1) return tiebreakers;
                else {//look at the second pair and kickers
                    for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the first pair, greatest to least
                        for (int j = i+1; j < tiebreakers.size(); j++) {
                            if (((TwoPair) tiebreakers.get(i).getBestHand()).getPair2().getRank() < ((TwoPair) tiebreakers.get(j).getBestHand()).getPair2().getRank()) {
                                Player temp = tiebreakers.get(i);
                                tiebreakers.set(i, tiebreakers.get(j));
                                tiebreakers.set(j, temp);
                            }
                        }
                    }

                    for (int i = 0; i < tiebreakers.size(); i++) {
                        if (((TwoPair)tiebreakers.get(i).getBestHand()).getPair2().getRank() != ((TwoPair)tiebreakers.get(0).getBestHand()).getPair2().getRank()){
                            tiebreakers.remove(tiebreakers.get(i));
                        }
                    }

                    if (tiebreakers.size() == 1) return tiebreakers;
                    else {
                        for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the kicker
                            for (int j = i+1; j < tiebreakers.size(); j++) {
                                if (((TwoPair) tiebreakers.get(i).getBestHand()).getKickers().get(0).getRank() == 1) { //hardcoded in the case if the kicker is an ace
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(0));
                                    tiebreakers.set(0, temp);
                                    i = tiebreakers.size();
                                    break;
                                }
                                if (((TwoPair) tiebreakers.get(i).getBestHand()).getKickers().get(0).getRank() < ((TwoPair) tiebreakers.get(j).getBestHand()).getKickers().get(0).getRank()) {
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(j));
                                    tiebreakers.set(j, temp);
                                }
                            }
                        }
                        for (int i = 0; i < tiebreakers.size(); i++) {
                            if (((TwoPair)tiebreakers.get(i).getBestHand()).getKickers().get(0).getRank() != ((TwoPair)tiebreakers.get(0).getBestHand()).getKickers().get(0).getRank()){
                                tiebreakers.remove(tiebreakers.get(i));
                            }
                        }

                        return tiebreakers;
                    }
                }
            } else if (highestRank == 9 && tiebreakers.get(0).getBestHand() instanceof Pair){
                //highest pair wins, in case of tie, then the kickers are used.
                for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the pair, greatest to least
                    for (int j = i+1; j < tiebreakers.size(); j++) {
                        if (((Pair) tiebreakers.get(i).getBestHand()).getRank() == 1) { //hardcoded in the case if the pair is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if (((Pair) tiebreakers.get(i).getBestHand()).getRank() < ((Pair) tiebreakers.get(j).getBestHand()).getRank()) {
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }

                for (int i = 0; i < tiebreakers.size(); i++) { //remove the ones that aren't the same as the highest pair
                    if (((Pair)tiebreakers.get(i).getBestHand()).getRank() != ((Pair)tiebreakers.get(0).getBestHand()).getRank()){
                        tiebreakers.remove(tiebreakers.get(i));
                    }
                }

                if (tiebreakers.size() == 1) return tiebreakers;
                else { //compare kickers
                    for (int k=0; k<3; k++){
                        for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the pair, greatest to least
                            for (int j = i+1; j < tiebreakers.size(); j++) {
                                if (((Pair) tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() == 1) { //hardcoded in the case if the pair is an ace
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(0));
                                    tiebreakers.set(0, temp);
                                    i = tiebreakers.size();
                                    break;
                                }
                                if (((Pair) tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() < ((Pair) tiebreakers.get(j).getBestHand()).getKickers().get(k).getRank()) {
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(j));
                                    tiebreakers.set(j, temp);
                                }
                            }
                        }

                        for (int i = 0; i < tiebreakers.size(); i++) { //removes those that are not the same as the highest kicker that is currently being compared
                            if (((Pair)tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() != ((Pair)tiebreakers.get(0).getBestHand()).getKickers().get(k).getRank()){
                                tiebreakers.remove(tiebreakers.get(i));
                            }
                        }
                    }
                    return tiebreakers;
                }
            } else if (highestRank == 10 && tiebreakers.get(0).getBestHand() instanceof HighCard){
                for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the highest card, greatest to least.
                    for (int j = i+1; j < tiebreakers.size(); j++) {
                        if (((HighCard) tiebreakers.get(i).getBestHand()).getRank() == 1) { //hardcoded in the case if the highest card is an ace
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(0));
                            tiebreakers.set(0, temp);
                            i = tiebreakers.size();
                            break;
                        }
                        if (((HighCard) tiebreakers.get(i).getBestHand()).getRank() < ((HighCard) tiebreakers.get(j).getBestHand()).getRank()) {
                            Player temp = tiebreakers.get(i);
                            tiebreakers.set(i, tiebreakers.get(j));
                            tiebreakers.set(j, temp);
                        }
                    }
                }
                for (int i = 0; i < tiebreakers.size(); i++) {
                    if (((HighCard)tiebreakers.get(i).getBestHand()).getRank() != ((HighCard)tiebreakers.get(0).getBestHand()).getRank()){
                        tiebreakers.remove(tiebreakers.get(i));
                    }
                }

                if (tiebreakers.size() == 1) return tiebreakers;
                else {
                    for (int k=0; k<4; k++){
                        for (int i = 0; i < tiebreakers.size(); i++) { //This just sorts the players based on the pair, greatest to least
                            for (int j = i+1; j < tiebreakers.size(); j++) {
                                if (((HighCard) tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() == 1) { //hardcoded in the case if the pair is an ace
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(0));
                                    tiebreakers.set(0, temp);
                                    i = tiebreakers.size();
                                    break;
                                }
                                if (((HighCard) tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() < ((HighCard) tiebreakers.get(j).getBestHand()).getKickers().get(k).getRank()) {
                                    Player temp = tiebreakers.get(i);
                                    tiebreakers.set(i, tiebreakers.get(j));
                                    tiebreakers.set(j, temp);
                                }
                            }
                        }

                        for (int i = 0; i < tiebreakers.size(); i++) {
                            if (((HighCard)tiebreakers.get(i).getBestHand()).getKickers().get(k).getRank() != ((HighCard)tiebreakers.get(0).getBestHand()).getKickers().get(k).getRank()){
                                tiebreakers.remove(tiebreakers.get(i));
                            }
                        }
                    }
                    return tiebreakers;
                }
            }
        }
        return null;
    }

    private static RoyalFlush isRoyalFlush(ArrayList<Card> cards){
        //check if the arraylist of cards has a royal flush
        //first it checks for the amount of each suit. then, if any number of suits>5, then it
        //checks if those cards have 10-A
        Flush checkFlush = isFlush(cards);
        if(checkFlush == null) return null;
        ArrayList<Card> temp = checkFlush.getCards();
        char suit = checkFlush.getSuit();
        
        //Logging-------------------------------------------------------
        System.out.println();
		System.out.println("Checking for a Royal Flush, already found a flush.");
        System.out.println("The Following cards are in the flush: " + temp.toString());
        System.out.println();
        //End Logging---------------------------------------------------
        
        boolean [] hasCards = {false, false, false, false, false}; //each boolean corresponds to a card, and if that card is present.
        int [] cardValues = {10, 11, 12 , 13, 1}; //each int corresponds to a boolean above

        for(int i=0; i<temp.size(); i++)
            for(int j = 0; j<cardValues.length; j++)
                if(temp.get(i).getRank() == cardValues[j])
                    hasCards[j] = true;

        for(int i=0; i<hasCards.length; i++){
			if(!hasCards[i]){ 
				
				//Logging-----------------------------------------------
				System.out.println();
				System.out.println("Could not find a Royal Straight inside the flush");
				System.out.println("Flush Cards: " + temp.toString());
				System.out.print("hasCards array: ");
				for(int s = 0; s<hasCards.length; s++) System.out.print(hasCards[s] + " ");
				System.out.println("\n");
				//End Logging-------------------------------------------
				
				return null;
			}
		}
		
		//Logging-------------------------------------------------------
		System.out.println();
		System.out.println("Found a Royal Flush");
		System.out.println("Flush Cards: " + temp.toString());
		System.out.println("Returning a Royal Flush with the following attributes: ");
		System.out.println("Suit: " + suit);
		System.out.println();
		//End Logging---------------------------------------------------
		
        return new RoyalFlush(suit);
    }

    private static StraightFlush isStraightFlush(ArrayList<Card> cards){
		Flush checkFlush = isFlush(cards);
		if(checkFlush == null) return null; //if there is no flush, then there is no straight flush
		
		ArrayList<Card> flushCards = checkFlush.getCards(); //gets the cards in the flush
		Straight checkStraight = isStraight(flushCards);    //and checks if those cards contain a straight
		if(checkStraight != null) {
            //Logging----------------------------------------------
            System.out.println("Returning a StraightFlush Object with the following attributes:");
            System.out.println("Cards: " + flushCards.toString());
            System.out.println("Suit: " + checkFlush.getSuit());
            //End Logging-------------------------------------------
		    return new StraightFlush(checkFlush.getSuit(), checkStraight.getHighCard());
        }
		else return null;
    }

    private static FourOfAKind isFourOfAKind(ArrayList<Card> cards){
        int repeats;
        for(int i=0; i<cards.size(); i++){
            repeats = 1;
            for(int j = i+1; j<cards.size(); j++){
                if(cards.get(i).getRank() == cards.get(j).getRank()) repeats++;
            }
            //System.out.println(cards.get(i).getRank() + " has " + repeats + " repeat(s)");
            if(repeats == 4) return new FourOfAKind(cards.get(i).getRank());
        }
        return null;
    }

    private static FullHouse isFullHouse(ArrayList<Card> cards){
        //This method checks for a full house
        //it first checks for a three of a kind, if there is none then there is no full house
        //Then it checks for another three of a kind, if there is not another three of a kind, it looks for a pair
        sortAcesFirst(cards);
        ArrayList<Card> noRepeats = new ArrayList<>();
        ThreeOfAKind triple;
        ThreeOfAKind triple2;
        Pair pair;
        if((triple = isThreeOfAKind(cards)) == null) return null; //looks for a three of a kind. If there is no three of a kind, then it returns null

        for (int i = 0; i < cards.size(); i++)
            if(triple.getRank() != cards.get(i).getRank()) noRepeats.add(cards.get(i)); //The noRepeats arraylist now does not have the highest three of a kind

        if((triple2 = isThreeOfAKind(noRepeats)) != null)
            return new FullHouse(triple, new Pair(triple2.getRank(), null));

        if((pair = isPair(noRepeats)) == null) return null;

        return new FullHouse(triple, pair);
    }

    private static Flush isFlush(ArrayList<Card> cards){
        int [] suitCount = {0, 0, 0, 0}; //goes by order of least to greatest: diamonds, clubs, hearts, spades
        char [] suitChar = {'d', 'c', 'h', 's'}; //each letter corresponds to the first letter of a suit
        ArrayList<Card> flushCards = new ArrayList<>();
        cards = sortAcesFirst(cards);
        for(int i=0; i<cards.size(); i++)
            for(int j=0; j<suitChar.length; j++)
                if(cards.get(i).getSuit() == suitChar[j]) suitCount[j]++; //counts the amount of each suit

        //for(int i=0; i<suitCount.length; i++)System.out.println("Number of " + suitChar[i] + " is " + suitCount[i]); //for debugging purposes

        for(int i=0; i<suitCount.length; i++)
            if(suitCount[i]>=5){ //if there are more than five of any suit
                for(int j=0; j<cards.size(); j++) //then this loop will iterate through the cards arraylist and
                    if(cards.get(j).getSuit() == suitChar[i]) //check if the card's suit is equal to the suit that has more than five
						flushCards.add(cards.get(j));
				//------------------------------------
				System.out.println();
				System.out.println("Returning a Flush Object with the following attributes:");
				System.out.println("Suit: " + suitChar[i]);
				System.out.println("Cards: " + flushCards.toString());
				System.out.println();
                return new Flush(suitChar[i], flushCards);
            }
        return null;
    }

    private static Straight isStraight(ArrayList<Card> cards){
        sort(cards);
        
        boolean [] hasCards = {false, false, false, false, false}; //each boolean corresponds to a card, and if that card is present.
        int [] cardValues = {10, 11, 12 , 13, 1}; //each int corresponds to a boolean above

        for(int i=0; i<cards.size(); i++)
            for(int j = 0; j<cardValues.length; j++)
                if(cards.get(i).getRank() == cardValues[j])
                    hasCards[j] = true;
        
        if(allTrue(hasCards)) return new Straight(1); //checks for a royal straight
        
        boolean hasStraight = false;
        
        for(int i=0; i<cards.size()-4; i++){
            //System.out.println(cards.get(i).getRank());
            for(int j = i+1; j<i+5; j++){
                //System.out.println(cards.get(j).getRank());
                if(!(hasStraight = (cards.get(i).getRank() == cards.get(j).getRank() + (j-i)))) {
					//System.out.println();
                    //System.out.println(cards.get(i).getRank() + " is not " + (j - i) + " away from " + cards.get(j).getRank());
                    break;
                }
            }
            if(hasStraight) return new Straight(cards.get(i).getRank());
        }
        return null;
    }

    private static ThreeOfAKind isThreeOfAKind(ArrayList<Card> cards){
        int repeats;
        for(int i=0; i<cards.size(); i++){
            repeats = 1;
            for(int j = i+1; j<cards.size(); j++){
                if(cards.get(i).getRank() == cards.get(j).getRank()) repeats++;
            }
            //System.out.println(cards.get(i).getRank() + " has " + repeats + " repeat(s)");
            if(repeats == 3) {
                ArrayList<Card> noRepeats = new ArrayList<>();
                for (int j=0; j<cards.size(); j++)
                    if (cards.get(j).getRank() != cards.get(i).getRank())
                        noRepeats.add(cards.get(j));

                sortAcesFirst(noRepeats);
                if (noRepeats.size() > 1)
                    return new ThreeOfAKind(cards.get(i).getRank(), new ArrayList<>(noRepeats.subList(0, 2)));
                else
                    return new ThreeOfAKind(cards.get(i).getRank(), null);
            }
        }
        return null;
    }
    private static TwoPair isTwoPair(ArrayList<Card> cards){
		//There are two possibilities:
		//Two pairs or three pairs
		//First, we check for a two pairs. If there isn't two pairs, then there isnt a two pair
		//then, if there are two pairs, then check for a third pair.
        //System.out.println(sortAcesFirst(cards).toString());
        sortAcesFirst(cards);
		ArrayList<Card> noRepeats = new ArrayList<>();
        ArrayList<PokerHand> hands = new ArrayList<>();
        PokerHand temp;
        if((temp = isPair(cards)) == null) return null;
        hands.add(temp);
        
        for (int i = 0; i < cards.size(); i++) //adds the card to a new array if it is not part of the pair
            if (((Pair) temp).getRank() != cards.get(i).getRank()) noRepeats.add(cards.get(i));
            
        cards = noRepeats; //now the cards arraylist has the initial pair removed
        noRepeats = new ArrayList<>();
        
        if((temp = isPair(cards)) == null) return null; //This is the second check for a pair. If there isn't a second pair,
                                                        //then there is no two pair
        hands.add(temp); //adds the second pair to the hands array.
        
        for (int i = 0; i < cards.size(); i++) //adds the card to a new array if it is not part of the pair
            if (((Pair) temp).getRank() != cards.get(i).getRank()) noRepeats.add(cards.get(i));

        //System.out.println(noRepeats.toString()); //For debugging purposes(prints out the kickers)
        sortAcesFirst(noRepeats);

        return new TwoPair((Pair)hands.get(0), (Pair)hands.get(1), new ArrayList<>(noRepeats.subList(0, 1))); //only want the first card in the arraylist
                                                                                                                //due to poker tiebreaker rules for kickers
	}
    private static Pair isPair(ArrayList<Card> cards){
        int repeats;
        ArrayList<Card> kickers = new ArrayList<>();
        for(int i=0; i<cards.size(); i++){
            repeats = 1;
            for(int j = i+1; j<cards.size(); j++){
                if(cards.get(i).getRank() == cards.get(j).getRank()) repeats++;
            }
            //System.out.println(cards.get(i).getRank() + " has " + repeats + " repeat(s)");
            if(repeats == 2){
                for (int j = 0; j < cards.size(); j++)
                    if (cards.get(i).getRank() != cards.get(j).getRank()) kickers.add(cards.get(j));
                sortAcesFirst(kickers);
                if (kickers.size() > 2)
                    return new Pair(cards.get(i).getRank(), new ArrayList<>(kickers.subList(0, 3)));
                else return new Pair(cards.get(i).getRank(), null);
            }
        }
        return null;
    }

    private static HighCard isHighCard(ArrayList<Card> cards){
       sortAcesFirst(cards);
       return new HighCard(cards.get(0).getRank(), new ArrayList<>(cards.subList(1, cards.size())));
    }

    private static ArrayList<Card> sort(ArrayList<Card> cards){
        for(int i=0; i<cards.size(); i++){
            for(int j=i+1; j<cards.size(); j++){
                if(cards.get(i).getRank() < cards.get(j).getRank()){
                    Card temp = cards.get(i);
                    cards.set(i, cards.get(j));
                    cards.set(j, temp);
                    //System.out.println("Switching " + cards.get(i).getRank() + " and " + cards.get(j).getRank());
                }
            }
        }
        return cards;
    }

    private static ArrayList<Card> sortAcesFirst(ArrayList<Card> cards){
        //sorts the cards, but puts the 'aces' (rank 1) cards first, since they are the highest value card
        sort(cards);
        for(int i=0; i<cards.size(); i++){
            if(cards.get(cards.size()-1).getRank() == 1){
                Card temp = cards.get(cards.size()-1);
                cards.remove(cards.size()-1);
                cards.add(0, temp);
            }
        }
        System.out.println(cards.toString());
        return cards;
    }

    private static boolean allTrue(boolean [] array){
		for(int i=0; i<array.length; i++)
			if(!array[i]) return false;
		return true;
	}
	
	public static String rankToString(int rank){
		if(rank == 1) return "ace";
		else if(rank == 2) return "2";
		else if(rank == 3) return "3";
		else if(rank == 4) return "4";
		else if(rank == 5) return "5";
		else if(rank == 6) return "6";
		else if(rank == 7) return "7";
		else if(rank == 8) return "8";
		else if(rank == 9) return "9";
		else if(rank == 10) return "10";
		else if(rank == 11) return "jack";
		else if(rank == 12) return "queen";
		else if(rank == 13) return "king";		
		else return "invalid";
	}
}
