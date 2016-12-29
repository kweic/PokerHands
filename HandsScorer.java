import java.util.Arrays;
import java.util.HashMap;

public class HandsScorer {
	static HashMap<Character, Integer> cardScores;
	private TieBreakerTools tieBreaker;

	
	
	public HandsScorer(){
		cardScores = new HashMap<>();
		tieBreaker = new TieBreakerTools();
		loadCardValues();
		//Possible improvements:
		//enums for cards, although this program is small so the gain in readability might not be worth it
		//sneaking feeling I could use String manipulations for hands instead of arrays.. shorter code but more memory
		//could almost combine the getAnswer and tieBreaker methods but it might become overly complicated
	}
	
	public String getHigherHand(PokerHand black, PokerHand white){
		assertCardsAreInOrder(black);
		assertCardsAreInOrder(white);
		scoreHand(black);
		scoreHand(white);
		
		if(black.getScore() > white.getScore()){
			return getAnswer(black);
		}else if(white.getScore() > black.getScore()){
			return getAnswer(white);
		}
		
		String answer = tieBreaker.tieBreaker(black, white);
		return answer;
	}
	
	private String getAnswer(PokerHand hand){
		String playerWinsWith = hand.getPlayer()+" wins. - with ";
		
		switch(hand.getScore()){
		case 8: return playerWinsWith+"a Straight Flush.";
		case 7: return playerWinsWith+"four of a kind.";
		case 6: return playerWinsWith+"a full House.";
		case 5: return playerWinsWith+"a Flush, high in flush: "+getHighCardName(hand.getCardCounts());
		case 4: return playerWinsWith+"a Straight, highest in straight: "+getHighCardName(hand.getHand()[4].getCardNumber());
		case 3: return playerWinsWith+"three of a kind.";
		case 2: return playerWinsWith+"Two Pairs.";
		}
		return playerWinsWith+"one pair of cards.";
		//0 can't happen here because that would go to tie-breaker
	}
	
	public void assertCardsAreInOrder(PokerHand hand){
		//problem states the cards will be in order, but let's be sure because my code relies on it.
		Arrays.sort(hand.getHand());
		if(isUnsortedWheel(hand)){
			hand.setInOrder(true);
			Card ace = hand.getHand()[4];
			System.arraycopy(hand.getHand(), 0, hand.getHand(), 1, hand.getHand().length-1);
			hand.getHand()[0] = ace;
		}
	}
	
	private void scoreHand(PokerHand hand){
		//System.out.println(hand);
		hand.setSameSuit(sameSuit(hand));
		hand.setInOrder(inOrder(hand));
		hand.setCardCounts(mapCardPairs(hand.getHand()));//face, count
		//System.out.println("same suit: "+hand.isSameSuit()+" in order: "+hand.isInOrder());
		
		//these are done in order to find highest score first, then break
		//8. straight flush(All the same suit and in order), tie breaker is highest card, Ace can wrap, but is counted as low
		if(hand.isSameSuit() && hand.isInOrder()){
			hand.setScore(8);
			return;
		}
		//7. four of a kind(same value), tie breaker is higher set
		char[] highCardCount = findHighestCardFaceWithCount(hand.getCardCounts());
		if(highCardCount[1] == '4'){
			hand.setScore(7);
			return;
		}
		
		//6. full house 3 cards same value, 2 pair remain, tie breaker is value of the 3
		if(hand.getCardCounts().size() == 2){
			hand.setScore(6);
			return;
		}
		
		//5. Flush: 5 of same suit, high card is tie breaker
		if(hand.isSameSuit()){
			hand.setScore(5);
			return; //flush, 5 same suit
		}
		
		//4. Straight: 5 consecutive value cards, highest card is tie breaker, exclude A if wrapped
		if(hand.isInOrder()){
			hand.setScore(4);
			return; //straight
		}
		
		//3. Three of a Kind: 3 have same value, tie breaker higher set
		//uses sameValue Count and checks if findHighestCardFaceWithCount == 3, tie breaker is cardScores
		if(highCardCount[1] == '3'){
			hand.setScore(3);
			return; //three of a kind
		}
		
		//2. Two Pairs: 2 different pairs, ranked by value of highest pair, same high pair ranked by lower pair
		//uses sameValue and checks if the count 2, is contained twice, tie break by highest card, if == tie break by second pair
		if(hasTwoPair(hand.getCardCounts())){
			hand.setScore(2);
			return; //two pair
		}
		
		//1. Pair: 2 cards have same value, ranked by value of pair cards, tie breaker, rank by value of cards not in pair, keep comparing until 1 is higher
		//	uses sameValue and checks for 2, tie breaker is highest != card outside of pair
		if(highCardCount[1] == '2'){
			hand.setScore(1);
			return;//pair
		}
		//0High Card: value of highest card, tie breaker, check next highest card
		hand.setScore(0);
	}
	
	private boolean hasTwoPair(HashMap<Character, Integer> cardCounts){
		boolean firstFound = false;
		for(char key: cardCounts.keySet()){
			if(cardCounts.get(key) == 2){
				if(firstFound)return true;
				firstFound = true;
			}
		}
		return false;
	}
	
	private String getHighCardName(HashMap<Character, Integer> cardCounts){
		//this and getHighCardName(char) are only for tidy output, maybe not needed.
		char highCard = findHighestCardFaceWithCount(cardCounts)[0];
		return getHighCardName(highCard);
	}
	
	private String getHighCardName(char highCard){
		//this and getHighCardName(HashMap) are only for tidy output, maybe not needed.
		if(highCard == 'T')return "10";
		if(highCard == 'J')return "Jack";
		if(highCard == 'Q')return "Queen";
		if(highCard == 'K')return "King";
		if(highCard == 'A')return "Ace";
		return highCard+"";
	}
	
	
	public boolean isUnsortedWheel(PokerHand hand){
		//could also loop up through 3 and then return fifth card is ace
		return hand.getHand()[0].getCardNumber() == '2' 
				&& hand.getHand()[1].getCardNumber() == '3'
				&& hand.getHand()[2].getCardNumber() == '4'
				&& hand.getHand()[3].getCardNumber() == '5'
				&& hand.getHand()[4].getCardNumber() == 'A';
	}
	
	private HashMap<Character, Integer> mapCardPairs(Card[] hand){
		//returns face with how many times it appeared
		HashMap<Character, Integer> countCards = new HashMap<>();
		for(Card value: hand){
			if(value.getCardNumber() != '0'){ //0 lets me eliminate cards in a tie breaker situation
				if(!countCards.containsKey(value.getCardNumber())){
					countCards.put(value.getCardNumber(), 1);
				}else{
					countCards.put(value.getCardNumber(), countCards.get(value.getCardNumber())+1);
				}
			}
		}
		return countCards;
	}
	
	private boolean inOrder(PokerHand hand){
		if(hand.isInOrder())return true; //skip the problem a wheel will cause (A 2 3 4 5)
		for(int i = 1; i < hand.getHand().length; i++){
			//previous card should be one less than next card
			if(cardScores.get(hand.getHand()[i-1].getCardNumber())+1 != cardScores.get(hand.getHand()[i].getCardNumber()))return false;
		}
		return true;
	}
	
	private boolean sameSuit(PokerHand hand){
		char firstCard = hand.getHand()[0].getCardSuit();
		for(Card suit : hand.getHand()){
			if(suit.getCardSuit() != firstCard)return false;
		}
		return true;
	}
	
	char[] findHighestCardFaceWithCount(HashMap<Character, Integer> countCards){
		char highCountFace = '0';
		
		for(Character face: countCards.keySet()){
			if(face != '0' && (highCountFace == '0' || countCards.get(face) >= countCards.get(highCountFace))){
					highCountFace = face;
			}
		}
		if(highCountFace == '0')return new char[]{'0','0'};
		
		return new char[]{highCountFace,(char)(countCards.get(highCountFace)+48)};
	}
	
	private void loadCardValues(){
		for(int i = 1; i <= 9; i++){
			cardScores.put((char)(i+48), i);
		}
		cardScores.put('T', 10);
		cardScores.put('J', 11);
		cardScores.put('Q', 12);
		cardScores.put('K', 13);
		cardScores.put('A', 14);
	}
	
	
	
	
	class TieBreakerTools{
		
		private TieBreakerTools(){
			
		}
		
		private String tieBreaker(PokerHand black, PokerHand white){
			//8 straight flush, return the highest of the set
			switch(black.getScore()){
			case 8:return highestCardFromCardArray(black.getHand(), white.getHand(), " wins. - with a Straight flush,");
			//7 four of a kind with set of card, return higher of the sets
			case 7:return higherSet(black, white, "win. with a Four of a kind.");
			//6 full house 3 pair 2 pair, return higher of value of sets
			case 6:return higherSet(black, white, "win with a Full house.");
			//5 Flush, all same suit, return highest card comparison
			case 5:return highestCardFromCardArray(black.getHand(), white.getHand(), " wins. - with a Flush, ");
			//4 Straight, all in order, highest card
			case 4:return highestCardFromCardArray(black.getHand(), white.getHand(), " wins. - with a Straight, ");
			//3 tree of a kind, higher set
			case 3:return higherSet(black, white, "win with 3 of a Kind.");
			//2 two pairs, highest set
			case 2:return highestWithSmallPairs(black.getHand(), white.getHand(), " wins with Two Pairs.");
				//return highestCardFromCardArray(black.getHand(), white.getHand(), " Two Pairs, ");
			//1 pair, higher of the two pairs, if equal, higher of cards not in pair
			case 1:return highestWithSmallPairs(black.getHand(), white.getHand(),  " wins with One Pair.");
			//0 high card
			}
			return highestCardFromCardArray(black.getHand(), white.getHand(), " wins. - with a Higher Card. ");
		}
		
		private String higherSet(PokerHand black, PokerHand white, String winMessage){
			//used to check which set is higher when it's not possible that both sets are equal because of # of cards
			//sets of 5, 4, 3 can't be equal on both sides
			int blackScore = cardScores.get(findHighestCardFaceWithCount(black.getCardCounts())[0]);
			int whiteScore = cardScores.get(findHighestCardFaceWithCount(white.getCardCounts())[0]);
			if(blackScore != whiteScore){
				if(blackScore > whiteScore){
					return "Black "+winMessage;
				}
				return "White "+winMessage;
			}
			
			return "Something's fishy with this deck. Both "+winMessage;
		}
		
		private String highestWithSmallPairs(Card[] blackCards, Card[] whiteCards, String winMessage){
			//copy because I'll be modifying cards to make comparisons easier
			Card[] blackCopy = Arrays.copyOf(blackCards, blackCards.length);
			Card[] whiteCopy = Arrays.copyOf(whiteCards, whiteCards.length);
			HashMap<Character, Integer> cardPairsBlack = mapCardPairs(blackCards);
			HashMap<Character, Integer> cardPairsWhite = mapCardPairs(whiteCards);
			char[] highPairBlack = findHighestCardFaceWithCount(cardPairsBlack);
			char[] highPairWhite = findHighestCardFaceWithCount(cardPairsWhite);
			if(highPairBlack[0]== '0' || highPairWhite[0]=='0')return "Tie.";
			//take in the two hands
			//go through hands from the back, see if both are a pair
			if(highPairBlack[1] > 1){ //pair exists
					if(highPairBlack[0] == highPairWhite[0]){ 
						//high pair are the same, so blank those ones out and check for next highest
						for(int i = 0; i < blackCards.length; i++){
							//if both are a same pair, remove those two from the list and recurse
							if(blackCopy[i].getCardNumber() == highPairBlack[0])blackCopy[i]=new Card('0', '0');
							if(whiteCopy[i].getCardNumber() == highPairBlack[0])whiteCopy[i]=new Card('0', '0');
						}
						return highestWithSmallPairs(blackCopy, whiteCopy, winMessage);
						//if both are a different pair, return winner
					}else if(cardScores.get(highPairBlack[0]) > cardScores.get(highPairWhite[0])){
						return "Black"+winMessage;
					}else{
						return "White"+winMessage;
					}
			}
			
			//no pairs remain, check for highest card
			return highestCardFromCardArray(blackCards, whiteCards, winMessage);
		}
		
		private String highestCardFromCardArray(Card[] cards1, Card[] cards2, String winMessage){
			//todo: this might not work if cards aren't lining up how I expect
			for(int i = cards1.length-1; i >= 0; i--){
				char card1 = cards1[i].getCardNumber();
				char card2 = cards2[i].getCardNumber();
				if(card1 != card2){
					if(cardScores.get(card1) > cardScores.get(card2)){
						return "Black"+winMessage+" high card: "+getHighCardName(cards1[i].getCardNumber());
					}else{
						return "White"+winMessage+" high card: "+getHighCardName(cards2[i].getCardNumber());
					}
				}
			}
			return "Tie.";
		}

	}
}
