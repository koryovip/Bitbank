package gui.action;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public abstract class LongSpanBtnAction implements java.awt.event.ActionListener {

    private final JButton btn;
    private final GUIController gui;

    public LongSpanBtnAction(JButton btn, GUIController controller) {
        super();
        this.btn = btn;
        this.gui = controller;
    }

    @Override
    final public void actionPerformed(final ActionEvent event) {
        busy();
        this.btn.setEnabled(false);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    doClick(event);
                } catch (Exception e) {
                    normal();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(gui.parentComponent(), e.toString(), "タイトル(例外)", JOptionPane.INFORMATION_MESSAGE);
                } finally {
                    normal();
                    btn.setEnabled(true);
                }
            }
        });
    }

    abstract public void doClick(ActionEvent event) throws Exception;

    private void busy() {
        gui.parentComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void normal() {
        gui.parentComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}
