import java.util.Random;
import java.util.Scanner;

public class Strategy4 {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Strategy 4: Pick random optimal and minimum move if no optimal\n");
        System.out.println("Enter X:");
        int x = reader.nextInt();
        System.out.println("Enter Y:");
        int y = reader.nextInt();
        System.out.println("Enter Player 1 accuracy (0.0 to 1.0):");
        double player1Accuracy = reader.nextDouble();
        System.out.println("Enter Player 2 accuracy (0.0 to 1.0):");
        double player2Accuracy = reader.nextDouble();

        int maxSticks = 1000;
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

        // Counters for win percentages
        int player1Wins = 0;
        int player2Wins = 0;
        for (int i = 0; i < 100000; i++) {
            int winner = simulateGame(20, x, y, winTable, player1Accuracy, player2Accuracy, rand);
            if (winner == 1) {
                player1Wins++;
            } else if (winner == 2) {
                player2Wins++;

            }
        }


        System.out.println("Player 1 wins: " + (double)player1Wins/1000+"%");
        System.out.println("Player 2 wins: " + (double)player2Wins/1000+"%");
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
     * @return The winner of the game (1 for Player 1, 2 for Player 2).
     */
    private static int simulateGame(int sticks, int x, int y, int[] winTable, double player1Accuracy, double player2Accuracy, Random rand) {
        boolean player1Turn = true;

        while (sticks > 0) {
            double accuracy = player1Turn ? player1Accuracy : player2Accuracy;
            boolean canWin = (player1Turn && winTable[sticks] == 1) || (!player1Turn && winTable[sticks] == 1);
            int currentPlayer = player1Turn ? 1 : 2;
            int move;

            if (canWin) { // Player can potentially win
                move = randomOptimalMove(sticks, x, y, winTable, rand);
                if (rand.nextDouble() > accuracy) { // Use a suboptimal move instead
                    move = minimumMove(sticks, x, y); // Choose the smallest valid move
                }
            } else { // Player is in a losing position
                move = minimumMove(sticks, x, y); // Always choose the smallest valid move
            }

            sticks -= move;

            if (sticks <= 0) {
                return currentPlayer;
            }

            player1Turn = !player1Turn; // Switch turns
        }

        return -1; // Should never reach here
    }

    /**
     * Determines a random optimal move given the current number of sticks.
     *
     * @param sticks  The current number of sticks.
     * @param x       The first special move.
     * @param y       The second special move.
     * @param winTable The win table indicating winning/losing positions.
     * @param rand    Random number generator.
     * @return A random optimal move (winning move if available).
     */
    private static int randomOptimalMove(int sticks, int x, int y, int[] winTable, Random rand) {
        int[] possibleMoves = {1, x, y};
        int[] optimalMoves = new int[3];
        int count = 0;

        for (int move : possibleMoves) {
            if (sticks - move >= 0 && winTable[sticks - move] == 2) {
                optimalMoves[count++] = move;
            }
        }

        if (count > 0) {
            return optimalMoves[rand.nextInt(count)];
        }

        // Fallback if no winning move found
        return minimumMove(sticks, x, y);
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
        int[] possibleMoves = {1, x, y};
        int minMove = Integer.MAX_VALUE;

        for (int move : possibleMoves) {
            if (sticks - move >= 0) {
                minMove = Math.min(minMove, move);
            }
        }

        return minMove;
    }
}
