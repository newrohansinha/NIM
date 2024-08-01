import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TwoPileWinPercentIncrementor {
    private static HashMap<String, Integer> grundyCache = new HashMap<>();
    private static Random rand = new Random();

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter X1 for pile 1:");
        int x1 = reader.nextInt();
        System.out.println("Enter Y1 for pile 1:");
        int y1 = reader.nextInt();
        System.out.println("Enter X2 for pile 2:");
        int x2 = reader.nextInt();
        System.out.println("Enter Y2 for pile 2:");
        int y2 = reader.nextInt();
        System.out.println("Enter sticks step: ");
        int sticksStep = reader.nextInt();
        System.out.println("Enter start stick for pile 1: ");
        int startStick1 = reader.nextInt();
        System.out.println("Enter start stick for pile 2: ");
        int startStick2 = reader.nextInt();
        System.out.println("Enter Player 1 accuracy (0.0 to 1.0):");
        double player1Accuracy = reader.nextDouble();
        System.out.println("Enter Player 2 accuracy (0.0 to 1.0):");
        double player2Accuracy = reader.nextDouble();

        try (PrintWriter writer = new PrintWriter(new FileWriter("twopileResults.txt"))) {
            for (int s1 = startStick1; s1 <= 100; s1 += sticksStep) {
                for (int s2 = startStick2; s2 <= 100; s2 += sticksStep) {
                    int player1Wins = 0;
                    int totalGames = 10000;

                    for (int i = 0; i < totalGames; i++) {
                        int[] stones = {s1, s2};
                        int[] xValues = {x1, x2};
                        int[] yValues = {y1, y2};
                        int winner = simulateGame(stones, xValues, yValues, player1Accuracy, player2Accuracy);
                        if (winner == 1) {
                            player1Wins++;
                        }
                    }

                    double player1WinPercent = (double) player1Wins / totalGames * 100;
                    writer.printf("[%d, %d, %.2f], ", s1, s2, player1WinPercent);
                }
                writer.println(); // New line after each s1 increment
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

        System.out.println("Simulation completed. Results are written to twopileResults.txt.");
    }

    private static int simulateGame(int[] stones, int[] xValues, int[] yValues, double player1Accuracy, double player2Accuracy) {
        boolean player1Turn = true;

        while (!isGameOver(stones)) {
            double accuracy = player1Turn ? player1Accuracy : player2Accuracy;
            int optimalMove = getOptimalMove(stones, xValues, yValues);
            int pile = optimalMove / 1000;
            int move = optimalMove % 1000;

            if (rand.nextDouble() <= accuracy) {
                stones[pile] -= move;
            } else {
                do {
                    pile = rand.nextInt(stones.length);
                    move = Math.min(stones[pile], Math.max(1, rand.nextInt(Math.max(xValues[pile], yValues[pile])) + 1));
                } while (stones[pile] == 0);
                stones[pile] -= move;
            }

            player1Turn = !player1Turn;
        }

        return player1Turn ? 2 : 1; // Return the winner (1 or 2)
    }

    private static int getOptimalMove(int[] stones, int[] xValues, int[] yValues) {
        int bestMove = -1;
        int maxStonesToRemove = 0;
        int minStonesToRemove = Integer.MAX_VALUE;
        boolean hasOptimalMove = false;

        for (int pile = 0; pile < stones.length; pile++) {
            int[] possibleMoves = {1, xValues[pile], yValues[pile]};
            for (int move : possibleMoves) {
                if (stones[pile] >= move) {
                    stones[pile] -= move;
                    int newNimSum = calculateNimSum(stones, xValues, yValues);
                    stones[pile] += move;

                    if (newNimSum == 0) {
                        hasOptimalMove = true;
                        if (move > maxStonesToRemove) {
                            maxStonesToRemove = move;
                            bestMove = pile * 1000 + move; // Encode pile and move
                        }
                    } else if (!hasOptimalMove && move < minStonesToRemove) {
                        minStonesToRemove = move;
                        bestMove = pile * 1000 + move;
                    }
                }
            }
        }

        if (bestMove == -1) {
            // If there's no valid move found, choose the first available move
            for (int pile = 0; pile < stones.length; pile++) {
                if (stones[pile] > 0) {
                    bestMove = pile * 1000 + 1; // Remove 1 stone from the first non-empty pile
                    break;
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
