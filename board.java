import java.util.Scanner;

public class board {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter maximum sticks for analysis:");
        int maxSticks = reader.nextInt();
        System.out.println("Enter X1 for Pile 1 (first move):");
        int x1 = reader.nextInt();
        System.out.println("Enter Y1 for Pile 1 (second move):");
        int y1 = reader.nextInt();
        System.out.println("Enter X2 for Pile 2 (first move):");
        int x2 = reader.nextInt();
        System.out.println("Enter Y2 for Pile 2 (second move):");
        int y2 = reader.nextInt();

        int[][] grundyTable = new int[maxSticks + 1][maxSticks + 1];

        // Initialize the grundy table
        for (int i = 0; i <= maxSticks; i++) {
            for (int j = 0; j <= maxSticks; j++) {
                grundyTable[i][j] = calculateGrundy(i, j, x1, y1, x2, y2, grundyTable);
            }
        }

        // Print out the 2D grundy table
        System.out.println("2D Grundy Table (0: Lose, >0: Win):");
        System.out.print("   ");
        for (int i = 0; i <= maxSticks; i++) {
            System.out.printf("%3d", i);
        }
        System.out.println();

        System.out.println();
        for (int i = 0; i <= maxSticks; i++) {
            System.out.printf("%3d", i);
            for (int j = 0; j <= maxSticks; j++) {
                int winLose = grundyTable[i][j] == 0 ? 2 : 1;
                System.out.printf("%3d", winLose);
            }
            System.out.println();
        }
        reader.close();
      
    }

    public static int calculateGrundy(int pile1, int pile2, int x1, int y1, int x2, int y2, int[][] grundyTable) {
        // Base case
        if (pile1 == 0 && pile2 == 0) {
            return 0;
        }

        // Set to keep track of reachable Grundy numbers
        boolean[] reachable = new boolean[100]; // Assuming max grundy number will be less than 100

        // Possible moves for Pile 1
        if (pile1 - 1 >= 0) reachable[grundyTable[pile1 - 1][pile2]] = true;
        if (pile1 - x1 >= 0) reachable[grundyTable[pile1 - x1][pile2]] = true;
        if (pile1 - y1 >= 0) reachable[grundyTable[pile1 - y1][pile2]] = true;

        // Possible moves for Pile 2
        if (pile2 - 1 >= 0) reachable[grundyTable[pile1][pile2 - 1]] = true;
        if (pile2 - x2 >= 0) reachable[grundyTable[pile1][pile2 - x2]] = true;
        if (pile2 - y2 >= 0) reachable[grundyTable[pile1][pile2 - y2]] = true;

        // Calculate the minimum excludant (Mex)
        int grundy = 0;
        while (reachable[grundy]) {
            grundy++;
        }

        return grundy;
       
    }
    
}
