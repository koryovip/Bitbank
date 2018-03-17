package gui.form;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetupDialog extends JDialog {
    private Logger logger = LogManager.getLogger();
    private static final long serialVersionUID = -6448674911084382078L;

    final private BigDecimal bought;
    private BigDecimal lostCut;
    final private JTextField lblLostCut = new JTextField();

    public SetupDialog(String title, BigDecimal bought) {
        super(JOptionPane.getFrameForComponent(BitBankMainFrame.me()), title, true);
        this.bought = bought;
        this.lostCut = bought;
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

    private JPanel makePanel2(Action act) {
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

    private JPanel makePanel(Action act) {
        JPanel p = new JPanel();
        p.setLayout(null);

        {
            JLabel label = new JLabel(this.bought.toPlainString());
            label.setBounds(10, 10, 100, 24);
            p.add(label);
        }
        {
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.1, 0, 10, 0.01));
            spinner.setBounds(110, 10, 100, 24);
            JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0%");
            editor.getTextField().setEditable(false);

            JFormattedTextField field = (JFormattedTextField) editor.getComponent(0);
            DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
            formatter.setCommitsOnValidEdit(true);
            spinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    //                    logger.debug("value changed: " + spinner.getValue());
                    // lostCut = bought.subtract(bought.multiply(new BigDecimal(spinner.getValue().toString())).divide(BigDecimal.TEN)).setScale(3, RoundingMode.HALF_UP);
                    updateLostCutValue(new BigDecimal(spinner.getValue().toString()));
                    lblLostCut.setText(lostCut.toPlainString());
                }
            });
            spinner.setEditor(editor);
            p.add(spinner);
            updateLostCutValue(new BigDecimal(spinner.getValue().toString()));
        }
        {
            lblLostCut.setBounds(250, 10, 200, 24);
            lblLostCut.setText(lostCut.toPlainString());
            p.add(lblLostCut);
        }
        {
            JButton btn = new JButton(act);
            btn.setBounds(110, 290, 100, 24);
            p.add(btn);
        }
        p.setPreferredSize(new Dimension(480, 320));
        return p;
    }

    private void updateLostCutValue(BigDecimal rate) {
        lostCut = bought.subtract(bought.multiply(rate).divide(BigDecimal.TEN)).setScale(3, RoundingMode.HALF_UP);
    }
}
