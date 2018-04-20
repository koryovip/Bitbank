package gui.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cc.Config;
import cc.bitbank.entity.Assets;
import cc.bitbank.entity.Assets.Asset;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.Orders;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;
import cc.bitbank.exception.BitbankException;
import gui.TS;
import gui.popup.TablePopupMenu;
import gui.renderer.StripeTableRenderer;
import gui.tablemodel.RowDataModel;
import mng.OrderManager;
import mng.TSManager;
import pubnub.TSMonitor;
import pubnub.TSMonitorUpdater;
import utils.BitbankClient;
import utils.DateUtil;
import utils.OtherUtil;

public class BitBankMainFrame extends JPanel {

    private Logger logger = LogManager.getLogger();

    private static final long serialVersionUID = -8858161812949493525L;
    private static final BitBankMainFrame singleton = new BitBankMainFrame();
    // private static final List<Long> ORDER_HISTORY = new ArrayList<Long>();
    // private static final List<TS> TS_LIST = new ArrayList<TS>();

    static {
        //        TS_LIST.add(new TS(22404937L, new BigDecimal(69.1), new BigDecimal(2541.5631), new BigDecimal(69.1), new BigDecimal(1)));
        //        TS_LIST.add(new TS(22404556L, new BigDecimal(69.1), new BigDecimal(2542), new BigDecimal(69.0), new BigDecimal(1)));
    }

    private final RowDataModel model = new RowDataModel();
    private final JTable table = new JTable(model) {
        private static final long serialVersionUID = 8304794967568437905L;
        /*public int getRowHeight(int row) {
        return 22;
        }*/
    };

    public static BitBankMainFrame me() {
        return singleton;
    }

    JLabel time = new JLabel();
    JLabel jpyBalance = new JLabel();
    JLabel btcBalance = new JLabel();
    JLabel xrpBalance = new JLabel();
    JLabel sellXRP = new JLabel();
    JLabel sellXRPVo = new JLabel();
    JLabel buyXRP = new JLabel();
    JLabel buyXRPVo = new JLabel();

    JLabel sellBTC = new JLabel();
    JLabel stop = new JLabel();
    JLabel profit = new JLabel();
    JLabel profit2 = new JLabel();
    BigDecimal buy = new BigDecimal(-1);
    BigDecimal sell = new BigDecimal(-1);
    BigDecimal stopValue = new BigDecimal(0);
    BigDecimal stopFix = new BigDecimal(3000);
    BigDecimal buyPrice = new BigDecimal(982000L);
    BigDecimal amount = new BigDecimal(0.2037);

    private BitBankMainFrame() {
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new Dimension(1400, 480));

        initGUI();

        initOrderList();

        updateXRPBalance();
        // update(3, 3);

        TSMonitor monitor = new TSMonitor(new TSMonitorUpdater() {
            @Override
            public boolean update(final pubnub.json.ticker.Message hoge) {
                sell = hoge.data.sell;
                buy = hoge.data.buy;

                time.setText(DateUtil.me().format1(hoge.data.timestamp));

                final String sellPrice = hoge.data.sell.toPlainString();
                updateWIndowTitlen(sellPrice);
                sellXRP.setText(sellPrice);
                sellXRPVo.setText(hoge.data.vol.toPlainString()); // TODO from 板情報: depth_btc_jpy

                buyXRP.setText(hoge.data.buy.toPlainString());

                model.updRowData(hoge);

                return false;
            }

            @Override
            public boolean update(final TS ts) {
                model.updRowDataTS(ts);
                return false;
            }
        });
        monitor.monitor();
    }

    private void initGUI() {
        int w1 = 100;
        time.setBounds(10, 10, 300, 20);
        add(time);

        jpyBalance.setBounds(310, 10, 300, 20);
        add(jpyBalance);
        btcBalance.setBounds(310, 40, 300, 20);
        add(btcBalance);
        xrpBalance.setBounds(310, 70, 300, 20);
        add(xrpBalance);

        {
            sellXRP.setBounds(10, 40, w1, 20);
            sellXRP.setHorizontalTextPosition(SwingConstants.RIGHT);
            sellXRP.setForeground(Color.PINK);
            add(sellXRP);
            //////
            sellXRPVo.setBounds(10, 70, w1 * 3, 20);
            sellXRPVo.setHorizontalTextPosition(SwingConstants.RIGHT);
            sellXRPVo.setForeground(Color.WHITE);
            add(sellXRPVo);
        }
        {
            buyXRP.setBounds(10 + w1, 40, w1, 20);
            buyXRP.setHorizontalTextPosition(SwingConstants.RIGHT);
            buyXRP.setForeground(Color.BLUE);
            add(buyXRP);
            ///////
            buyXRPVo.setBounds(10 + w1, 70, w1, 20);
            buyXRPVo.setHorizontalTextPosition(SwingConstants.RIGHT);
            buyXRPVo.setForeground(Color.BLUE);
            add(buyXRPVo);
        }
        sellBTC.setBounds(10, 70, w1, 20);
        sellBTC.setHorizontalTextPosition(SwingConstants.RIGHT);
        add(sellBTC);
        profit.setBounds(10 + w1, 70, w1, 20);
        profit.setHorizontalTextPosition(SwingConstants.RIGHT);
        add(profit);
        profit2.setBounds(10 + w1 + w1, 70, w1, 20);
        profit2.setHorizontalTextPosition(SwingConstants.RIGHT);
        add(profit2);

        stop.setBounds(10, 100, w1, 20);
        stop.setHorizontalTextPosition(SwingConstants.RIGHT);
        add(stop);

        //        JButton btn = new JButton("Sell");
        //        btn.setBounds(10 + w1, 100, w1, 20);
        //        btn.addActionListener(new ActionListener() {
        //            @Override
        //            public void actionPerformed(ActionEvent e) {
        //                try {
        //                    Order order = doSellMARKET(CurrencyPair.BTC_JPY, amount);
        //                    if (order != null && order.orderId != 0) {
        //                        JOptionPane.showMessageDialog(me(), order.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
        //                    } else {
        //                        JOptionPane.showMessageDialog(me(), "order = null", "Error", JOptionPane.ERROR_MESSAGE);
        //                    }
        //                } catch (BitbankException | IOException e1) {
        //                    e1.printStackTrace();
        //                    JOptionPane.showMessageDialog(me(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        //                }
        //            }
        //        });
        //        add(btn);

        int fontSize = 20;
        int tableRowHight = fontSize + 6;
        Font font14 = new Font("MS Gothic", Font.PLAIN, fontSize);
        {
            // JTable table = new JTable(model);
            table.setFont(font14);
            table.getTableHeader().setFont(font14);
            table.setRowHeight(tableRowHight);
            table.setRowMargin(1);
            StripeTableRenderer renderer = new StripeTableRenderer();
            table.setDefaultRenderer(String.class, renderer);
            table.setDefaultRenderer(Integer.class, renderer);
            table.setDefaultRenderer(Long.class, renderer);
            table.setDefaultRenderer(BigDecimal.class, renderer);
            table.setDefaultRenderer(Object.class, renderer);

            //table.setShowGrid(false);
            model.initColumnSize(table.getColumnModel());

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //列の入れ替えを禁止
            table.getTableHeader().setReorderingAllowed(false);
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            table.setComponentPopupMenu(new TablePopupMenu());
            final JScrollPane jScrollPane = new JScrollPane(table);
            jScrollPane.setBounds(10, 130, 1400, 300);
            add(jScrollPane);
        }
        {
            final float buyAmountFixed = 10f;
            JButton btn = new JButton("Buy(" + buyAmountFixed + ")");
            btn.setFont(font14);
            btn.setBounds(10, 440, 150, tableRowHight);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        BigDecimal price = sell;//new BigDecimal("10");
                        BigDecimal amount = new BigDecimal(buyAmountFixed);
                        logger.debug("buy:" + price.toPlainString());
                        final Order order = BitbankClient.me().bbW.sendOrder(Config.me().getPair(), price, amount, OrderSide.BUY, OrderType.LIMIT);
                        if (order == null || order.orderId == 0) {
                            throw new Exception("Order Failed!");
                        }
                        OrderManager.me().add(order.orderId);

                        logger.debug(order);
                        model.addOrderData(order);

                        updateXRPBalance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(me(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // updateXRPBalance();
                    }
                }
            });
            add(btn);
        }
        {
            JButton btn = new JButton("Sell");
            btn.setFont(font14);
            btn.setBounds(200, 440, 100, tableRowHight);
            btn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    int selectedRowIndex = table.getSelectedRow();
                    if (selectedRowIndex < 0) {
                        return;
                    }
                    if (!model.canSell(selectedRowIndex)) {
                        JOptionPane.showMessageDialog(me(), "売り可能な状態じゃない", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    try {
                        BigDecimal price = buy;
                        final long orderIdBought = model.getOrderId(selectedRowIndex);
                        final BigDecimal amount = model.getExecutedAmount(selectedRowIndex);
                        logger.debug("sell:" + price.toPlainString() + ", amount:" + amount.toPlainString());
                        final Order order = BitbankClient.me().bbW.sendOrder(Config.me().getPair(), price, amount, OrderSide.SELL, OrderType.LIMIT);
                        if (order == null || order.orderId == 0) {
                            throw new Exception("Sell Order Failed!");
                        }
                        logger.debug(order);

                        final long orderId = order.orderId;
                        OrderManager.me().add(orderId);
                        model.addOrderData(order);

                        // 選択したオーダーのTSを外す
                        if (TSManager.me().remove(orderIdBought)) {
                            model.resetRowDataTS(orderIdBought);
                        }
                        updateXRPBalance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(me(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // updateXRPBalance();
                    }
                }
            });
            add(btn);
        }
        {
            JButton btn = new JButton("Cancel");
            btn.setFont(font14);
            btn.setBounds(600, 440, 100, tableRowHight);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    int selectedRowIndex = table.getSelectedRow();
                    if (selectedRowIndex < 0) {
                        return;
                    }
                    final long orderId = model.getOrderId(selectedRowIndex);
                    logger.debug(orderId);
                    try {
                        Order order = BitbankClient.me().bbW.cancelOrder(Config.me().getPair(), orderId);
                        if (order == null || order.orderId == 0) {
                            throw new Exception("Cancel Order Failed!");
                        }
                        logger.debug(order);
                        model.updOrderData(order);
                        updateXRPBalance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(me(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            add(btn);
        }
    }

    private void initOrderList() {
        try {
            // 未約定のオーダーを取得
            {
                Map<String, Long> option = new HashMap<String, Long>();
                option.put("count", 10L);
                Orders orders = BitbankClient.me().bbR.getActiveOrders(Config.me().getPair(), option);
                for (Order order : orders.orders) {
                    OrderManager.me().add(order.orderId);
                }
            }
            // オーダー一覧を初期化
            {
                int size = OrderManager.me().size();
                if (size <= 0) {
                    return;
                }
                final Orders orders = BitbankClient.me().bbR.getOrders(Config.me().getPair(), OrderManager.me().getOrderIds());
                for (final Order order : orders.orders) {
                    model.addOrderData(order);
                }
            }
            // オーダー監視
            {
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleWithFixedDelay(() -> {
                    try {
                        updateOrderList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 3, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(me(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderList() {
        List<Long> watchOrderIds = new ArrayList<Long>();
        final int rowCount = model.getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            if (model.watch(rowIndex)) {
                watchOrderIds.add(model.getOrderId(rowIndex));
            }
        }
        if (watchOrderIds.size() <= 0) {
            return;
        }
        logger.debug(watchOrderIds.size());
        try {
            boolean updateBalance = false;
            final Orders orders = BitbankClient.me().bbR.getOrders(Config.me().getPair(), OtherUtil.me().toArray(watchOrderIds));
            for (final Order order : orders.orders) {
                if (model.updOrderData(order)) {
                    updateBalance = true;
                }
            }
            if (updateBalance) {
                updateXRPBalance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(final long delay1, final long delay2) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(() -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, delay1, TimeUnit.SECONDS);
    }

    boolean isUpdatingBalance = false;

    synchronized final private void updateXRPBalance() {
        if (isUpdatingBalance) {
            logger.debug("Balance更新中");
            return;
        }
        try {
            isUpdatingBalance = true;
            //            logger.debug("isUpdatingBalance:" + isUpdatingBalance);
            Assets assets = BitbankClient.me().bbR.getAsset();
            /*
                    asset アセット名
                    amount_precision 小数点の表示精度
                    onhand_amount 保有量
                    locked_amount ロックされている量
                    free_amount 利用可能な量
                    withdrawal_fee 引き出し手数料
             */
            for (Asset asset : assets.assets) {
                // logger.debug(asset);
                if (asset.asset.equals("jpy")) {
                    jpyBalance.setText(String.format("%s/%s", asset.lockedAmount.toPlainString(), asset.freeAmount.toPlainString()));
                } else if (asset.asset.equals("btc")) {
                    btcBalance.setText(String.format("%s/%s", asset.lockedAmount.toPlainString(), asset.freeAmount.toPlainString()));
                } else if (asset.asset.equals("xrp")) {
                    xrpBalance.setText(String.format("%s/%s", asset.lockedAmount.toPlainString(), asset.freeAmount.toPlainString()));
                    // break;
                }
            }
        } catch (BitbankException | IOException e) {
            e.printStackTrace();
        } finally {
            isUpdatingBalance = false;
            //            logger.debug("isUpdatingBalance:" + isUpdatingBalance);
        }
        //        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        //        service.schedule(() -> {
        //        } , 2, TimeUnit.SECONDS);
    }

    public void updateWIndowTitlen(String title) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setTitle(title);
    }

    private Order doSellMARKET(CurrencyPair pair, BigDecimal amount) throws BitbankException, IOException {
        return BitbankClient.me().bbW.sendOrder(pair, BigDecimal.valueOf(0), amount, OrderSide.SELL, OrderType.MARKET);
    }

    final public void addOrder(Order order) {
        model.addOrderData(order);
    }

    final public void updOrder(Order order) {
        model.updOrderData(order);
    }

    final public void resetRowDataTS(Order order) {
        model.resetRowDataTS(order.orderId);
    }
}
