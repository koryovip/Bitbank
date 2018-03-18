package gui.popup;

import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import cc.Config;
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
            BigDecimal buyTotal = BigDecimal.ZERO;
            BigDecimal buyTotalExecutedAmount = BigDecimal.ZERO;
            BigDecimal sellTotal = BigDecimal.ZERO;
            for (int row : table.getSelectedRows()) {
                if (model.isBuy(row)) {
                    buyTotal = buyTotal.add(model.getExecutedAmount(row).multiply(model.getAveragePrice(row)));
                    buyTotalExecutedAmount = buyTotalExecutedAmount.add(model.getExecutedAmount(row));
                }
                if (model.isSell(row)) {
                    sellTotal = sellTotal.add(model.getExecutedAmount(row).multiply(model.getAveragePrice(row)));
                }
            }
            String msg = "Profit:" + sellTotal.subtract(buyTotal).setScale(Config.me().getRound1(), RoundingMode.HALF_UP).toPlainString();
            msg += "\r\n";
            msg += "AveragePrice:" + buyTotal.divide(buyTotalExecutedAmount, Config.me().getRound1(), RoundingMode.HALF_UP).toPlainString();
            JOptionPane.showMessageDialog(BitBankMainFrame.me(), //
                    msg, //
                    "Profit", JOptionPane.INFORMATION_MESSAGE);
            //            model.addRowData(new RowData("New row", ""));
            //            Rectangle r = table.getCellRect(model.getRowCount() - 1, 0, true);
            //            table.scrollRectToVisible(r);

        });
        addSeparator();
        setTS = add("setTS");
        setTS.addActionListener(e -> {
            JTable table = (JTable) getInvoker();
            RowDataModel model = (RowDataModel) table.getModel();
            //            int[] selection = table.getSelectedRows();
            //            for (int i = selection.length - 1; i >= 0; i--) {
            //                model.removeRow(table.convertRowIndexToModel(selection[i]));
            //            }

            JDialog dialog = new SetupDialog("title", model.getAveragePrice(table.getSelectedRow()));
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(getRootPane());
            dialog.setVisible(true);
        });
    }

    @Override
    public void show(Component c, int x, int y) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            RowDataModel model = (RowDataModel) table.getModel();
            calc.setEnabled(table.getSelectedRowCount() > 0);
            setTS.setEnabled((table.getSelectedRowCount() == 1) && model.canSetTC(table.getSelectedRow()));
            super.show(c, x, y);
        }
    }
}