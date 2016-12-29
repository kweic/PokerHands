import java.util.Scanner;


public class PokerHandsMain {
	
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		
		System.out.println("How many tests would you like to run?");
		int tests = Integer.parseInt(reader.nextLine());
		
		PokerHand black;
		PokerHand white;
		HandsScorer cardScorer = new HandsScorer();
		String[] inputArr;

		  
		for(int i = 0; i < tests; i++){
			inputArr = reader.nextLine().split(" ");
			black = new PokerHand("Black");
			white = new PokerHand("White");
			if(inputArr.length == 13){
				placeCardsInHands(inputArr, black, white);
				System.out.println("Test #"+(i+1)+": "+cardScorer.getHigherHand(black, white));
			}else{
				System.out.println("Detected improperly formatted input.");
			}
		}
	}
	
	
	
	static void placeCardsInHands(String[] input, PokerHand black, PokerHand white){
		int whiteIndex = 0;
		int blackIndex = 0;
		//1 to 5 black
		//8 to 12 white
		for(int i = 0; i < input.length; i++){
			if(i >= 1 && i <= 5){
				black.buildHand(blackIndex, input[i]);
				blackIndex++;
			}else if(i >= 8){
				white.buildHand(whiteIndex, input[i]);
				whiteIndex++;
			}
			
		}
	}
}
