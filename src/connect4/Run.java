package connect4;

import connect4.view.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Runs the connect-4 gui application.
 */
public final class Run {

    private Run() {}

    /**
     * The applications' entry point.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
