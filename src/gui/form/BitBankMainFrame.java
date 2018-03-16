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

import cc.Config;
import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Assets;
import cc.bitbank.entity.Assets.Asset;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.Orders;
import cc.bitbank.entity.Ticker;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;
import cc.bitbank.exception.BitbankException;
import gui.popup.TablePopupMenu;
import gui.renderer.StripeTableRenderer;
import gui.tablemodel.RowDataModel;
import utils.DateUtil;

public class BitBankMainFrame extends JPanel {
    private static final long serialVersionUID = -8858161812949493525L;
    private static final BitBankMainFrame singleton = new BitBankMainFrame();
    private static final CurrencyPair PAIR = CurrencyPair.BTC_JPY;
    private static final List<Long> ORDER_HISTORY = new ArrayList<Long>();

    static {
        //        ORDER_HISTORY.add(21631360L);
        //        ORDER_HISTORY.add(21631793L);
        //        ORDER_HISTORY.add(21633650L);
        //        ORDER_HISTORY.add(21638696L);
        //        ORDER_HISTORY.add(21675036L);
        //        ORDER_HISTORY.add(21683589L);
        //        ORDER_HISTORY.add(21684658L);
        //        ORDER_HISTORY.add(21687237L);
        //        ORDER_HISTORY.add(21725789L);
        //        ORDER_HISTORY.add(21791026L);
        // ORDER_HISTORY.add(21789874L);
        ORDER_HISTORY.add(368628374L);
        ORDER_HISTORY.add(368693910L);
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
    JLabel xrpBalance = new JLabel();
    JLabel sellXRP = new JLabel();
    JLabel buyXRP = new JLabel();
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
    final Bitbankcc bb = new Bitbankcc();

    private BitBankMainFrame() {

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new Dimension(800, 480));

        init();

        bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());
        updateXRPBalance();

        update(3);
    }

    private void init() {
        int w1 = 100;
        time.setBounds(10, 10, 300, 20);
        add(time);

        jpyBalance.setBounds(310, 10, 300, 20);
        add(jpyBalance);
        xrpBalance.setBounds(310, 10, 300, 20);
        add(xrpBalance);

        sellXRP.setBounds(10, 40, w1, 20);
        sellXRP.setHorizontalTextPosition(SwingConstants.RIGHT);
        sellXRP.setForeground(Color.PINK);
        add(sellXRP);

        buyXRP.setBounds(10 + w1, 40, w1, 20);
        buyXRP.setHorizontalTextPosition(SwingConstants.RIGHT);
        buyXRP.setForeground(Color.BLUE);
        add(buyXRP);

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

        Font font14 = new Font("MS Gothic", Font.PLAIN, 16);
        {
            // JTable table = new JTable(model);
            table.setFont(font14);
            table.getTableHeader().setFont(font14);
            table.setRowHeight(24);

            StripeTableRenderer renderer = new StripeTableRenderer();
            table.setDefaultRenderer(Object.class, renderer);
            table.setDefaultRenderer(Integer.class, renderer);
            table.setDefaultRenderer(Long.class, renderer);
            table.setDefaultRenderer(BigDecimal.class, renderer);

            //table.setShowGrid(false);
            model.initColumnSize(table.getColumnModel());

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            table.setComponentPopupMenu(new TablePopupMenu());
            final JScrollPane jScrollPane = new JScrollPane(table);
            jScrollPane.setBounds(10, 130, 900, 300);
            add(jScrollPane);
        }
        {
            JButton btn = new JButton("Buy");
            btn.setFont(font14);
            btn.setBounds(10, 440, 100, 20);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        BigDecimal price = sell;
                        BigDecimal amount = new BigDecimal(1);
                        System.out.println("buy:" + price.toPlainString());
                        Order order = bb.sendOrder(PAIR, price, amount, OrderSide.BUY, OrderType.LIMIT);
                        if (order != null && order.orderId != 0) {
                            // JOptionPane.showMessageDialog(me(), order.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println(order);
                            if (model.addOrUpdRowData(buy, order)) {
                                updateXRPBalance();
                            }
                            updateOrderId(order.orderId);
                        } else {
                            JOptionPane.showMessageDialog(me(), "order = null", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (BitbankException | IOException e) {
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
            btn.setBounds(160, 440, 100, 20);
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
                        BigDecimal amount = model.getExecutedAmount(selectedRowIndex);
                        System.out.println("sell:" + price.toPlainString() + ", amount:" + amount.toPlainString());
                        Order order = bb.sendOrder(PAIR, price, amount, OrderSide.SELL, OrderType.LIMIT);
                        if (order != null && order.orderId != 0) {
                            // JOptionPane.showMessageDialog(me(), order.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println(order);
                            if (model.addOrUpdRowData(buy, order)) {
                                updateXRPBalance();
                            }
                            updateOrderId(order.orderId);
                        } else {
                            JOptionPane.showMessageDialog(me(), "order = null", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (BitbankException | IOException e) {
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
            btn.setBounds(600, 440, 100, 20);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    int selectedRowIndex = table.getSelectedRow();
                    if (selectedRowIndex < 0) {
                        return;
                    }
                    final long orderId = model.getOrderId(selectedRowIndex);
                    System.out.println(orderId);
                    try {
                        Order order = bb.cancelOrder(PAIR, orderId);
                        if (order != null && order.orderId != 0) {
                            // JOptionPane.showMessageDialog(me(), order.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println(order);
                            if (model.addOrUpdRowData(buy, order)) {
                                updateXRPBalance();
                            }
                        } else {
                            JOptionPane.showMessageDialog(me(), "order = null", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (BitbankException | IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(me(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            add(btn);
        }

    }

    private void update(final long delay) {
        //        {
        //            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        //            service.scheduleWithFixedDelay(() -> {
        //                try {
        //                    Ticker ticker = bb.getTicker(CurrencyPair.BTC_JPY);
        //                    sell = ticker.sell;
        //                    buy = ticker.buy;
        //                    sellBTC.setText(ticker.sell.toPlainString());
        //                    profit.setText(ticker.sell.subtract(buyPrice).toPlainString());
        //                    profit2.setText(ticker.sell.subtract(buyPrice).multiply(amount).setScale(0, RoundingMode.HALF_UP).toPlainString());
        //                    if (ticker.sell.compareTo(buyPrice) <= 0) {
        //                        sellBTC.setForeground(Color.RED);
        //                    }
        //                    BigDecimal diff = ticker.sell.subtract(stopFix);
        //                    if (diff.compareTo(stopValue) > 0) {
        //                        stopValue = diff;
        //                        stop.setText(stopValue.toPlainString());
        //                        stop.setForeground(Color.BLUE);
        //                    }
        //                    if (ticker.sell.compareTo(stopValue) <= 0) {
        //                        // JOptionPane.showMessageDialog(me(), "traling stop!", "Sell", JOptionPane.INFORMATION_MESSAGE);
        //                        stop.setForeground(Color.RED);
        //                    }
        //                } catch (BitbankException | IOException e) {
        //                    e.printStackTrace();
        //                }
        //            }, 0, delay, TimeUnit.SECONDS);
        //        }
        {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(() -> {
                try {
                    Ticker ticker = bb.getTicker(PAIR);
                    sell = ticker.sell;
                    buy = ticker.buy;
                    time.setText(DateUtil.me().format1(ticker.timestamp));

                    final String sellPrice = ticker.sell.toPlainString();
                    sellXRP.setText(sellPrice);

                    buyXRP.setText(ticker.buy.toPlainString());

                    updateWIndowTitlen(sellPrice);
                } catch (BitbankException | IOException e) {
                    e.printStackTrace();
                }
            }, 0, delay, TimeUnit.SECONDS);
        }
        {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(() -> {
                try {
                    Map<String, Long> option = new HashMap<String, Long>();
                    option.put("count", 10L);
                    Orders orders = bb.getActiveOrders(PAIR, option);
                    boolean updateXRPBalance = false;
                    for (Order order : orders.orders) {
                        // System.out.println(order);
                        if (model.addOrUpdRowData(buy, order)) {
                            updateXRPBalance = true;
                        }
                        updateOrderId(order.orderId);
                    }
                    if (updateXRPBalance) {
                        updateXRPBalance();
                    }
                } catch (BitbankException | IOException e) {
                    e.printStackTrace();
                }
            }, 0, delay, TimeUnit.SECONDS);
        }
        {

            // long[] orderIds = new long[] { 21631360, 21631793, 21633650 };
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(() -> {
                try {
                    long[] orderIds = new long[ORDER_HISTORY.size()];
                    for (int ii = 0; ii < orderIds.length; ii++) {
                        orderIds[ii] = ORDER_HISTORY.get(ii);
                    }
                    Orders orders = bb.getOrders(PAIR, orderIds);
                    for (Order order : orders.orders) {
                        // System.out.println(order);
                        model.addOrUpdRowData(buy, order);
                    }
                } catch (BitbankException | IOException e) {
                    e.printStackTrace();
                }
            }, 0, delay, TimeUnit.SECONDS);

        }
    }

    private void updateOrderId(long orderId) {
        if (!ORDER_HISTORY.contains(orderId)) {
            ORDER_HISTORY.add(orderId);
        }
    }

    final public void updateXRPBalance() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            try {
                Assets assets = bb.getAsset();
                for (Asset asset : assets.assets) {
                    // System.out.println(asset);
                    if (asset.asset.equals("jpy") || asset.asset.equals("btc")) {
                        System.out.println(String.format("%s/%s", asset.lockedAmount, asset.freeAmount));
                    }
                    if (asset.asset.equals("xrp")) {
                        /*
                         * asset アセット名
                        amount_precision 小数点の表示精度
                        onhand_amount 保有量
                        locked_amount ロックされている量
                        free_amount 利用可能な量
                        withdrawal_fee 引き出し手数料
                         */
                        xrpBalance.setText(String.format("%s/%s", asset.lockedAmount, asset.freeAmount));
                        break;
                    }
                }
            } catch (BitbankException | IOException e) {
                e.printStackTrace();
            }
        }, 2, TimeUnit.SECONDS);
    }

    public void updateWIndowTitlen(String title) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setTitle(title);
    }

    private Order doSellMARKET(CurrencyPair pair, BigDecimal amount) throws BitbankException, IOException {
        return bb.sendOrder(pair, BigDecimal.valueOf(0), amount, OrderSide.SELL, OrderType.MARKET);
    }
}
