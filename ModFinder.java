// Imports
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

// Code
public class ModFinder {
    
    // Main
    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("Enter X: ");
        int X = myObj.nextInt();
        System.out.print("Enter Y: ");
        int Y = myObj.nextInt();
        System.out.println(Arrays.toString(problem_one(X, Y)));
        System.out.println(problem_two(problem_one(X, Y)));
        System.out.println(problem_three(problem_one(X, Y)));
    }

    // Problem One
    // Win Table Generation
    public static int[] problem_one(int x, int y) {
        int[] arr = new int[500];
        int[] moves = {1, x, y};
        Arrays.fill(arr, 2);
        for (int i = 0; i < moves.length; i++) {
            arr[moves[i]] = 1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (i - 1 >= 0) {
                if (arr[i - 1] == 2) {
                    arr[i] = 1;
                }
            }
            if (i - x >= 0) {
                if (arr[i - x] == 2) {
                    arr[i] = 1;
                }
            }
            if (i - y >= 0) {
                if (arr[i - y] == 2) {
                    arr[i] = 1;
                }
            }
        }
        return arr;
    }

    // Problem Two
    // Mod Generator
    public static int problem_two(int[] arr) {
        // Starts with jumps of size 2
        for (int i = 2; i < arr.length; i++) {
            boolean c = true;
            // Goes through every single index and checks each if an the next indices a jump away are also wins 
            for (int j = 0; j < arr.length; j += i) {
                // Returns false if one of the jumps is not a win
                if (arr[j] != 2) {
                    c = false;
                }
            }
            // Returns the jump size if every single jump of size i is completed 
            if (c) {
                return i;
            }
        }
        return 0;
    }

    // 2 will win iff n is equivalent to ____ after it has been modded by the above
    public static ArrayList<Integer> problem_three(int[] arr) {
        // Obtains the mod or jump
        int mod = problem_two(arr);
        ArrayList<Integer> valids = new ArrayList<Integer>();
        for (int i = 0; i < arr.length; i++) {
            // Finds out if the remainder is 2
            int remainder = i % mod;
            if (arr[remainder] == 2) {
                // If the remainder is already added, then it isn't added again
                if (!valids.contains(remainder)) {
                    valids.add(remainder);
                }
            }
        }
        // String answerKey = ("2 wins iff n is equiv " + Arrays.toString(valids.toArray()) + " mod " + mod);
        //return answerKey;
        return valids;
    }
}