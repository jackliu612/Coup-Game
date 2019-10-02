import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<String> names = new ArrayList<>();
        System.out.println("Enter names (type ESC to finish)");
        String s = input.nextLine();
        while (!s.equals("ESC")) {
            names.add(s);
            s = input.nextLine();
        }
        CoupGame cGame = new CoupGame(names);
        cGame.playManualGame();
    }
}
