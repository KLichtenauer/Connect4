package connect4;

import connect4.view.View;

import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(View::new);
        //SwingUtilities.invokeLater(new Runnable() {
        //    @Override
        //    public void run() {
        //        new View();
        //    }
        //});
    }
}
