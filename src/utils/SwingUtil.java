package utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class SwingUtil {

    private static SwingUtil singleton = new SwingUtil();

    public static SwingUtil me() {
        return singleton;
    }

    private SwingUtil() {

    }

    final public void updateFont(JComponent p, Font font) {
        FontUIResource fontUIResource = new FontUIResource(font);
        for (Object o : UIManager.getLookAndFeelDefaults().keySet()) {
            if (o.toString().toLowerCase(Locale.ENGLISH).endsWith("font")) {
                UIManager.put(o, fontUIResource);
            }
        }
        recursiveUpdateUI(p); //SwingUtilities.updateComponentTreeUI(this);
        Container c = p.getTopLevelAncestor();
        if (c instanceof Window) {
            ((Window) c).pack();
        }
    }

    final public void recursiveUpdateUI(JComponent p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JToolBar) {
                continue;
            } else if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.updateUI();
                if (jc.getComponentCount() > 0) {
                    recursiveUpdateUI(jc);
                }
            }
        }
    }

}
