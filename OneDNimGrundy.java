import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class OneDNimGrundy {
    private static HashMap<String, Integer> grundyCache = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Ask the user for the number of piles
        System.out.print("Enter the number of piles: ");
        int numPiles = scanner.nextInt();
        
        // Arrays to store the number of stones and x, y values for each pile
        int[] stones = new int[numPiles];
        int[] xValues = new int[numPiles];
        int[] yValues = new int[numPiles];
        
        // Collect x, y values and number of stones for each pile
        for (int i = 0; i < numPiles; i++) {
            System.out.print("Enter the number of stones for pile " + (i + 1) + ": ");
            stones[i] = scanner.nextInt();
            System.out.print("Enter the value for x for pile " + (i + 1) + ": ");
            xValues[i] = scanner.nextInt();
            System.out.print("Enter the value for y for pile " + (i + 1) + ": ");
            yValues[i] = scanner.nextInt();
        }
        
        // Calculate and print Grundy numbers for each pile, and calculate Nim sum
        int nimSum = 0;
        for (int i = 0; i < numPiles; i++) {
            int grundyNumber = calculateGrundy(stones[i], xValues[i], yValues[i]);
            System.out.println("Grundy number for pile " + (i + 1) + " with " + stones[i] + " stones: " + grundyNumber);
            nimSum ^= grundyNumber;
        }
        
        // Print the Nim sum
        System.out.println("Nim sum: " + nimSum);
        
        scanner.close();
    }

    // Function to calculate Grundy number for a given number of stones
    private static int calculateGrundy(int n, int x, int y) {
        String key = n + "," + x + "," + y;
        if (grundyCache.containsKey(key)) {
            return grundyCache.get(key);
        }

        if (n == 0) {
            return 0;
        }

        HashSet<Integer> set = new HashSet<>();
        
        // Valid moves are 1, x, and y
        if (n >= 1) set.add(calculateGrundy(n - 1, x, y));
        if (n >= x) set.add(calculateGrundy(n - x, x, y));
        if (n >= y) set.add(calculateGrundy(n - y, x, y));

        // Find the minimum excludant (mex)
        int mex = 0;
        while (set.contains(mex)) {
            mex++;
        }

        grundyCache.put(key, mex);
        return mex;
    }
}