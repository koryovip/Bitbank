package gui.tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cc.bitbank.entity.Order;
import utils.DateUtil;

public class RowDataModel extends DefaultTableModel {
    private static final long serialVersionUID = -813484403607557100L;
    public static final int COL_INDEX_NO = 0;
    public static final int COL_INDEX_ORDER_ID = 1;
    public static final int COL_INDEX_AMOUNT = 3;
    public static final int COL_INDEX_STATUS = 5;
    public static final int COL_INDEX_DIFF = 6;
    public static final int COL_INDEX_PROFIT = 7;
    public static final int COL_INDEX_LASTUPD = 8;

    private static final ColumnContext[] COLUMN_ARRAY = { //
            new ColumnContext("No.", Integer.class, false, 10), //
            new ColumnContext("OrderId", Long.class, false, 50), //
            new ColumnContext("Info", String.class, false, 100), //
            new ColumnContext("Amount", BigDecimal.class, false, 100), //
            new ColumnContext("Price", BigDecimal.class, false, 100), //
            new ColumnContext("Status", String.class, false, 150), //
            new ColumnContext("Diff", BigDecimal.class, false, 100), //buy
            new ColumnContext("Profit", BigDecimal.class, false, 100), //buy
            new ColumnContext("LastUpd", String.class, false, 120), //
    };
    private int number = 1;

    final public void initColumnSize(TableColumnModel tableColumnModel) {
        int index = 0;
        for (ColumnContext context : COLUMN_ARRAY) {
            TableColumn tableColumn = tableColumnModel.getColumn(index++);
            tableColumn.setMinWidth(context.columnWidth);
            if (index == COL_INDEX_NO) {
                tableColumn.setMaxWidth(context.columnWidth);
            }
        }
    }

    synchronized public boolean addOrUpdRowData(final BigDecimal buy, final Order order) {
        boolean updateBalance = false;
        int rowCount = super.getRowCount();
        boolean exists = false;
        final BigDecimal amount = this.getAmount(order);
        final BigDecimal price = this.getPrice(order);
        for (int index = 0; index < rowCount; index++) {
            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
            if (orderId.toString().equals(Long.toString(order.orderId))) {
                // update
                if (!super.getValueAt(index, COL_INDEX_STATUS).toString().equals(order.status)) {
                    // ステータスが変化ありの場合、残高を更新
                    System.out.println("ステータスが変化ありの場合、残高を更新:" + order.status);
                    updateBalance = true;
                }
                super.setValueAt(order.status, index, COL_INDEX_STATUS);
                super.setValueAt(buy.subtract(price).setScale(0, RoundingMode.HALF_UP), index, COL_INDEX_DIFF);
                super.setValueAt(buy.subtract(price).multiply(amount).setScale(0, RoundingMode.HALF_UP), index, COL_INDEX_PROFIT);
                super.setValueAt(DateUtil.me().format1(new Date()), index, COL_INDEX_LASTUPD);
                exists = true;
                break;
            }
        }
        if (!exists) {
            Object[] obj = { number, order.orderId, //
                    order.pair + "(" + order.side.name() + ")", //
                    amount, //
                    price, //
                    order.status, //
                    buy.subtract(price).setScale(0, RoundingMode.HALF_UP), //
                    buy.subtract(price).multiply(amount).setScale(0, RoundingMode.HALF_UP), //
                    DateUtil.me().format1(new Date()) //
            };
            super.addRow(obj);
            updateBalance = true;
            number++;
        }
        return updateBalance;
    }

    public long getOrderId(int rowIndex) {
        return Long.parseLong(super.getValueAt(rowIndex, COL_INDEX_ORDER_ID).toString());
    }

    /**
     * UNFILLED 注文中
     * PARTIALLY_FILLED 注文中(一部約定)
     * FULLY_FILLED 約定済み
     * CANCELED_UNFILLED 取消済
     * CANCELED_PARTIALLY_FILLED 取消済(一部約定)
     * @param rowIndex
     * @return
     */
    public boolean canSell(int rowIndex) {
        String status = super.getValueAt(rowIndex, COL_INDEX_STATUS).toString();
        return "FULLY_FILLED".equals(status) || "PARTIALLY_FILLED".equals(status);
    }

    public BigDecimal getAxecutedAmount(int rowIndex) {
        return (BigDecimal) super.getValueAt(rowIndex, COL_INDEX_AMOUNT);
    }

    synchronized public BigDecimal getAmount(Order order) {
        if (order.remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            return order.remainingAmount;
        }
        return order.executedAmount;
    }

    synchronized public BigDecimal getPrice(Order order) {
        if (order.averagePrice.compareTo(BigDecimal.ZERO) > 0) {
            return order.averagePrice;
        }
        return order.price;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return COLUMN_ARRAY[col].isEditable;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_ARRAY[column].columnClass;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_ARRAY.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_ARRAY[column].columnName;
    }

    private static class ColumnContext {
        public final String columnName;
        public final Class<?> columnClass;
        public final boolean isEditable;
        public final int columnWidth;

        protected ColumnContext(String columnName, Class<?> columnClass, boolean isEditable, int columnWidth) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
            this.columnWidth = columnWidth;
        }
    }
}