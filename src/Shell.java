    import model.Board;
    import model.Game;
    import model.Player;
    import model.Validate;

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
                        game = game.machineMove(); // ITS HAPPENING
                        break;
                    case 'n':
                        game = new Game(true);
                        break;
                    case 's':
                        assert game != null;
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
            assert parts != null;
            assert game != null;
            int chosenCol = getValue(parts);
            if(Validate.colIsValid(chosenCol)) {
                return game.move(chosenCol);
            } else {
                return null;
            }
        }

        private static void regulateLevel(String[] parts, Board game) {
            assert parts != null;
            assert game != null;
            int levelToBeSet = getValue(parts);
            if (Validate.levelIsValid(levelToBeSet)) {
                game.setLevel(levelToBeSet);
            }
        }

        private static Game regulateSwitch(Board game) {
            assert game != null;
            Game newGame;
            if(game.getFirstPlayer() == Player.HUMAN) {
                newGame = new Game(false);
                newGame.machineMove();
            } else {
                newGame = new Game(true);
            }
            return newGame;
        }

        private static int getValue(String[] parts) {
            assert parts != null;
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
            assert part != null;
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


