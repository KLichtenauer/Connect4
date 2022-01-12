package connect4;

import connect4.model.Board;
    import connect4.model.Coordinates2D;
    import connect4.model.Game;
    import connect4.model.Player;
    import connect4.model.Validate;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.List;


    /**
     * Communicates with the user
     */
    public final class Shell {

        /**
         * The needed integer-values for the commands move and level are located
         * in the second part of the command "move x". Because the inputs get
         * split and put in arrays, the index of the value is 1.
         */
        private static final int INDEX_OF_VALUE = 1;

        /**
         * The default level for every game is 4.
         */
        private static final int DEFAULT_LEVEL = 4;

        private Shell() {}

        /**
         * The applications' entry point. Creates a new buffered reader to be
         * able to process the users input. Calls
         * {@link #execute(BufferedReader)} for managing the core part of the
         * program.
         *
         * @param args Command-line arguments.
         * @throws IOException Gets thrown if buffered reader can't read a line.
         */
        public static void main(String[] args) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            execute(reader);
        }

        /**
         * Manages each input of the user by splitting it to a string array and
         * depending on the first letter the corresponding action happens. If
         * not,the users gave an invalid input and a matching error message will
         * be shown.
         *
         * @param reader The reader for processing the users input.
         * @throws IOException Gets thrown if buffered reader can't read a line.
         */
        private static void execute(BufferedReader reader) throws IOException {
            int setLevel = DEFAULT_LEVEL;
            Board game = new Game(true, setLevel);
            boolean programRunning = true;
            while (programRunning) {
                System.out.print("connect4> ");
                String input = reader.readLine();
                char keyChar = ' ';
                String[] parts = input.split(" ");
                if (!input.isEmpty()) {
                    keyChar = Character.toLowerCase(input.charAt(0));
                }
                switch (keyChar) {
                    case 'l':
                        setLevel = regulateLevel(parts, game);
                        break;
                    case 'm':
                        Game gameAfterMove = (Game) regulateMove(parts, game);
                        if (gameAfterMove != null) {
                            game = gameAfterMove;
                            if (!game.isGameOver()) {
                                game = game.machineMove();
                            }
                            if (game.isGameOver()) {
                                regulateGameEnd(game);
                            }
                        }
                        break;
                    case 'n':
                        game = new Game(true, setLevel);
                        break;
                    case 's':
                        assert game != null;
                        game = regulateSwitch(game, setLevel);
                        break;
                    case 'w':
                        regulateWitness(game);
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

        private static void regulateWitness(Board game) {
            if (game != null && game.getWitness() != null) {
                List<Coordinates2D> list =
                        (List<Coordinates2D>) game.getWitness();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    builder.append(list.get(i).toString());
                    builder.append(", ");
                }
                builder.append(list.get(3).toString());
                System.out.println(builder);
            } else {
                error("No witness found.");
            }
        }

        private static void regulateGameEnd(Board game) {
            assert game != null;
            Player winner = game.getWinner();
            if (winner == Player.HUMAN) {
                System.out.println("Congratulations! You won.");
            } else if (winner == Player.BOT) {
                System.out.println("Sorry! Machine wins.");
            } else {
                System.out.println("Nobody wins. Tie.");
            }
        }

        private static Board regulateMove(String[] parts, Board game) {
            assert parts != null && game != null;
            int chosenCol = getValue(parts);
            Board gameAfterMove = null;
            if (!Validate.colIsValid(chosenCol) || game.isGameOver()) {
                error("Invalid column given!");
            } else {
                gameAfterMove = game.move(chosenCol);
                if (gameAfterMove == null) {
                    error("Move not possible. Please try again with "
                            + "another column.");
                }
            }

            return gameAfterMove;
        }

        private static int regulateLevel(String[] parts, Board game) {
            assert parts != null && game != null;
            int levelToBeSet = getValue(parts);
            if (Validate.levelIsValid(levelToBeSet)) {
                game.setLevel(levelToBeSet);
            } else {
                error("Given level is invalid.");
            }
            return levelToBeSet;
        }

        private static Game regulateSwitch(Board game, int setLevel) {
            assert game != null;
            Game newGame;
            boolean isFirstPlayerHuman = game.getFirstPlayer() == Player.HUMAN;
                newGame = new Game(!isFirstPlayerHuman, setLevel);
                newGame.setLevel(setLevel);
                if (isFirstPlayerHuman) {
                    newGame = (Game) newGame.machineMove();
                }
            return newGame;
        }

        private static int getValue(String[] parts) {
            assert parts != null;
            String value;
            /*
             * Choosing an invalid return value, so if input is invalid and no
             * value can be read an invalid value gets returned, because the
             * methods who use this method can't use the value and the input
             * won't be used.
             */
            int returnValue = Integer.MAX_VALUE;
            if (parts.length < 2) {
                error("Command not correct, pleas use <HELP> if "
                        + "commands are not known.");
            } else {
                value = parts[INDEX_OF_VALUE];
                if (isInteger(value)) {
                    returnValue = Integer.parseInt(value);
                }
            }
            return returnValue;
        }

        /**
         * Checks if given {@code String} can be transformed to an
         * {@code Integer}.
         *
         * @param part The part of {@code String} to be checked.
         * @return If the part can be transformed {@code true}, {@code false}
         *         otherwise.
         */
        private static boolean isInteger(String part) {
            assert part != null;
            boolean isInt = true;
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(0) < '0' || part.charAt(0) > '9') {
                    isInt = false;
                    break;
                }
            }
            return isInt;
        }

        private static String errorMessage() {
            return ("Make sure you use the correct"
                    + " commands! \nIf you need help with the commands just "
                    + "type HELP in the commandline ");
        }

        private static void helpInMain() {
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

        private static void error(String errorMessage) {
            System.out.println("Error! " + errorMessage);
        }
    }


