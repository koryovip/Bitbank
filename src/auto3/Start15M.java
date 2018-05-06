package auto3;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import gui.KRFontManager;
import utils.SwingUtil;

public class Start15M {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        KRFontManager.me().init(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(Candle15MForm.me());
        frame.pack();
        frame.setLocationRelativeTo(null);
        // Font font = new Font("MS Gothic", Font.PLAIN, fontSize);
        SwingUtil.me().updateFont(Candle15MForm.me(), KRFontManager.me().font());
        frame.setVisible(true);
        //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // dispose();
                // System.exit(0); //calling the method is a must
                System.out.println("windowClosing");
            }
        });
    }
}
