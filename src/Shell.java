    import model.Board;
    import model.Game;
    import model.Player;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;


    /**
     * Communicates with the user
     */
    public class Shell {
        private static final int INDEX_OF_VALUE = 1;

        private Shell() {}

        public static void main(String[] args) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            execute(reader);
        }


        private static void execute(BufferedReader reader) throws IOException {
            Board game = new Game(true);
            boolean programRunning = true;
            while (programRunning) {
                System.out.print("connect4> ");
                String input = reader.readLine();
                char keyChar = Character.toLowerCase(input.charAt(0));
                String[] parts = input.split(" ");
                switch (keyChar) {
                    case 'l':
                        regulateLevel(parts, game);
                    case 'm':
                        game = regulateMove(parts, game);
                        break;
                    case 'n':
                        game = new Game(true);
                        break;
                    case 's':
                        game = regulateSwitch(game);
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

        private static Board regulateMove(String[] parts, Board game) {
            int chosenCol = getValue(parts);
            if(chosenCol >= 1 && chosenCol < 8) {
                return game.move(chosenCol);
            } else {
                error("Given column is invalid.");
                return null;
            }
        }

        private static void regulateLevel(String[] parts, Board game) {
            int levelToBeSet = getValue(parts);
            if (levelToBeSet >= 1) {
                game.setLevel(levelToBeSet);
            } else {
                error("Given level is invalid.");
            }
        }

        private static Game regulateSwitch(Board game) {
            Game newGame;
            if(game.getFirstPlayer() == Player.HUMAN) {
                newGame = new Game(false);
            } else {
                newGame = new Game(true);
            }
            return newGame;
        }

        private static int getValue(String[] parts) {
            String value;
            int returnValue = 0;
            try {
                value = parts[INDEX_OF_VALUE];
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Command not correct, pleas "
                        + "use <HELP> if commands are not known.");
            }
            if (isInteger(value)) {
                returnValue = Integer.parseInt(value);
            }
            return returnValue;
        }

        private static boolean isInteger(String part) {
            boolean isInt = true;
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(0) < '0' || part.charAt(0) > '9') {
                    isInt = false;
                    error("Invalid input! Integer needed!");
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


