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
                        regulateLevel(parts, game);
                        break;
                    case 'm':
                        regulateMove(parts, game);
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

        private static void regulateLevel(String[] parts, Game game) {
            String toTransform = parts[1];
            if(isInteger(toTransform) && gameInitialized(game)){
                int level = Integer.parseInt(toTransform);
                game.setLevel(level);
            }
        }

        private static boolean gameInitialized(Game game) {
            return game != null;
        }

        // TODO: 30.11.2021 noch sehr unschön! wahrschienlich mit excepiton
        private static void regulateMove(String[] parts, Game game) {
            String colo = parts[1];
            if(isInteger(colo) && gameInitialized(game)){
                int col = Integer.parseInt(colo);
                game.move(col);
            }
        }

        private static boolean isInteger(String part) {
            boolean isInt = true;
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(i) < '0' || part.charAt(i) > '9') {
                    isInt = false;
                    error("Method has needs the form <MOVE i>, " +
                            "with i as an Integer!");
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


