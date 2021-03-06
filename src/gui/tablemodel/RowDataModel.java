package gui.tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import cc.Config;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.OrderSide;
import gui.TS;
import utils.DateUtil;
import utils.OtherUtil;

public class RowDataModel extends KRTableModel {
    private static final long serialVersionUID = -813484403607557100L;
    private static int __COL_INDEX__ = 0;
    public static final int COL_INDEX_NO = __COL_INDEX__++;
    public static final int COL_INDEX_ORDER_ID = __COL_INDEX__++;
    public static final int COL_INDEX_PAIR = __COL_INDEX__++;
    public static final int COL_INDEX_SIDE = __COL_INDEX__++;
    public static final int COL_INDEX_EXECD_AMOUNT = __COL_INDEX__++;
    public static final int COL_INDEX_AMOUNT = __COL_INDEX__++;
    public static final int COL_INDEX_PRICE = __COL_INDEX__++;
    public static final int COL_INDEX_STATUS = __COL_INDEX__++;
    public static final int COL_INDEX_DATE = __COL_INDEX__++;
    public static final int COL_INDEX_DIFF = __COL_INDEX__++; // 売値―買った値
    public static final int COL_INDEX_PROFIT_NORMAL = __COL_INDEX__++; // 裁量Profit
    public static final int COL_INDEX_PROFIT_BY_TS = __COL_INDEX__++; // TSでExit場合の利益
    public static final int COL_INDEX_TRALINGSTOP = __COL_INDEX__++; // TS設定値（距離）
    public static final int COL_INDEX_TRALINGSTOP_PRICE = __COL_INDEX__++; // TSで今の売値
    public static final int COL_INDEX_LOSSCUT_PRICE = __COL_INDEX__++; // LC設定値(売値)
    public static final int COL_INDEX_PROFIT_BY_LC = __COL_INDEX__++; // LCでExit場合の利益
    public static final int COL_INDEX_LASTUPD = __COL_INDEX__++;

    public RowDataModel() {
        super(new ColumnContext[] { //
                new ColumnContext("No", Integer.class, false, 30), //
                new ColumnContext("OrderId", Long.class, false, 100), //
                new ColumnContext("Pair", String.class, false, 80), //
                new ColumnContext("Side", String.class, false, 50), //
                new ColumnContext("Amount Executed", BigDecimal.class, false, 100), //
                new ColumnContext("Amount", BigDecimal.class, false, 100), //
                new ColumnContext("Price", BigDecimal.class, false, 80), //
                new ColumnContext("Status", String.class, false, 120), //
                new ColumnContext("Order Date", String.class, false, 130), // order date
                new ColumnContext("Buy-Price", BigDecimal.class, false, 90), // 距離
                new ColumnContext("裁量Profit", BigDecimal.class, false, 100), // COL_INDEX_PROFIT_NORMAL
                new ColumnContext("T/SProfit", BigDecimal.class, false, 100), // COL_INDEX_PROFIT_BY_TS
                new ColumnContext("TStop", BigDecimal.class, false, 60), // COL_INDEX_TRALINGSTOP
                new ColumnContext("T/SPrice", BigDecimal.class, false, 90), // COL_INDEX_TRALINGSTOP_PRICE
                new ColumnContext("L/CPrice", BigDecimal.class, false, 90), // COL_INDEX_LOSSCUT_PRICE
                new ColumnContext("L/CProfit", BigDecimal.class, false, 100), // COL_INDEX_PROFIT_BY_LC
                new ColumnContext("LastUpd", String.class, false, 130), //
        });
    }

    private int number = 1;

    private final int ROUND = Config.me().getRound1();

    synchronized public void addOrderData(final Order order) {
        Object[] obj = { number, // rownum
                order.orderId, // orderid
                order.pair, // pair
                order.side, // side (buy, sell)
                // ↑ オーダー新規時固定。
                // ↓ オーダー発行～約定の間更新(一部約定の可能性があるため)
                OtherUtil.me().scale(order.executedAmount, Config.me().getRoundCurrencyPairAmount()), //
                OtherUtil.me().scale(this.getAmount(order), Config.me().getRoundCurrencyPairAmount()), //
                this.getPrice(order), //
                order.status, //
                DateUtil.me().format2(this.getOrderDate(order)), //
                // ↓ realtime 更新
                BigDecimal.ZERO, // diff 現在の買い注文の最高値 - 買値
                BigDecimal.ZERO, // profit (現在の買い注文の最高値 - 買値) × 約定数量 COL_INDEX_PROFIT_NORMAL
                // ↓ Trailing Stop 設定した場合、realtime 更新
                BigDecimal.ZERO, // COL_INDEX_PROFIT_BY_TS
                BigDecimal.ZERO, // COL_INDEX_PROFIT_BY_LC
                BigDecimal.ZERO, // COL_INDEX_TRALINGSTOP
                BigDecimal.ZERO, // COL_INDEX_TRALINGSTOP_PRICE
                BigDecimal.ZERO, // COL_INDEX_LOSSCUT_PRICE
                // case by case
                DateUtil.me().format2(new Date()) // last update time
        };
        super.addRow(obj);
        number++;
    }

    /**
     * オーダー発行/キャンセル～約定の間
     * @param order
     */
    synchronized public boolean updOrderData(final Order order) {
        boolean updateBalance = false;
        final int rowCount = super.getRowCount();
        for (int index = 0; index < rowCount; index++) {
            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
            if (!orderId.toString().equals(Long.toString(order.orderId))) {
                continue;
            }
            if (!super.getValueAt(index, COL_INDEX_STATUS).toString().equals(order.status)) {
                updateBalance = true;
            }
            // 固定のはずだが、一応更新
            super.setValueAt(order.orderId, index, COL_INDEX_ORDER_ID); //
            super.setValueAt(order.pair, index, COL_INDEX_PAIR); //
            super.setValueAt(order.side, index, COL_INDEX_SIDE); //
            // リアルに変わる
            super.setValueAt(OtherUtil.me().scale(order.executedAmount, Config.me().getRoundCurrencyPairAmount()), index, COL_INDEX_EXECD_AMOUNT); // 約定した数量
            super.setValueAt(this.getPrice(order), index, COL_INDEX_PRICE); // 平均取得価格（成行の場合）
            super.setValueAt(order.status, index, COL_INDEX_STATUS);
            super.setValueAt(DateUtil.me().format2(this.getOrderDate(order)), index, COL_INDEX_DATE);
            super.setValueAt(DateUtil.me().format2(new Date()), index, COL_INDEX_LASTUPD);
            break;
        }
        return updateBalance;
    }

    //    synchronized public boolean addOrUpdRowData(final Order order) {
    //        boolean updateBalance = false;
    //        boolean exists = false;
    //        final BigDecimal amount = this.getAmount(order);
    //        final BigDecimal price = this.getPrice(order);
    //        final int rowCount = super.getRowCount();
    //        //final BigDecimal diff = buy.subtract(price).setScale(ROUND, RoundingMode.HALF_UP);
    //        //final BigDecimal profit = buy.subtract(price).multiply(amount).setScale(ROUND, RoundingMode.HALF_UP);
    //        // final BigDecimal ifSell = buy.multiply(amount).divide(buy.subtract(price.subtract(buy)), ROUND, RoundingMode.HALF_UP);
    //        final String orderDate = DateUtil.me().format2(this.getOrderDate(order));
    //        for (int index = 0; index < rowCount; index++) {
    //            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
    //            if (!orderId.toString().equals(Long.toString(order.orderId))) {
    //                continue;
    //            }
    //            exists = true;
    //            // update
    //            if (!super.getValueAt(index, COL_INDEX_STATUS).toString().equals(order.status)) {
    //                // ステータスが変化ありの場合、残高を更新
    //                System.out.println("ステータスが変化ありの場合、残高を更新:" + order.status);
    //                updateBalance = true;
    //            }
    //            super.setValueAt(order.status, index, COL_INDEX_STATUS);
    //            {
    //                // -で初期化
    //                super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_DIFF);
    //                super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_PROFIT);
    //            }
    //            super.setValueAt(orderDate, index, COL_INDEX_DATE);
    //            // super.setValueAt(ifSell, index, COL_INDEX_IFSELL);
    //            super.setValueAt(DateUtil.me().format2(new Date()), index, COL_INDEX_LASTUPD);
    //            break;
    //        }
    //        if (!exists) {
    //            Object[] obj = { number, // rownum
    //                    order.orderId, // orderid
    //                    order.pair, // pair
    //                    order.side, // side (buy, sell)
    //                    amount, //
    //                    price, //
    //                    order.status, //
    //                    BigDecimal.ZERO, // diff
    //                    BigDecimal.ZERO, // profit
    //                    orderDate, //
    //                    // ifSell, // ifsell
    //                    BigDecimal.ZERO, // lostcut
    //                    BigDecimal.ZERO, // tp
    //                    DateUtil.me().format2(new Date()) // last update time
    //            };
    //            super.addRow(obj);
    //            updateBalance = true;
    //            number++;
    //        }
    //        return updateBalance;
    //    }

    synchronized public void updRowData(final pubnub.json.ticker.Message hoge) {
        final int rowCount = super.getRowCount();
        for (int index = 0; index < rowCount; index++) {
            if (!this.canSell(index)) {
                continue;
            }
            final BigDecimal price = getAveragePrice(index);
            final BigDecimal amount = getExecutedAmount(index);
            final BigDecimal diff = hoge.data.buy.subtract(price).setScale(ROUND, RoundingMode.HALF_UP);
            final BigDecimal profit = hoge.data.buy.subtract(price).multiply(amount).setScale(ROUND, RoundingMode.HALF_UP);

            super.setValueAt(diff, index, COL_INDEX_DIFF);
            super.setValueAt(profit, index, COL_INDEX_PROFIT_NORMAL);

            super.setValueAt(DateUtil.me().format2(hoge.data.timestamp), index, COL_INDEX_LASTUPD);
        }
    }

    synchronized public void updRowDataTS(final TS ts) {
        final int rowCount = super.getRowCount();
        final int round = Config.me().getRoundCurrencyPair();
        for (int index = 0; index < rowCount; index++) {
            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
            if (ts.orderId != Long.valueOf(orderId.toString())) {
                continue;
            }

            if (ts.isVictory()) {
                super.setValueAt(OtherUtil.me().scale(ts.profitTS(), 1), index, COL_INDEX_PROFIT_BY_TS);
            } else {
                super.setValueAt(ts.getDistance(), index, COL_INDEX_PROFIT_BY_TS);
            }

            super.setValueAt(OtherUtil.me().scale(ts.profitLC(), 1), index, COL_INDEX_PROFIT_BY_LC);

            super.setValueAt(ts.tralingStop, index, COL_INDEX_TRALINGSTOP);
            super.setValueAt(ts.getSellPrice(), index, COL_INDEX_TRALINGSTOP_PRICE);

            super.setValueAt(ts.lossCut.setScale(round, RoundingMode.HALF_UP), index, COL_INDEX_LOSSCUT_PRICE);
            /*
            if (ts.onSelling()) {
                super.setValueAt("On Selling", index, COL_INDEX_LOSSCUT_PRICE);
            } else {
            }*/

            break;
        }
    }

    synchronized public void resetRowDataTS(final long orderIdTS) {
        final int rowCount = super.getRowCount();
        for (int index = 0; index < rowCount; index++) {
            Object orderId = super.getValueAt(index, COL_INDEX_ORDER_ID);
            if (orderIdTS != Long.valueOf(orderId.toString())) {
                continue;
            }
            super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_PROFIT_BY_TS);
            super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_PROFIT_BY_LC);
            super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_TRALINGSTOP);
            super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_TRALINGSTOP_PRICE);
            super.setValueAt(BigDecimal.ZERO, index, COL_INDEX_LOSSCUT_PRICE);

            break;
        }
    }

    synchronized public final void clear() {
        this.number = 1;
        super._clear();
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
        OrderSide side = (OrderSide) super.getValueAt(rowIndex, COL_INDEX_SIDE);
        String status = super.getValueAt(rowIndex, COL_INDEX_STATUS).toString();
        return canSell(side, status);
    }

    final public boolean canSell(Order order) {
        return canSell(order.side, order.status);
    }

    final private boolean canSell(OrderSide side, String status) {
        return side == OrderSide.BUY //
                && ("FULLY_FILLED".equals(status) || "PARTIALLY_FILLED".equals(status));
    }

    final public boolean watch(int rowIndex) {
        String status = super.getValueAt(rowIndex, COL_INDEX_STATUS).toString();
        return watch(status);
    }

    final public boolean watch(String status) {
        return "UNFILLED".equals(status) || "PARTIALLY_FILLED".equals(status);
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

}