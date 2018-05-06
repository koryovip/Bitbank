package gui.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public abstract class KRTablePopupMenu<M extends TableModel> extends JPopupMenu {

    private static final long serialVersionUID = -670363107274398445L;

    public KRTablePopupMenu(JTable table) {
        table.setComponentPopupMenu(this);
    }

    abstract public void show(JTable table, M model);

    @Override
    final public void show(Component c, int x, int y) {
        if (!(c instanceof JTable)) {
            return;
        }
        this.show((JTable) c, (M) ((JTable) c).getModel());
        super.show(c, x, y);
    }

    final public void addAction(JMenuItem item, JMenuItemAction<M> action) {
        item.addActionListener((ActionEvent e) -> {
            JTable table = (JTable) getInvoker();
            M model = (M) table.getModel();
            action.menuClicked(table, model, table.getSelectedRow());
        });
    }

    public interface JMenuItemAction<M extends TableModel> {
        public void menuClicked(JTable table, M model, int selectedRow);
    }
}
