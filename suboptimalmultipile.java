import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class suboptimalmulit {
    private static HashMap<String, Integer> grundyCache = new HashMap<>();
    private static Random random = new Random();
    private static PrintWriter moveLogger;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask the user for the number of piles
        System.out.print("Enter the number of piles: ");
        int numPiles = scanner.nextInt();

        // Arrays to store the number of stones and x, y values for each pile
        int[] initialStones = new int[numPiles];
        int[] xValues = new int[numPiles];
        int[] yValues = new int[numPiles];

        // Collect x, y values and number of stones for each pile
        for (int i = 0; i < numPiles; i++) {
            System.out.print("Enter the number of stones for pile " + (i + 1) + ": ");
            initialStones[i] = scanner.nextInt();
            System.out.print("Enter the value for x for pile " + (i + 1) + ": ");
            xValues[i] = scanner.nextInt();
            System.out.print("Enter the value for y for pile " + (i + 1) + ": ");
            yValues[i] = scanner.nextInt();
        }

        // Ask for the accuracy of each player
        System.out.print("Enter the accuracy (0 to 1) for Player 1: ");
        double accuracyPlayer1 = scanner.nextDouble();
        System.out.print("Enter the accuracy (0 to 1) for Player 2: ");
        double accuracyPlayer2 = scanner.nextDouble();

        scanner.close();

        try {
            moveLogger = new PrintWriter(new FileWriter("moves.txt"));

            int player1Wins = 0;
            int player2Wins = 0;
            int totalGames = 100000;

            for (int game = 0; game < totalGames; game++) {
                moveLogger.println("Game " + (game + 1) + ":");
                int[] stones = initialStones.clone(); // Reset stones for each game
                boolean player1Turn = true;

                while (!isGameOver(stones)) {
                    if (player1Turn) {
                        playerMove(stones, xValues, yValues, accuracyPlayer1, "Player 1");
                    } else {
                        playerMove(stones, xValues, yValues, accuracyPlayer2, "Player 2");
                    }
                    player1Turn = !player1Turn;
                }

                if (player1Turn) {
                    player2Wins++;
                    moveLogger.println("Player 2 wins!");
                } else {
                    player1Wins++;
                    moveLogger.println("Player 1 wins!");
                }
                moveLogger.println();
            }

            double player1WinPercentage = (player1Wins * 100.0) / totalGames;
            double player2WinPercentage = (player2Wins * 100.0) / totalGames;

            System.out.printf("Player 1 win percentage: %.2f%%\n", player1WinPercentage);
            System.out.printf("Player 2 win percentage: %.2f%%\n", player2WinPercentage);

            moveLogger.close();
            System.out.println("All moves have been logged to moves.txt");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private static void playerMove(int[] stones, int[] xValues, int[] yValues, double accuracy, String playerName) {
        int optimalMove = getOptimalMove(stones, xValues, yValues);
        int pile = optimalMove / 1000;
        int move = optimalMove % 1000;
        
        if (random.nextDouble() <= accuracy) {
            stones[pile] -= move;
            logMove(playerName, pile, move, stones, true);
        } else {
            // Make a random valid move
            do {
                pile = random.nextInt(stones.length);
                move = Math.min(stones[pile], Math.max(1, random.nextInt(Math.max(xValues[pile], yValues[pile])) + 1));
            } while (stones[pile] == 0);
            
            stones[pile] -= move;
            logMove(playerName, pile, move, stones, false);
        }
    }

    private static void logMove(String playerName, int pile, int move, int[] stones, boolean optimal) {
        StringBuilder sb = new StringBuilder();
        sb.append(playerName).append(" removed ").append(move).append(" stones from pile ").append(pile + 1);
        if (!optimal) {
            sb.append(" (Inaccurate)");
        }
        sb.append(". Remaining: ");
        for (int i = 0; i < stones.length; i++) {
            sb.append(stones[i]);
            if (i < stones.length - 1) {
                sb.append(", ");
            }
        }
        moveLogger.println(sb.toString());
    }
    private static int getOptimalMove(int[] stones, int[] xValues, int[] yValues) {

        int bestMove = -1;
        int maxStonesToRemove = 0;
        int minStonesToRemove = Integer.MAX_VALUE;
        
        for (int pile = 0; pile < stones.length; pile++) {
            int[] possibleMoves = {1, xValues[pile], yValues[pile]};
            for (int move : possibleMoves) {
                if (stones[pile] >= move) {
                    stones[pile] -= move;
                    int newNimSum = calculateNimSum(stones, xValues, yValues);
                    stones[pile] += move;
                    
                    if (newNimSum == 0) {
                        if (move > maxStonesToRemove) {
                            maxStonesToRemove = move;
                            bestMove = pile * 1000 + move; // Encode pile and move
                        }
                    } else if (bestMove == -1 && move < minStonesToRemove) {
                        minStonesToRemove = move;
                        bestMove = pile * 1000 + move;
                    }
                }
            }
        }
        
        return bestMove;
    }

    private static int calculateNimSum(int[] stones, int[] xValues, int[] yValues) {
        int nimSum = 0;
        for (int i = 0; i < stones.length; i++) {
            nimSum ^= calculateGrundy(stones[i], xValues[i], yValues[i]);
        }
        return nimSum;
    }

    private static int calculateGrundy(int n, int x, int y) {
        if (n == 0) {
            return 0;
        }
        String key = n + "-" + x + "-" + y;
        if (grundyCache.containsKey(key)) {
            return grundyCache.get(key);
        }

        Set<Integer> subpositions = new HashSet<>();
        if (n - 1 >= 0) subpositions.add(calculateGrundy(n - 1, x, y));
        if (n - x >= 0) subpositions.add(calculateGrundy(n - x, x, y));
        if (n - y >= 0) subpositions.add(calculateGrundy(n - y, x, y));

        int grundyNumber = 0;
        while (subpositions.contains(grundyNumber)) {
            grundyNumber++;
        }

        grundyCache.put(key, grundyNumber);
        return grundyNumber;
    }

    private static boolean isGameOver(int[] stones) {
        for (int stone : stones) {
            if (stone > 0) {
                return false;
            }
        }
        return true;
    }
}