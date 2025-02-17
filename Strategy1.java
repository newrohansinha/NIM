import java.util.Random;
import java.util.Scanner;

public class Strategy1 {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Strategy 1: Pick Max optimal and Min move if no optimal\n");
        System.out.println("Enter X:");
        int x = reader.nextInt();
        System.out.println("Enter Y:");
        int y = reader.nextInt();
        System.out.println("Enter Player 1 accuracy (0.0 to 1.0):");
        double player1Accuracy = reader.nextDouble();
        System.out.println("Enter Player 2 accuracy (0.0 to 1.0):");
        double player2Accuracy = reader.nextDouble();

        int maxSticks = 5000;
        int[] winTable = new int[maxSticks + 1];
        winTable[0] = 2; // Losing position for the starting player

        // Fill the win table with perfect play strategy
        for (int i = 1; i <= maxSticks; i++) {
            if (i - 1 >= 0 && winTable[i - 1] == 2) {
                winTable[i] = 1;
            } else if (i - x >= 0 && winTable[i - x] == 2) {
                winTable[i] = 1;
            } else if (i - y >= 0 && winTable[i - y] == 2) {
                winTable[i] = 1;
            } else {
                winTable[i] = 2;
            }
        }

        Random rand = new Random();

        int player1Wins = 0;
        int player2Wins = 0;
        int startingSticks =7;

        // Start simulating the game for 4 starting sticks 100,000 times
        for (int i = 0; i < 100000; i++) {
            int winner = simulateGame(startingSticks, x, y, winTable, player1Accuracy, player2Accuracy, rand);
            if (winner == 1) {
                player1Wins++;
            } else if (winner == 2) {
                player2Wins++;
            }
        }

        System.out.println("Player 1 wins: " + (double) player1Wins / 1000 + "%");
        System.out.println("Player 2 wins: " + (double) player2Wins / 1000 + "%");
        reader.close();
    }

    /**
     * Simulates the game from a given starting number of sticks with controlled imperfections.
     *
     * @param sticks          The initial number of sticks.
     * @param x               The first special move.
     * @param y               The second special move.
     * @param winTable        The win table.
     * @param player1Accuracy The accuracy of Player 1.
     * @param player2Accuracy The accuracy of Player 2.
     * @param rand            Random generator.
     * @return The winner player number (1 or 2).
     */
    private static int simulateGame(int sticks, int x, int y, int[] winTable, double player1Accuracy, double player2Accuracy, Random rand) {
        boolean player1Turn = true;

        while (sticks > 0) {
            double accuracy = player1Turn ? player1Accuracy : player2Accuracy;
            boolean canWin = (player1Turn && winTable[sticks] == 1) || (!player1Turn && winTable[sticks] == 1);
            int currentPlayer = player1Turn ? 1 : 2;
            int move;

            if (canWin) { // Player can potentially win
                move = maxOptimalMove(sticks, x, y, winTable);
                if (rand.nextDouble() > accuracy) { // Use a suboptimal move instead
                    if (x != move) move = x;
                    if (y != move) move = y;
                    if (1 != move) move = 1;
                }
            } else { // Player is in a losing position
                move = Math.min(minimumMove(sticks, x, y), 1); // Always choose the smallest move
            }

            sticks -= move;

            if (sticks <= 0) {
                return currentPlayer; // Winner
            }

            player1Turn = !player1Turn; // Switch turns
        }

        return 0; // Should never reach this point
    }

    /**
     * Determines the maximum optimal move given the current number of sticks.
     *
     * @param sticks  The current number of sticks.
     * @param x       The first special move.
     * @param y       The second special move.
     * @param winTable The win table indicating winning/losing positions.
     * @return The optimal move (winning move if available).
     */
    private static int maxOptimalMove(int sticks, int x, int y, int[] winTable) {
        // Optimal move tries to place the opponent in a losing position
        int maxMove = -1; // Initialize with an invalid value
        if (sticks - 1 >= 0 && winTable[sticks - 1] == 2) maxMove = 1;
        if (sticks - x >= 0 && winTable[sticks - x] == 2) maxMove = Math.max(maxMove, x);
        if (sticks - y >= 0 && winTable[sticks - y] == 2) maxMove = Math.max(maxMove, y);

        if (maxMove == -1) {
            return minimumMove(sticks, x, y); // Fallback if no winning move found
        }

        return maxMove;
    }

    /**
     * Determines the smallest valid move available.
     *
     * @param sticks The current number of sticks.
     * @param x      The first special move.
     * @param y      The second special move.
     * @return The smallest valid move.
     */
    private static int minimumMove(int sticks, int x, int y) {
        int move = 1; // Minimum move is to remove 1 stick
        if (x <= y) {
            if (sticks - x >= 0) move = x;
            if (sticks - y >= 0) move = Math.min(move, y);
        } else {
            if (sticks - y >= 0) move = y;
            if (sticks - x >= 0) move = Math.min(move, x);
        }
        return move;
    }
}
