package gui.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import cc.Config;
import gui.form.BitBankMainFrame;
import gui.form.SetupDialog;
import gui.tablemodel.RowDataModel;
import mng.TSManager;

public class TablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = -8857893357535730207L;

    private final JMenuItem calc;
    private final JMenuItem setupTs;
    private final JMenu modifyTs;

    public TablePopupMenu() {
        super();
        {
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
                msg += "Amount:" + buyTotalExecutedAmount/*.setScale(Config.me().getRound1(), RoundingMode.HALF_UP)*/.toPlainString();
                msg += "\r\n";
                msg += "AveragePrice:" + buyTotal.divide(buyTotalExecutedAmount, Config.me().getRound1(), RoundingMode.HALF_UP).toPlainString();
                JOptionPane.showMessageDialog(BitBankMainFrame.me(), //
                        msg, //
                        "Profit", JOptionPane.INFORMATION_MESSAGE);
                //            model.addRowData(new RowData("New row", ""));
                //            Rectangle r = table.getCellRect(model.getRowCount() - 1, 0, true);
                //            table.scrollRectToVisible(r);

            });
        }
        addSeparator();
        {
            setupTs = add("Setup TP");
            setupTs.addActionListener(e -> {
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
        {
            modifyTs = new JMenu("Edit TS");
            modifyTs.add(createJMenuItem("0.20"));
            //modifyTs.add(createJMenuItem("0.25"));
            modifyTs.add(createJMenuItem("0.30"));
            //modifyTs.add(createJMenuItem("0.35"));
            modifyTs.add(createJMenuItem("0.40"));
            //modifyTs.add(createJMenuItem("0.45"));
            modifyTs.add(createJMenuItem("0.50"));
            //modifyTs.add(createJMenuItem("0.55"));
            modifyTs.add(createJMenuItem("0.60"));
            //modifyTs.add(createJMenuItem("0.65"));
            modifyTs.add(createJMenuItem("0.70"));
            //modifyTs.add(createJMenuItem("0.75"));
            modifyTs.add(createJMenuItem("0.80"));
            //modifyTs.add(createJMenuItem("0.85"));
            modifyTs.add(createJMenuItem("0.90"));
            //modifyTs.add(createJMenuItem("0.95"));
            modifyTs.add(createJMenuItem("1.00"));
            add(modifyTs);
        }
    }

    @Override
    public void show(Component c, int x, int y) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            RowDataModel model = (RowDataModel) table.getModel();
            calc.setEnabled(table.getSelectedRowCount() > 0);
            setupTs.setEnabled((table.getSelectedRowCount() == 1) && model.canSetTC(table.getSelectedRow()));
            //modifyTs.setEnabled((table.getSelectedRowCount() == 1) && model.canSetTC(table.getSelectedRow()));
            modifyTs.setEnabled(table.getSelectedRowCount() > 0);
            super.show(c, x, y);
        }
    }

    private JMenuItem createJMenuItem(String value) {
        JMenuItem ts1 = new JMenuItem(new ModifyTsAction(new BigDecimal(value)));
        ts1.setText(value);
        return ts1;
    }

    class ModifyTsAction extends AbstractAction {
        private static final long serialVersionUID = -660393676870756452L;

        private final BigDecimal value;

        public ModifyTsAction(BigDecimal value) {
            this.value = value;
        }

        private final BigDecimal defaultLostCut = new BigDecimal(1);

        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) getInvoker();
            RowDataModel model = (RowDataModel) table.getModel();
            for (int rowIndex : table.getSelectedRows()) {
                final long orderId = model.getOrderId(rowIndex);
                final BigDecimal averagePrice = model.getAveragePrice(rowIndex);
                final BigDecimal executedAmount = model.getExecutedAmount(rowIndex);
                TSManager.me().addOrUpdate(orderId, averagePrice, executedAmount, defaultLostCut, this.value);
            }
        }
    }
}