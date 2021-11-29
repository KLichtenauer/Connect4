    import java.util.Scanner;

    public class Shell {

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            boolean programRunning = true;
            while (programRunning) {
                System.out.print("connect4> ");
                String input = scanner.nextLine();
                char keyChar = Character.toLowerCase(input.charAt(0));
                switch (keyChar) {
                    case 'l':
                        break;
                    case 'n':
                        break;
                    case 's':
                        break;
                    case 'w':
                        break;
                    case 'p':
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


