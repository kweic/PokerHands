
	public class Card implements Comparable<Card>{
		private char cardNumber;
		private char cardSuit;
		
		public Card(char cardNumber, char cardSuit){
			this.cardNumber = cardNumber;
			this.cardSuit = cardSuit;
		}
		
		char getCardNumber(){
			return cardNumber;
		}
		
		char getCardSuit(){
			return cardSuit;
		}

		@Override
		public int compareTo(Card card2) {
			return HandsScorer.cardScores.get(cardNumber) - HandsScorer.cardScores.get(card2.getCardNumber());
		}
		
		@Override
		public String toString(){
			return cardNumber+""+cardSuit;
		}
	}

