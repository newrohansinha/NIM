import java.util.ArrayList;
import java.util.List;

public class test{
    public static void main(String[] args) {
        int maxX = 20;
        int maxY = 20;
        int maxSticks = 500;

        System.out.println("Finding pairs of X and Y with starting positions having multiple optimal moves:");
        for (int x = 1; x <= maxX; x++) {
            for (int y = 1; y <= maxY; y++) {
                List<Integer> positionsWithMultipleOptimal = findMultipleOptimalMoves(x, y, maxSticks);
                if (!positionsWithMultipleOptimal.isEmpty()) {
                    System.out.println("For X = " + x + ", Y = " + y + ", positions with multiple optimal moves:");
                    System.out.println(positionsWithMultipleOptimal);
                }
            }
        }

    }

    /**
     * Find positions where both x and y moves are optimal.
     *
     * @param x        First move option.
     * @param y        Second move option.
     * @param length   Number of sticks to consider.
     * @return List of starting positions with multiple optimal moves.
     */
    private static List<Integer> findMultipleOptimalMoves(int x, int y, int length) {
        int[] winTable = new int[length + 1];
        winTable[0] = 2; // Losing position for the starting player

        // Fill the win table with perfect play strategy
        for (int i = 1; i <= length; i++) {
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

        List<Integer> positionsWithMultipleOptimal = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            boolean optimalX = (i - x >= 0 && winTable[i - x] == 2);
            boolean optimalY = (i - y >= 0 && winTable[i - y] == 2);
            if (optimalX && optimalY) {
                positionsWithMultipleOptimal.add(i);
            }
        }

        return positionsWithMultipleOptimal;
    }
}
