package gui.tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cc.Config;
import cc.bitbank.entity.Order;
import gui.TS;
import utils.DateUtil;

public class RowDataModel extends DefaultTableModel {
    private static final long serialVersionUID = -813484403607557100L;
    public static final int COL_INDEX_NO = 0;
    public static final int COL_INDEX_ORDER_ID = 1;
    public static final int COL_INDEX_PAIR = 2;
    public static final int COL_INDEX_SIDE = 3;
    public static final int COL_INDEX_AMOUNT = 4;
    public static final int COL_INDEX_PRICE = 5;
    public static final int COL_INDEX_STATUS = 6;
    public static final int COL_INDEX_DIFF = 7;
    public static final int COL_INDEX_PROFIT = 8;
    public static final int COL_INDEX_DATE = 9;
    public static final int COL_INDEX_LOSTCUT = 10;
    public static final int COL_INDEX_TRALINGSTOP = 11;
    public static final int COL_INDEX_LASTUPD = 12;

    private static final ColumnContext[] COLUMN_ARRAY = { //
            new ColumnContext("No.", Integer.class, false, 10), //
            new ColumnContext("OrderId", Long.class, false, 50), //
            new ColumnContext("Pair", String.class, false, 80), //
            new ColumnContext("Side", String.class, false, 40), //
            new ColumnContext("Amount", BigDecimal.class, false, 100), //
            new ColumnContext("Price", BigDecimal.class, false, 100), //
            new ColumnContext("Status", String.class, false, 150), //
            new ColumnContext("Buy-Price", BigDecimal.class, false, 100), //buy
            new ColumnContext("Profit", BigDecimal.class, false, 100), //buy
            new ColumnContext("Date", String.class, false, 120), //
            new ColumnContext("LostCut", BigDecimal.class, false, 100), //
            new ColumnContext("TralingStop", BigDecimal.class, false, 100), //
            new ColumnContext("LastUpd", String.class, false, 120), //
    };
    private int number = 1;

    final public void initColumnSize(TableColumnModel tableColumnModel) {
        int index = 0;
        for (ColumnContext context : COLUMN_ARRAY) {
            TableColumn tableColumn = tableColumnModel.getColumn(index++);
            tableColumn.setMinWidth(context.columnWidth);
            if (index == COL_INDEX_NO) {
                tableColumn.setWidth(context.columnWidth);
            }
        }
    }

    private final int ROUND = Config.me().getRound1();

    synchronized public boolean addOrUpdRowData(final BigDecimal buy, final Order order) {
        boolean updateBalance = false;
        boolean exists = false;
        final BigDecimal amount = this.getAmount(order);
        final BigDecimal price = this.getPrice(order);
        int rowCount = super.getRowCount();
        BigDecimal diff = buy.subtract(price).setScale(ROUND, RoundingMode.HALF_UP);
        BigDecimal profit = buy.subtract(price).multiply(amount).setScale(ROUND, RoundingMode.HALF_UP);
        final String orderDate = DateUtil.me().format2(this.getOrderDate(order));
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
                if (this.canSell(index)) {
                    super.setValueAt(diff, index, COL_INDEX_DIFF);
                    super.setValueAt(profit, index, COL_INDEX_PROFIT);
                }
                super.setValueAt(orderDate, index, COL_INDEX_DATE);
                super.setValueAt(DateUtil.me().format2(new Date()), index, COL_INDEX_LASTUPD);
                exists = true;
                break;
            }
        }
        if (!exists) {
            Object[] obj = { number, //
                    order.orderId, //
                    order.pair, //
                    order.side, //
                    amount, //
                    price, //
                    order.status, //
                    canSell(order) ? diff : BigDecimal.ZERO, //
                    canSell(order) ? profit : BigDecimal.ZERO, //
                    orderDate, //
                    BigDecimal.ZERO, // lostcut
                    BigDecimal.ZERO, //
                    DateUtil.me().format2(new Date()) //
            };
            super.addRow(obj);
            updateBalance = true;
            number++;
        }
        return updateBalance;
    }

    synchronized public void updRowDataTS(TS ts) {
        int rowCount = super.getRowCount();
        for (int index = 0; index < rowCount; index++) {
            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
            if (!orderId.toString().equals(Long.toString(ts.orderId))) {
                continue;
            }
            super.setValueAt(ts.lostCut.setScale(ROUND, RoundingMode.HALF_UP), index, COL_INDEX_LOSTCUT);
            super.setValueAt(ts.getSellPrice().setScale(ROUND, RoundingMode.HALF_UP), index, COL_INDEX_TRALINGSTOP);
        }
    }

    public Date getOrderDate(Order order) {
        if ("CANCELED_UNFILLED".equals(order.status) || "CANCELED_PARTIALLY_FILLED".equals(order.status)) {
            return order.canceledAt;
        } else if ("UNFILLED".equals(order.status)) {
            return order.orderedAt;
        }
        return order.executedAt;
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
    final public boolean canSell(int rowIndex) {
        String status = super.getValueAt(rowIndex, COL_INDEX_STATUS).toString();
        return canSell(status);
    }

    final public boolean canSell(Order order) {
        return canSell(order.status);
    }

    final private boolean canSell(String status) {
        return "FULLY_FILLED".equals(status) || "PARTIALLY_FILLED".equals(status);
    }

    final public boolean canSetTC(int rowIndex) {
        String status = super.getValueAt(rowIndex, COL_INDEX_STATUS).toString();
        return "FULLY_FILLED".equals(status) || "PARTIALLY_FILLED".equals(status);
    }

    //    final public BigDecimal getBought(int rowIndex) {
    //        return (BigDecimal) super.getValueAt(rowIndex, COL_INDEX_PRICE);
    //    }

    final public BigDecimal getExecutedAmount(int rowIndex) {
        return (BigDecimal) super.getValueAt(rowIndex, COL_INDEX_AMOUNT);
    }

    final public BigDecimal getAveragePrice(int rowIndex) {
        return (BigDecimal) super.getValueAt(rowIndex, COL_INDEX_PRICE);
    }

    /*synchronized*/ public BigDecimal getAmount(Order order) {
        if ("UNFILLED".equals(order.status) || "CANCELED_UNFILLED".equals(order.status)) {
            return order.startAmount;
        } else if ("FULLY_FILLED".equals(order.status)) {
            return order.executedAmount;
        }
        return order.remainingAmount;
    }

    /*synchronized*/ public BigDecimal getPrice(Order order) {
        if ("UNFILLED".equals(order.status) || "CANCELED_UNFILLED".equals(order.status)) {
            return order.price;
        } /*else if ("FULLY_FILLED".equals(order.status)) {
            return order.executedAmount;
          }*/
        return order.averagePrice;
    }

    final public boolean isBuy(int row) {
        return "BUY".equals(getValueAt(row, COL_INDEX_SIDE).toString());
    }

    final public boolean isSell(int row) {
        return "SELL".equals(getValueAt(row, COL_INDEX_SIDE).toString());
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