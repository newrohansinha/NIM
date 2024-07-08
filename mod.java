import java.util.Scanner;

public class mod {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter the win table as a space-separated string (1 for Player 1 wins, 2 for Player 2 wins):");
        String input = scanner.nextLine();
        String[] entries = input.split(" ");
        int[] winTable = new int[entries.length];


        for (int i = 0; i < entries.length; i++) {
            winTable[i] = Integer.parseInt(entries[i].trim());
        }


        boolean foundPattern = false;
        for (int m = 1; m < winTable.length; m++) {
            boolean hasPattern = true;
            for (int i = 0; i < winTable.length; i++) {
                if (winTable[i] != winTable[i % m]) {
                    hasPattern = false;
                    break;
                }
            }
            if (hasPattern) {

                System.out.println("The smallest modulus with a consistent pattern is: " + m);
                System.out.println("Player 2 wins if:");
                System.out.print("n ≡ ");
                for (int j = 0; j < m; j++) {
                    if (winTable[j] == 2) {
                        System.out.print(j+", ");
                    }
                }
                System.out.println("mod "+m);
                foundPattern = true;
                break;
            }
        }

        if (!foundPattern) {
            System.out.println("No repeating mod pattern found within the given win table.");
        }

        scanner.close();
    }
}
