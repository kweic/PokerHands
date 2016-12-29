import java.util.HashMap;

public class PokerHand {
	private String player;
	private Card[] handArr = new Card[5];
	private int score;
	private boolean sameSuit;
	private boolean inOrder;
	private HashMap<Character, Integer> cardCounts;

	
	PokerHand(String player){
		this.player = player;
	}
	
	public String getPlayer(){
		return player;
	}
	
	public void setCardCounts(HashMap<Character, Integer> cardCounts){
		this.cardCounts = cardCounts;
	}
	
	public HashMap<Character, Integer> getCardCounts(){
		return cardCounts;
	}
	
	public void setSameSuit(boolean sameSuit){
		this.sameSuit = sameSuit;
	}
	
	public boolean isSameSuit(){
		return sameSuit;
	}
	
	public void setInOrder(boolean inOrder){
		this.inOrder = inOrder;
	}
	
	public boolean isInOrder(){
		return inOrder;
	}
	
	void buildHand(int index, String card){
		handArr[index] = new Card(card.charAt(0), card.charAt(1));
	}
	
	public Card[] getHand(){
		return handArr;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		return score;
	}
	
	@Override
	public String toString(){
		//for debug to check sorting
		StringBuilder sb = new StringBuilder();
		for(Card card: handArr){
			sb.append((card.getCardNumber()));
			sb.append(card.getCardSuit());
			sb.append(" ");
		}
		return sb.toString();
	}
}
