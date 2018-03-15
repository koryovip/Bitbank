package gui.popup;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import gui.form.SetupDialog;

public class TablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = -8857893357535730207L;

    private final JMenuItem delete;

    public TablePopupMenu() {
        super();
        add("add").addActionListener(e -> {
            //            JTable table = (JTable) getInvoker();
            //            RowDataModel model = (RowDataModel) table.getModel();
            //            model.addRowData(new RowData("New row", ""));
            //            Rectangle r = table.getCellRect(model.getRowCount() - 1, 0, true);
            //            table.scrollRectToVisible(r);

        });
        addSeparator();
        delete = add("delete");
        delete.addActionListener(e -> {
            //            JTable table = (JTable) getInvoker();
            //            DefaultTableModel model = (DefaultTableModel) table.getModel();
            //            int[] selection = table.getSelectedRows();
            //            for (int i = selection.length - 1; i >= 0; i--) {
            //                model.removeRow(table.convertRowIndexToModel(selection[i]));
            //            }

            JDialog dialog = new SetupDialog("title");
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(getRootPane());
            dialog.setVisible(true);
        });
    }

    @Override
    public void show(Component c, int x, int y) {
        if (c instanceof JTable) {
            delete.setEnabled(((JTable) c).getSelectedRowCount() > 0);
            super.show(c, x, y);
        }
    }
}