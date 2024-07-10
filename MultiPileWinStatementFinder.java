import java.util.*;

public class MultiPileWinStatementFinder {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // For simplicity, assume the same max number of sticks for all piles
        int maxSticks = 50;  // Adjust as needed

        // Get the number of piles
        System.out.print("Enter the number of piles: ");
        int numPiles = scanner.nextInt();

        int[][] moves = new int[numPiles][2];

        for (int i = 0; i < numPiles; i++) {
            System.out.println("For pile " + (i + 1) + ":");
            System.out.print("Enter move x: ");
            moves[i][0] = scanner.nextInt();
            System.out.print("Enter move y: ");
            moves[i][1] = scanner.nextInt();
        }

        List<List<Integer>> grundyTables = new ArrayList<>();

        // Calculate Grundy numbers for each pile
        for (int i = 0; i < numPiles; i++) {
            grundyTables.add(calculateGrundyNumbers(maxSticks, moves[i][0], moves[i][1]));
        }

        // Calculate periodicity for each pile
        List<Integer> periods = new ArrayList<>();
        for (int i = 0; i < grundyTables.size(); i++) {
            int period = findCyclePeriod(grundyTables.get(i));
            periods.add(period);
        }

        // Print the conditions under which Player 2 wins
        printWinConditions(grundyTables, periods);

        scanner.close();
    }

    private static List<Integer> calculateGrundyNumbers(int maxSticks, int x, int y) {
        List<Integer> grundy = new ArrayList<>();
        grundy.add(0); // Grundy number for 0 sticks is always 0

        for (int i = 1; i <= maxSticks; i++) {
            List<Integer> reachable = new ArrayList<>();

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

    private static int findCyclePeriod(List<Integer> grundyNumbers) {
        for (int period = 1; period < grundyNumbers.size(); period++) {
            boolean isCycle = true;
            for (int i = 0; i < grundyNumbers.size() - period; i++) {
                if (!grundyNumbers.get(i).equals(grundyNumbers.get(i + period))) {
                    isCycle = false;
                    break;
                }
            }
            if (isCycle) {
                return period;
            }
        }
        return grundyNumbers.size();
    }

    private static void printWinConditions(List<List<Integer>> grundyTables, List<Integer> periods) {
        System.out.println("\nPlayer 2 wins if:");

        Map<Integer, Set<int[]>> winConditions = new HashMap<>();
        generateWinConditions(new int[grundyTables.size()], 0, periods, grundyTables, winConditions);

        for (int grundyNumber = 0; grundyNumber < 4; grundyNumber++) {
            if (winConditions.containsKey(grundyNumber)) {
                Set<int[]> conditions = winConditions.get(grundyNumber);
                List<Set<Integer>> sets = new ArrayList<>();
                for (int i = 0; i < grundyTables.size(); i++) {
                    sets.add(new HashSet<>());
                }

                for (int[] condition : conditions) {
                    for (int i = 0; i < condition.length; i++) {
                        sets.get(i).add(condition[i]);
                    }
                }

                printCondition(sets, periods);
            }
        }
    }

    private static void printCondition(List<Set<Integer>> sets, List<Integer> periods) {
        for (int i = 0; i < sets.size(); i++) {
            String setString = formatSetString(sets.get(i), "n" + (i + 1), periods.get(i));
            System.out.print(setString);
            if (i < sets.size() - 1) {
                System.out.print(" and ");
            }
        }
        System.out.println();
    }

    private static String formatSetString(Set<Integer> set, String variable, int period) {
        if (set.size() == 1) {
            return variable + " ≡ " + set.iterator().next() + " (mod " + period + ")";
        } else {
            return variable + " ≡ " + set +" (mod "+period+")";
        }
    }

    private static void generateWinConditions(int[] current, int index, List<Integer> periods, List<List<Integer>> grundyTables, Map<Integer, Set<int[]>> winConditions) {
        if (index == grundyTables.size()) {
            int nimSum = 0;
            for (int i = 0; i < current.length; i++) {
                nimSum ^= grundyTables.get(i).get(current[i]);
            }
            if (nimSum == 0) {
                int grundyNumber = grundyTables.get(0).get(current[0]);
                winConditions.computeIfAbsent(grundyNumber, k -> new HashSet<>()).add(current.clone());
            }
            return;
        }

        for (int i = 0; i < periods.get(index); i++) {
            current[index] = i;
            generateWinConditions(current, index + 1, periods, grundyTables, winConditions);
        }
    }
}