import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GrundyNumberTable {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the value for x: ");
        int x = scanner.nextInt();
        System.out.print("Enter the value for y: ");
        int y = scanner.nextInt();
        
        int maxSticks = 19; // Change this to display more or fewer numbers
        List<Integer> grundyNumbers = calculateGrundyNumbers(maxSticks, x, y);
        
        // Print the header
        System.out.printf("%-18s", "Number of Sticks");
        for (int i = 0; i <= maxSticks; i++) {
            System.out.printf("%3d", i);
        }
        System.out.println();
        
        System.out.printf("%-18s", "Grundy Number");
        for (int grundy : grundyNumbers) {
            System.out.printf("%3d", grundy);
        }
        System.out.println();
        
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
}