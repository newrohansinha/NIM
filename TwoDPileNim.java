import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TwoDPileNim {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the number of piles: ");
        int numPiles = scanner.nextInt();
        
        int[][] moves = new int[numPiles][3];
        for (int i = 0; i < numPiles; i++) {
            System.out.println("For pile " + (i + 1) + ":");
            System.out.print("Enter move x: ");
            moves[i][0] = scanner.nextInt();
            System.out.print("Enter move y: ");
            moves[i][1] = scanner.nextInt();
            moves[i][2] = 1; // Always include move of removing 1 stick
        }
        
        int maxSticks = 19; // Adjust as needed
        List<List<Integer>> grundyTables = new ArrayList<>();
        
        for (int i = 0; i < numPiles; i++) {
            grundyTables.add(calculateGrundyNumbers(maxSticks, moves[i][0], moves[i][1]));
        }
        
        List<Integer> combinedGrundyNumbers = calculateCombinedGrundyNumbers(grundyTables, maxSticks);
        
        System.out.print("Player 2 wins if n_i is equivalent to: ");
        List<Integer> winConditions = determineWinConditions(combinedGrundyNumbers);
        System.out.println(winConditions + " mod " + combinedGrundyNumbers.size());
        
        System.out.print("Player 1 wins if n_i is NOT equivalent to: ");
        System.out.println(winConditions + " mod " + combinedGrundyNumbers.size());
        
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
    
    private static List<Integer> calculateCombinedGrundyNumbers(List<List<Integer>> grundyTables, int maxSticks) {
        List<Integer> combined = new ArrayList<>();
        
        for (int i = 0; i <= maxSticks; i++) {
            int nimSum = 0;
            for (List<Integer> table : grundyTables) {
                nimSum ^= table.get(i);
            }
            combined.add(nimSum);
        }
        
        return combined;
    }
    
    private static List<Integer> determineWinConditions(List<Integer> combinedGrundyNumbers) {
        List<Integer> winConditions = new ArrayList<>();
        int mod = combinedGrundyNumbers.size();
        
        for (int i = 0; i < combinedGrundyNumbers.size(); i++) {
            if (combinedGrundyNumbers.get(i) == 0) {
                winConditions.add(i % mod);
            }
        }
        
        return winConditions;
    }
}
