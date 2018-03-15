package gui.tablemodel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import cc.bitbank.entity.Order;

public class RowDataModel extends DefaultTableModel {
    private static final long serialVersionUID = -813484403607557100L;
    public static final int COL_INDEX_ORDER_ID = 1;
    public static final int COL_INDEX_AMOUNT = 3;
    public static final int COL_INDEX_STATUS = 5;
    public static final int COL_INDEX_LASTUPD = 6;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
    private static final ColumnContext[] COLUMN_ARRAY = { //
            new ColumnContext("No.", Integer.class, false), //
            new ColumnContext("OrderId", Long.class, false), //
            new ColumnContext("Info", String.class, false), //
            new ColumnContext("Amount", BigDecimal.class, false), //
            new ColumnContext("Price", BigDecimal.class, false), //
            new ColumnContext("Status", String.class, false), //
            new ColumnContext("LastUpd", String.class, false), //
    };
    private int number = 1;

    synchronized public boolean addOrUpdRowData(Order order) {
        boolean updateBalance = false;
        int rowCount = super.getRowCount();
        boolean exists = false;
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
                super.setValueAt(sdf.format(new Date()), index, COL_INDEX_LASTUPD);
                exists = true;
                break;
            }
        }
        if (!exists) {
            Object[] obj = { number, order.orderId, //
                    order.pair + "(" + order.side.name() + ")", //
                    this.getAmount(order), //
                    this.getPrice(order), //
                    order.status, //
                    sdf.format(new Date()) //
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

        protected ColumnContext(String columnName, Class<?> columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
    }
}