    import model.Game;

    import java.util.Scanner;

    public class Shell {


        // TODO: 29.11.2021 Prüfen ob Game initialisiert wurde
        public static void main(String[] args) {
            Game game = null;
            Scanner scanner = new Scanner(System.in);
            boolean programRunning = true;
            while (programRunning) {
                System.out.print("connect4> ");
                String input = scanner.nextLine();
                char keyChar = Character.toLowerCase(input.charAt(0));
                String[] parts = input.split(" ");
                switch (keyChar) {
                    case 'l':

                        break;
                    case 'm':
                        regulateMove(parts);
                        break;
                    case 'n':
                        game = new Game(true);
                        break;
                    case 's':
                        game = new Game(false);
                        break;
                    case 'w':
                        break;
                    case 'p':
                        System.out.println(game);
                        break;
                    case 'h':
                        helpInMain();
                        break;
                    case 'q':
                        programRunning = false;
                        break;
                    default:
                        error(errorMessage());
                }
            }

        }

        private static void regulateMove(String[] parts) {
            if(isInteger(parts[1])){
                System.out.println("JUHU");
            }
        }

        private static boolean isInteger(String part) {
            boolean isInt = true;
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(0) < '0' || part.charAt(0) > '9') {
                    isInt = false;
                    error("Method has to of the form MOVE i, " +
                            "where i is an Integer!");
                    break;
                }
            }

            return isInt;
        }

        private static String errorMessage(){
            return ("Make sure you use the correct" +
                    " commands! \nIf you need help with the commands just" +
                    "type HELP in the commandline ");
        }

        private static void helpInMain(){
            System.out.println(
                    """
                        Following commands can be used in this program:
                        To create a new game: NEW
                        To set the difficulty i: LEVEL i (if no difficulty is
                        chosen, the bot enemy has default difficulty 4
                        To switch the person who plays the first move and
                        restart the game: SWITCH
                        To place a chip in the column c: MOVE c
                        To print out the current game board: PRINT
                        To give out the winning 4-chip-group of the ended game:
                         WITNESS
                        To quit the program: QUIT
                            """);
        }

        private static void error(String errorMessage){
            System.out.println("Error! " + errorMessage);
        }
    }


