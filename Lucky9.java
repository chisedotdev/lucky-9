import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
public class Lucky9 {
	static String name;
	static int funds = 0;
	public static void main(String[] args) {
		boolean exit = false;
		while(exit == false) {
			int action = chooseAction();
			switch(action) {
				case 1:
					startGame();
					break;
				case 2:
					viewLeaderBoard();
					break;
				case 3:
					exit = true;
					break;
			}
		}
	}

	public static int chooseAction() {
		Scanner input = new Scanner(System.in);
		System.out.println("\nWelcome to Lucky-9\n");
		System.out.println("[1] Start Game\n[2] View Leader board\n[3] Exit");
		System.out.print("Select your action: ");
		int action = input.nextInt();
		return action;
	}

	public static void startGame() {
		Scanner input = new Scanner(System.in);
		Random random = new Random();
		System.out.print("\nEnter your dealer name: ");
		name = input.nextLine();
		if(funds <= 0) {
			System.out.print("Enter cash fund: ");
			funds = input.nextInt();
		}
		System.out.print("Enter number of player(s): ");
		int players = input.nextInt();
		if(players < 1 || players > 20) {
			boolean allowed = false;
			while(allowed == false) {
				System.out.print("Invalid input. Max of 20 players only. Try again: ");
				players = input.nextInt();
				if(players >=1 && players <= 20) {
					allowed = true;
				}
			}
		}
		input.nextLine();
		System.out.println("-------------------------");
		ArrayList<Integer> bets = new ArrayList<>();
		for(int player = 1; player <= players; player++) {	
			int playerBet = random.nextInt(81) + 20;
			System.out.println("Player " + player + " bet: " + playerBet);
			bets.add(playerBet);
		}
		System.out.println("\n[1] Play\n[2] Quit");
		System.out.print("Select your action: ");
		int action = input.nextInt();
		input.nextLine();
		switch(action) {
			case 1:
				boolean playAgain = true;
				while(playAgain == true) {
					ArrayList<String[]> deck = generateDeck();
					System.out.println("-------------------------");
					String[] firstCard = drawCard(deck);
					String[] secondCard = drawCard(deck);
					System.out.println("Your 1st card is: " + firstCard[0]);
					System.out.println("Your 2nd card is: " + secondCard[0]);
					System.out.print("Draw another card [Y/N]: ");
					String answer = input.nextLine();
					int thirdValue = 0;
					String thirdCardBool = "false";
					if(answer.equalsIgnoreCase("y")) {
						String[] thirdCard = drawCard(deck);
						thirdValue = Integer.parseInt(thirdCard[1]);
						System.out.println("Your 3rd card is: " + thirdCard[0]);
						thirdCardBool = "true";
					}
					int totalValue = Integer.parseInt(firstCard[1]) + Integer.parseInt(secondCard[1]) + thirdValue;
					if(totalValue >= 10) {
						totalValue %= 10;
					}
					String[] dealerCards = {Integer.toString(totalValue), thirdCardBool};
					System.out.println("\nYour total value: " + totalValue);
					System.out.print("Press Enter to view player's cards...");
					input.nextLine();
					System.out.println("-------------------------");
					ArrayList<String[]> playerCards = new ArrayList<>();
					playerCards = drawPlayersCards(players, deck);
					for(String[] card: playerCards) {
						System.out.println("Player " + card[5]);
						System.out.println("1st card: " + card[0]);
						System.out.println("2nd card: " + card[1]);
						if(card[4].equalsIgnoreCase("true")) {
							System.out.println("3rd card: " + card[2]);
						}
						System.out.println("Player " + card[5] + " value: " + card[3]);
						System.out.println("-------------------------");
					}
					playerCards.sort(Comparator.comparingInt((String[] cards) -> Integer.parseInt(cards[3])).reversed());
					ArrayList<ArrayList<String[]>> result = new ArrayList<>();
					result = checkWinnersAndLosers(dealerCards, playerCards, players);
					ArrayList<Integer> betsCopy = new ArrayList<>(bets);
					ArrayList<String[]> winners = result.get(0);
					System.out.println("Winner(s):");
					int winnersBetsTotal = 0;
					ArrayList<Integer> winnersBets = new ArrayList<>();
					if(winners.size() > 0) {
						for(String[] winner: winners) {
							int winnerIndex = Integer.parseInt(winner[5]) - 1;
							if (winnerIndex >= 0 && winnerIndex < betsCopy.size()) {
								int winnerBet = betsCopy.get(winnerIndex) * players;
								winnersBetsTotal += winnerBet;
								winnersBets.add(winnerIndex);
								System.out.println("Player " + winner[5] + " (" + winnerBet + ")");
							} else {
								System.out.println("...");
							}
						}
					}
					for(int counter = winnersBets.size() - 1; counter >= 0; counter--) {
						int index = winnersBets.get(counter);
						betsCopy.remove(index);
					}
					System.out.println("\nLoser(s):");
					ArrayList<String[]> losers = result.get(1);
					int losersBetsTotal = 0;
					for(int bet: betsCopy) {
						losersBetsTotal += bet;
					}
					if(losers.size() > 0) {
						for(String[] loser: losers) {
							System.out.println("Player " + loser[5]);
						}
					}
					if(winners.size() == 0 && losers.size() < players) {
						losersBetsTotal /= (players - losers.size()) + 1;	
					}
					System.out.println("-------------------------");
					System.out.println("Dealer Result:");
					System.out.println("Won: " + losersBetsTotal);
					System.out.println("Loss: " + winnersBetsTotal);
					funds -= winnersBetsTotal;
					funds += losersBetsTotal;
					System.out.println("Dealer total fund: " + funds);
					System.out.println("-------------------------");
					System.out.println("[1] Play Again\n[2] Quit");
					System.out.print("Select your action: ");
					action = input.nextInt();
					if(action != 1) {
						playAgain = false;
					}
					input.nextLine();
				}
				break;
			case 2:
				break;
		}
	}

	public static ArrayList<ArrayList<String[]>> checkWinnersAndLosers(String[] dealerCard, ArrayList<String[]> playerCards, int players) {
		int playerHighestValue = Integer.parseInt(playerCards.get(0)[3]);
		ArrayList<String[]> possibleWinners = new ArrayList<>();
		ArrayList<String[]> losers = new ArrayList<>();
		for(String[] card: playerCards) {
		    int cardValue = Integer.parseInt(card[3]);
			if (cardValue > playerHighestValue) {
				playerHighestValue = cardValue;
				possibleWinners.clear();
				possibleWinners.add(card);
			} else if (cardValue == playerHighestValue) {
				possibleWinners.add(card);
			} else {
				boolean isAlreadyLoser = false;
				for (String[] loser : losers) {
					if (loser[5].equals(card[5])) {
						isAlreadyLoser = true;
						break;
					}
				}
				if (!isAlreadyLoser) {
					losers.add(card);
				}
			}
		}
		if(possibleWinners.size() > 1) {
			ArrayList<String[]> naturalWinners = new ArrayList<>();
			ArrayList<String[]> notNaturalWinners = new ArrayList<>();
			for(String[] card: possibleWinners) {
				boolean isNotNatural = Boolean.parseBoolean(card[4]);
				if(isNotNatural == true) {
					notNaturalWinners.add(card);
				} else {
					naturalWinners.add(card);
				}
			}
			if(naturalWinners.size() >= 1 && notNaturalWinners.size() >= 1) {
				possibleWinners = naturalWinners;
				losers.addAll(notNaturalWinners);
			}
		}
		int dealerValue = Integer.parseInt(dealerCard[0]);
		boolean dealerThirdBool = Boolean.parseBoolean(dealerCard[1]);
		ArrayList<String[]> winners = new ArrayList<>();
		for(String[] possibleWinner: possibleWinners) {
			int pWinnerValue = Integer.parseInt(possibleWinner[3]);
			boolean pWinnerThirdBool = Boolean.parseBoolean(possibleWinner[4]);
			if(pWinnerValue >= dealerValue) {
				if(pWinnerValue == dealerValue) {
					if(pWinnerThirdBool == false && dealerThirdBool == false || pWinnerThirdBool == true && dealerThirdBool == true) {
						continue;
					} else if(pWinnerThirdBool == false && dealerThirdBool == true) {
						winners.add(possibleWinner);
					} else {
						losers.add(possibleWinner);
					}
				} else {
					winners.add(possibleWinner);
				}
			} else {
				losers.add(possibleWinner);
			}	
		}
		if (losers.size() > players) {
			losers.removeIf(card -> {
				int count = 0;
				for (String[] loser : losers) {
					if (loser[5].equals(card[5])) {
						count++;
						if (count > players) {
							return true;
						}
					}
				}
				return false;
			});
		}
		ArrayList<ArrayList<String[]>> result = new ArrayList<>();	
		winners.sort(Comparator.comparingInt((String[] winner) -> Integer.parseInt(winner[5])));
		losers.sort(Comparator.comparingInt((String[] loser) -> Integer.parseInt(loser[5])));
		result.add(winners);
		result.add(losers);
		return result;
	}

	public static String[] drawCard(ArrayList<String[]> deck) {
		Random random = new Random();
		int randomIndex = random.nextInt(deck.size());
		String[] randomCard = deck.get(randomIndex);
		deck.remove(randomIndex);
		return randomCard;
	}

	public static ArrayList<String[]> generateDeck() {
		ArrayList<String[]> deck = new ArrayList<>();
		String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
		String[] suits ={"of Diamonds", "of Hearts", "of Clubs", "of Spades"}; 
		for(int counter = 1; counter <= 8; counter++) {
			for(String suit: suits) {
				for(String rank: ranks) {
					String card = rank + " " + suit;
					String value = getValue(rank);
					deck.add(new String[] {card, value});
				}
			}
		}
		return deck;
	}

	public static String getValue(String rank) {
		if(rank.equals("A")) {
			return "1";
		} else if(rank.equals("J") || rank.equals("Q") || rank.equals("K")) {
			return "10";
		} else {
			return rank;
		}
	}

	public static ArrayList<String[]> drawPlayersCards(int players, ArrayList<String[]> deck) {
		ArrayList<String[]> playerCards = new ArrayList<>();
		for(int counter = 1; counter <= players; counter++) {
			String[] playerFirstCard = drawCard(deck);
			String[] playerSecondCard = drawCard(deck);
			String[] playerThirdCard = drawCard(deck);
			int totalValue = Integer.parseInt(playerFirstCard[1]) + Integer.parseInt(playerSecondCard[1]);
			String thirdCardBool = "false";
			if(totalValue <= 5 || totalValue >= 10) {
				totalValue += Integer.parseInt(playerThirdCard[1]);
				if(totalValue >= 10) {
					totalValue %= 10;
				}
				thirdCardBool = "true";
			}
			playerCards.add(new String[] {playerFirstCard[0], playerSecondCard[0], playerThirdCard[0], Integer.toString(totalValue), thirdCardBool, Integer.toString(counter)});
		}
		return playerCards;
	}

	public static void viewLeaderBoard() {
		Random random = new Random();
		Scanner input = new Scanner(System.in);
		System.out.println("\nLeaderboard (TOP 10)");
		System.out.println("-------------------------");
		ArrayList<String[]> board = new ArrayList<>();
		String[] names = {"Chris Board", "Joey Bizinger", "Garnt Maneetapho", "Connor Marc Colquhoun", "Sydney Ann Poniewaz", "Agnes Yulo Diego", "Felix Kjellberg", "Marzia Kjellberg", "Linus Sebastian"};
		for(String name: names) {
			int score = random.nextInt(100000 - 10000 + 1) + 10000;
			board.add(new String[] {name, Integer.toString(score)});
		}
		board.add(new String[] {name, Integer.toString(funds)});
		board.sort(Comparator.comparingInt((String[] user) -> Integer.parseInt(user[1])).reversed());
		for(int counter = 0; counter < board.size(); counter++) {
			String[] player = board.get(counter);
			System.out.println((counter + 1) + ") " + player[0] + " (" + player[1] + ")");
		}
		System.out.print("\nPress Enter to go back to the main menu...");
		input.nextLine();
	}
}