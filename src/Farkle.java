import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Farkle {
	private static int totalScore;
	private static int roundScore;
	static Random rgen = new Random();
	static ArrayList<Integer> diceRollResults = new ArrayList<Integer>();
	static ArrayList<Integer> savedDice = new ArrayList<Integer>();
	static JFrame frame = new JFrame("Farkle");
	static JPanel mainPanel = new JPanel(new BorderLayout());
	static List<JButton> buttonList = new ArrayList<JButton>();
	static JLabel scoreLabel = new JLabel("Current Total Score: ");
	static JLabel roundScoreLabel = new JLabel("Round Score: ");
	static JPanel southPanel = new JPanel();
	static JPanel middlePanel = new JPanel();
	static boolean holdScore = false;
	static JLabel invalidPickTextBox = new JLabel("");
	static JLabel currentDicePick = new JLabel("");

	public static void main(String[] args) {
		createGUI();
		createScore();
	}

	public static void generateNumbers(int rolls) {
		diceRollResults.removeAll(diceRollResults);
		for (int i = 0; i < rolls; i++) {
			int randomNumber = (int) rgen.nextInt(6) + 1;
			diceRollResults.add(randomNumber);
		}
		updateGUI();
	}

	public static void rollDice(int numberOfDice) {
		generateNumbers(numberOfDice);
	}

	public static void keepScore() {
		holdScore = true;
		updateRoundScore();
		savedDice.removeAll(savedDice);
		rollDice(6);
		roundToTotalScore();
		scoreLabel.setText("Current Total Score: 0");

	}

	public static int diceToPoints() {
		int checkForThree = 0;
		int[] table = new int[6];
		int threeOfAKind;
		int roundScore = 0;
		for (int i = 0; i < savedDice.size(); i++) {
			checkForThree = ++table[(savedDice.get(i)) - 1];
			if (checkForThree == 3) {
				threeOfAKind = (savedDice.get(i));
				roundScore += threeOfAKind * 100;
				savedDice.remove(savedDice.indexOf(threeOfAKind));
				savedDice.remove(savedDice.indexOf(threeOfAKind));
				savedDice.remove(savedDice.indexOf(threeOfAKind));
			}
		}
		for (int i = 0; i < savedDice.size(); i++) {
			int currentNumber = savedDice.get(i);
			if (currentNumber == 1) {
				roundScore += 100;
			} else if (currentNumber == 5) {
				roundScore += 50;
			}
		}
		return roundScore;
	}

	public static void updateRoundScore() {

		roundScore += diceToPoints();
		scoreLabel.setText("Current Total Score: " + String.valueOf(totalScore));
	}

	public static void roundToTotalScore() {
		totalScore += roundScore;
		roundScoreLabel.setText("Round Score: " + String.valueOf(roundScore));
	}

	public static int createScore() {
		totalScore = 0;
		return totalScore;
	}

	public static void createGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		JButton roll = new JButton("Roll");
		JButton stay = new JButton("Stay");
		frame.setVisible(true);
		frame.add(mainPanel);
		JPanel northPanel = new JPanel();
		northPanel.add(roll);
		northPanel.add(stay);
		southPanel.add(scoreLabel);
		southPanel.add(roundScoreLabel);
		middlePanel.add(invalidPickTextBox);
		middlePanel.add(currentDicePick);
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		roll.addActionListener(e -> selectionButtonPressed());
		stay.addActionListener(e -> keepScore());

	}

	private static void selectionButtonPressed() {
		if (diceRollResults.size() != 0 && !holdScore) {
			updateRoundScore();
			rollDice(diceRollResults.size());
		} else {
			rollDice(6);
			holdScore = false;
		}
		savedDice.removeAll(savedDice);
	}

	public static void updateGUI() {
		for (int i = 0; i < buttonList.size(); i++) {
			JButton thisButton = buttonList.get(i);
			middlePanel.remove(thisButton);
		}
		buttonList.removeAll(buttonList);
		for (int i = 0; i < diceRollResults.size(); i++) {
			String buttonNumber = String.valueOf(diceRollResults.get(i));
			buttonList.add(new JButton(buttonNumber));
		}
		for (int i = 0; i < buttonList.size(); i++) {
			middlePanel.add(buttonList.get(i));
			buttonList.get(i).addActionListener(e -> saveDice((JButton) e.getSource()));
			SwingUtilities.updateComponentTreeUI(frame);
		}
		checkForBust();
	}

	private static void saveDice(JButton x) {
		int diceNumber = Integer.parseInt(x.getText());
		int checkForThree = 0;
		for (int i = 0; i < diceRollResults.size(); i++) {
			if (diceRollResults.get(i) == diceNumber) {
				checkForThree++;
			}
		}
		// May need to change this to equal 3 or to increase score if theres a forth not
		// sure
		if (checkForThree >= 3) {
			savedDice.add(diceNumber);
			savedDice.add(diceNumber);
			savedDice.add(diceNumber);
			diceRollResults.remove(diceRollResults.indexOf(diceNumber));
			diceRollResults.remove(diceRollResults.indexOf(diceNumber));
			diceRollResults.remove(diceRollResults.indexOf(diceNumber));
		} else if (diceNumber == 5 || diceNumber == 1) {
			savedDice.add(diceNumber);
			diceRollResults.remove(diceRollResults.indexOf(diceNumber));
		} else {
			invalidPickTextBox.setText("Needs to be 1, 5, or three of a kind");
		}
		currentDicePick.setText(String.valueOf(diceNumber));
	}

	private static boolean checkForBust() {
		ArrayList<Integer> keyNumbers = new ArrayList<Integer>(Arrays.asList(1, 5));
		if (!diceRollResults.stream().anyMatch(keyNumbers::contains)) {
			int[] table = new int[6];
			int checkForThree = 0;
			for (int i = 0; i < diceRollResults.size(); i++) {
				checkForThree = ++table[(diceRollResults.get(i)) - 1];
				if (checkForThree == 3) {
					return false;
				}
			}
			invalidPickTextBox.setText(("Bust, time to reroll"));
			diceRollResults.removeAll(diceRollResults);
		}
		return true;
	}
}
