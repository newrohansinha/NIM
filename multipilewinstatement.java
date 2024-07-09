import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class multipilewinstatement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read inputs for the first pile
        System.out.print("Enter the value for x for pile 1: ");
        int x1 = scanner.nextInt();
        System.out.print("Enter the value for y for pile 1: ");
        int y1 = scanner.nextInt();

        // Read inputs for the second pile
        System.out.print("Enter the value for x for pile 2: ");
        int x2 = scanner.nextInt();
        System.out.print("Enter the value for y for pile 2: ");
        int y2 = scanner.nextInt();

        int maxSticks = 50; // Adjust this limit if needed
        List<Integer> grundyNumbersPile1 = calculateGrundyNumbers(maxSticks, x1, y1);
        List<Integer> grundyNumbersPile2 = calculateGrundyNumbers(maxSticks, x2, y2);

        // Find and print the generalized win statement
        printGeneralizedWinStatement(grundyNumbersPile1, grundyNumbersPile2);

        scanner.close();
    }

    private static List<Integer> calculateGrundyNumbers(int maxSticks, int x, int y) {
        List<Integer> grundy = new ArrayList<>();
        grundy.add(0); // Grundy number for 0 sticks is always 0

        for (int i = 1; i <= maxSticks; i++) {
            Set<Integer> reachable = new HashSet<>();

            // Check possible moves (remove 1, x, or y sticks)
            if (i >= 1) reachable.add(grundy.get(i - 1));
            if (i >= x) reachable.add(grundy.get(i - x));
            if (i >= y) reachable.add(grundy.get(i - y));

            // Find the mex (minimum excludant)
            int grundyNumber = 0;
            while (reachable.contains(grundyNumber)) {
                grundyNumber++;
            }
            grundy.add(grundyNumber);
        }

        return grundy;
    }

    private static void printGeneralizedWinStatement(List<Integer> grundyPile1, List<Integer> grundyPile2) {
        int period1 = findPeriod(grundyPile1);
        int period2 = findPeriod(grundyPile2);

        List<Integer> patterns1 = getUniquePatterns(grundyPile1, period1);
        List<Integer> patterns2 = getUniquePatterns(grundyPile2, period2);

        System.out.printf("Player 2 wins if the Grundy number of Pile 1 mod %d is in %s\n", period1, patterns1);
        System.out.printf("AND\n");
        System.out.printf("Player 2 wins if the Grundy number of Pile 2 mod %d is in %s\n", period2, patterns2);
    }

    private static int findPeriod(List<Integer> grundyNumbers) {
        for (int period = 1; period <= grundyNumbers.size() / 2; period++) {
            boolean isPeriod = true;
            for (int i = 0; i < period; i++) {
                for (int j = i + period; j < grundyNumbers.size(); j += period) {
                    if (!grundyNumbers.get(i).equals(grundyNumbers.get(j))) {
                        isPeriod = false;
                        break;
                    }
                }
                if (!isPeriod) break;
            }
            if (isPeriod) return period;
        }
        return grundyNumbers.size(); // Default to the entire range if no period found
    }

    private static List<Integer> getUniquePatterns(List<Integer> grundyNumbers, int period) {
        Set<Integer> uniquePatterns = new HashSet<>();
        for (int i = 0; i < period; i++) {
            uniquePatterns.add(grundyNumbers.get(i));
        }
        return new ArrayList<>(uniquePatterns);
    }
}
