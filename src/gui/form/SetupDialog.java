package gui.form;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class SetupDialog extends JDialog {
    private static final long serialVersionUID = -6448674911084382078L;

    public SetupDialog(String title) {
        super(JOptionPane.getFrameForComponent(BitBankMainFrame.me()), title, true);
        Action act = new AbstractAction("OK") {
            private static final long serialVersionUID = 8570543644383428816L;

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        InputMap imap = super.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close-it");
        super.getRootPane().getActionMap().put("close-it", act);
        super.getContentPane().add(this.makePanel(act));
    }

    private JPanel makePanel(Action act) {
        JPanel p = new JPanel(new GridBagLayout()) {
            private static final long serialVersionUID = -3668199661095757707L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = Math.max(240, d.width);
                return d;
            }
        };
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.LINE_START;
        p.add(new JLabel("AAAAAAAAAAAAAA"), c);

        c.insets = new Insets(5, 0, 5, 0);
        //p.add(new JLabel("<html>Message<br>aaaaaa<br>aaaaaaaaaaa<br>aaaaaaaaaaaaaaaa"), c);
        p.add(new JLabel("Message"), c);

        c.gridwidth = 2;
        c.gridy = 1;
        c.weightx = 1d;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        p.add(new JButton(act), c);

        p.setPreferredSize(new Dimension(480, 320));

        return p;
    }

}
