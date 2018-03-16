package gui.popup;

import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import gui.form.BitBankMainFrame;
import gui.form.SetupDialog;
import gui.tablemodel.RowDataModel;

public class TablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = -8857893357535730207L;

    private final JMenuItem calc;
    private final JMenuItem setTS;

    public TablePopupMenu() {
        super();
        calc = add("calc profit");
        calc.addActionListener(e -> {
            JTable table = (JTable) getInvoker();
            RowDataModel model = (RowDataModel) table.getModel();
            BigDecimal profit = BigDecimal.ZERO;
            BigDecimal buyTotal = BigDecimal.ZERO;
            BigDecimal sellTotal = BigDecimal.ZERO;
            for (int row : table.getSelectedRows()) {
                System.out.println(model.getValueAt(row, RowDataModel.COL_INDEX_SIDE));
                if (model.isBuy(row)) {
                    buyTotal = buyTotal.add(model.getExecutedAmount(row).multiply(model.getAveragePrice(row)));
                }
                if (model.isSell(row)) {
                    sellTotal = sellTotal.add(model.getExecutedAmount(row).multiply(model.getAveragePrice(row)));
                }
            }
            System.out.println(sellTotal.subtract(buyTotal));
            JOptionPane.showMessageDialog(BitBankMainFrame.me(), sellTotal.subtract(buyTotal).setScale(0, RoundingMode.HALF_UP), "Profit", JOptionPane.INFORMATION_MESSAGE);
            //            model.addRowData(new RowData("New row", ""));
            //            Rectangle r = table.getCellRect(model.getRowCount() - 1, 0, true);
            //            table.scrollRectToVisible(r);

        });
        addSeparator();
        setTS = add("setTS");
        setTS.addActionListener(e -> {
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
            calc.setEnabled(((JTable) c).getSelectedRowCount() > 0);
            setTS.setEnabled(((JTable) c).getSelectedRowCount() == 1);
            super.show(c, x, y);
        }
    }
}