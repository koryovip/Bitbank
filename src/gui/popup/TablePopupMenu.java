package gui.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;

import cc.Config;
import gui.form.BitBankMainFrame;
import gui.form.SetupDialog;
import gui.tablemodel.RowDataModel;
import mng.TSManager;
import utils.OtherUtil;
import utils.StringUtilsKR;

public class TablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = -8857893357535730207L;

    private final JMenuItem calc;
    private final JMenuItem setupTs;
    private final JMenu modifyTs; // Trailing Stop
    private final JMenu modifyLc; // lost cut

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
        addSeparator();
        {
            ButtonGroup group = new ButtonGroup();
            modifyTs = new JMenu("Change TralingStop");
            group.add(modifyTs.add(this.createJMenuItemTs("none", BigDecimal.ZERO)));
            modifyTs.addSeparator();
            group.add(modifyTs.add(this.createJMenuItemTs("0.2")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.3")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.4")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.5")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.6")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.7")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.8")));
            group.add(modifyTs.add(this.createJMenuItemTs("0.9")));
            group.add(modifyTs.add(this.createJMenuItemTs("1.0")));
            modifyTs.addSeparator();
            modifyTs.add(this.createJMenuItem2Ts("custom"));
            add(modifyTs);
        }
        addSeparator();
        {
            ButtonGroup group = new ButtonGroup();
            modifyLc = new JMenu("Change LossCut");
            group.add(modifyLc.add(this.createJMenuItemLc("none", BigDecimal.ZERO)));
            modifyLc.addSeparator();
            modifyLc.add(this.createJMenuItem2Lc("custom"));
            modifyLc.addSeparator();
            group.add(modifyLc.add(this.createJMenuItemLc("99%", new BigDecimal("99"))));
            group.add(modifyLc.add(this.createJMenuItemLc("98%", new BigDecimal("98"))));
            group.add(modifyLc.add(this.createJMenuItemLc("97%", new BigDecimal("97"))));
            group.add(modifyLc.add(this.createJMenuItemLc("96%", new BigDecimal("96"))));
            group.add(modifyLc.add(this.createJMenuItemLc("95%", new BigDecimal("95"))));
            group.add(modifyLc.add(this.createJMenuItemLc("94%", new BigDecimal("94"))));
            group.add(modifyLc.add(this.createJMenuItemLc("93%", new BigDecimal("93"))));
            group.add(modifyLc.add(this.createJMenuItemLc("92%", new BigDecimal("92"))));
            group.add(modifyLc.add(this.createJMenuItemLc("91%", new BigDecimal("91"))));
            group.add(modifyLc.add(this.createJMenuItemLc("90%", new BigDecimal("90"))));
            add(modifyLc);
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
            modifyLc.setEnabled(table.getSelectedRowCount() > 0);
            super.show(c, x, y);
        }
    }

    private JMenuItem createJMenuItemTs(String value) {
        return createJMenuItemTs(value, new BigDecimal(value));
    }

    private JMenuItem createJMenuItemTs(String text, BigDecimal value) {
        JMenuItem ts1 = new JRadioButtonMenuItem(new ModifyTsAction(value));
        ts1.setText(text);
        return ts1;
    }

    private JMenuItem createJMenuItem2Ts(String text) {
        JMenuItem ts1 = new JMenuItem(new AbstractAction() {
            private static final long serialVersionUID = 1516788165956125322L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String input = JOptionPane.showInputDialog(BitBankMainFrame.me(), "TralingStopの値を入力してください：", "");
                if (StringUtilsKR.me().isStrBlank(input, true)) {
                    return;
                }
                final BigDecimal value;
                try {
                    value = new BigDecimal(input);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BitBankMainFrame.me(), input, "値不正", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                new ModifyTsAction(value).actionPerformed(event);
            }
        });
        ts1.setText(text);
        return ts1;
    }

    public class ModifyTsAction extends AbstractAction {
        private static final long serialVersionUID = -660393676870756452L;

        private final BigDecimal value;

        public ModifyTsAction(BigDecimal value) {
            this.value = value;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) getInvoker();
            RowDataModel model = (RowDataModel) table.getModel();
            for (int rowIndex : table.getSelectedRows()) {
                if (!model.canSell(rowIndex)) {
                    continue;
                }
                final long orderId = model.getOrderId(rowIndex);
                final BigDecimal averagePrice = model.getAveragePrice(rowIndex);
                final BigDecimal executedAmount = model.getExecutedAmount(rowIndex);
                TSManager.me().addOrUpdateTs(orderId, averagePrice, executedAmount, this.value);
                /*
                if (this.value.compareTo(BigDecimal.ZERO) <= 0) {
                    model.resetRowDataTS(orderId);
                }
                */
            }
        }
    }

    //    private JMenuItem createJMenuItemLc(String value) {
    //        return createJMenuItemLc(value, new BigDecimal(value));
    //    }

    private JMenuItem createJMenuItemLc(String text, BigDecimal value) {
        JMenuItem ts1 = new JRadioButtonMenuItem(new ModifyLcAction(value, true));
        ts1.setText(text);
        return ts1;
    }

    private JMenuItem createJMenuItem2Lc(String text) {
        JMenuItem ts1 = new JMenuItem(new AbstractAction() {
            private static final long serialVersionUID = 1516788165956125322L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String input = JOptionPane.showInputDialog(BitBankMainFrame.me(), "LossCutの値を入力してください：", "");
                if (StringUtilsKR.me().isStrBlank(input, true)) {
                    return;
                }
                final BigDecimal value;
                try {
                    value = new BigDecimal(input);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BitBankMainFrame.me(), input, "値不正", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                new ModifyLcAction(value, false).actionPerformed(event);
            }
        });
        ts1.setText(text);
        return ts1;
    }

    public class ModifyLcAction extends AbstractAction {

        private static final long serialVersionUID = -4855080588158297797L;
        private final BigDecimal value;
        private final boolean present;

        public ModifyLcAction(BigDecimal value, boolean present) {
            this.present = present;
            this.value = value;//OtherUtil.me().scale(value, Config.me().getRoundCurrencyPair());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) getInvoker();
            RowDataModel model = (RowDataModel) table.getModel();
            for (int rowIndex : table.getSelectedRows()) {
                if (!model.canSell(rowIndex)) {
                    continue;
                }
                final long orderId = model.getOrderId(rowIndex);
                final BigDecimal averagePrice = model.getAveragePrice(rowIndex);
                final BigDecimal executedAmount = model.getExecutedAmount(rowIndex);
                BigDecimal price = this.present ? //
                        OtherUtil.me().scale(OtherUtil.me().persent2(averagePrice, this.value), Config.me().getRoundCurrencyPair()) // %計算。平均取得価格の99％
                        : OtherUtil.me().scale(this.value, Config.me().getRoundCurrencyPair());
                TSManager.me().addOrUpdateLc(orderId, averagePrice, executedAmount, price);
            }
        }
    }
}