package auto3;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

import cc.Config;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.CandleType;
import db.SyncCandle;
import gui.action.GUIController;
import gui.action.LongSpanBtnAction;
import gui.form.KRMainFrame;
import gui.renderer.StripeTableRenderer;
import pubnub.json.candlestick.Candlestick;
import utils.DateUtil;
import utils.OtherUtil;
import utils.SwingUtil;

public class Candle15MForm extends KRMainFrame {

    private Logger logger = LogManager.getLogger();

    private static final long serialVersionUID = 8307509386386126712L;
    private static final Candle15MForm singleton = new Candle15MForm();

    public static Candle15MForm me() {
        return singleton;
    }

    // private BigDecimal balance = new BigDecimal(100 * 10000);
    private BigDecimal hold = BigDecimal.ZERO;
    final private BigDecimal AMOUNT = new BigDecimal(100);

    private final int table_width = 1350;

    private Candle15MForm() {
        logger.debug("Candle15MForm start");
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new Dimension(table_width + 40, 480));

        initGUI();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                final int startMonth = 3;
                final float period = 15.0f;
                initDB(startMonth, period);
                calc(true);
                new Candle15MWatcher(new Candle15MWatcherUpdater() {
                    boolean init = false;

                    @Override
                    public void doUpdate(final long timestamp, final Candlestick candle) {
                        // System.out.println(candle);
                        final String nowStr = DateUtil.me().format1(timestamp);
                        updateWIndowTitlen(nowStr);

                        Candle15M last = OtherUtil.me().lastItem(datas);
                        if (!init) {
                            if (last.openTime != candle.openTime()) {
                                System.err.println("データ足りません");
                                return;
                            }
                            init = true;
                        }
                        if (!init) {
                            return;
                        }
                        if (last.openTime != candle.openTime()) {
                            Candle15M newRow = new Candle15M(candle.openTime());
                            newRow.open = candle.open();
                            newRow.high = candle.high();
                            newRow.low = candle.low();
                            newRow.close = candle.close();
                            datas.add(newRow);
                            calc(false);
                            model.addRow(newRow);

                            if (!autoTrade.isSelected()) {
                                // logger.debug("自動取引：OFF");
                                return;
                            }
                            Candle15M last1 = OtherUtil.me().lastItem(datas);
                            if (!last1.buy9) {
                                logger.debug("[{}] 売買しない", nowStr);
                                return;
                            }
                            if (hold.compareTo(BigDecimal.ZERO) > 0) {
                                return;
                            }
                            // send buy order
                            //hold = balance.divide(candle.open(), 4, RoundingMode.DOWN);
                            final BigDecimal buyPrice = candle.close(); // closeは一番正確。このタイミングでの価格で買う。殆どの場合、openと変わらないはず。
                            new AutoBuyLimit(Config.me().getPair(), buyPrice, AMOUNT) {
                                @Override
                                public void onSuccessed(Order order) {
                                    hold = order.executedAmount;
                                }

                                @Override
                                public void onGiveUped(Order order) {
                                    hold = order.executedAmount;
                                }
                            }.execute();
                            logger.debug("[{}]:{}で買い。hold:{}", nowStr, buyPrice, hold);
                        } else {
                            last.open = candle.open();
                            last.high = candle.high();
                            last.low = candle.low();
                            last.close = candle.close();
                            calc(false);
                            model.updRow(last);

                            if (!autoTrade.isSelected()) {
                                // logger.debug("自動取引：OFF");
                                return;
                            }
                            /*Candle15M last1 = OtherUtil.me().lastItem(datas);
                            if (!last1.buy9) {
                                System.out.println(String.format("[%s] 売買しない", DateUtil.me().format0(timestamp)));
                                return;
                            }*/
                            // 売り条件は、持っていたらだけです。売買条件は途中で変わるため。
                            if (hold.compareTo(BigDecimal.ZERO) <= 0) {
                                return;
                            }
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(new Date(timestamp));
                            int min = cal1.get(Calendar.MINUTE);
                            int sec = cal1.get(Calendar.SECOND);
                            if (!(min == 14 || min == 29 || min == 44 || min == 59)) {
                                return;
                            }
                            if (sec < 50) {
                                return;
                            }
                            // send sell order
                            // balance = hold.multiply(candle.close());
                            final BigDecimal price = candle.close();
                            new AutoSellLimit(Config.me().getPair(), price, hold) {
                                @Override
                                public void onSuccessed(Order order) {
                                    hold = BigDecimal.ZERO;
                                }

                                @Override
                                public void onGiveUped(Order order) {
                                    hold = BigDecimal.ZERO; // TODO 売り残った数量をセット
                                }
                            }.execute();
                            logger.debug("[{}]:{}で売り。hold:{}", nowStr, price, hold);
                        }
                        // calc();
                    }
                }).monitor();
            }
        });
    }

    private List<Candle15M> datas = null;
    private final Candle15MTableModel model = new Candle15MTableModel();
    private final JTable table = new JTable(model) {
        private static final long serialVersionUID = 8304794967568437905L;
    };
    private final JScrollPane jScrollPane = new JScrollPane(table);

    static private final int fontSize = 14;
    private JSpinner spinnerB15M_E;
    private JSpinner spinnerB1H_E;
    private JSpinner spinnerB4H_E;
    private JSpinner spinnerB1D_E;
    private JSpinner spinnerCO_DIFF;

    final private int MAXContinuingBuy = 6; // 最大連続買い回数（5～6）がよさそう

    private JLabel simTransCount = new JLabel();
    private JLabel simBalance = new JLabel();

    private JCheckBox autoTrade = new JCheckBox("Auto", false);

    private void initGUI() {
        final int x1 = 20;
        int xx = x1;
        int width1 = 100;
        int pad1 = 20;
        final int y1 = 20;
        final int y2 = 50;
        int tableRowHight = fontSize + 6;
        Font font14 = new Font("MS Gothic", Font.PLAIN, fontSize);
        {
            {
                spinnerB15M_E = new JSpinner(new SpinnerNumberModel(0, -10, 10, 0.001));
                spinnerB15M_E.setBounds(xx, y1, 100, 24);
                add(spinnerB15M_E);
                xx += (width1 + pad1);
            }
            {
                spinnerB1H_E = new JSpinner(new SpinnerNumberModel(-0.06, -10, 10, 0.001));
                spinnerB1H_E.setBounds(xx, y1, 100, 24);
                add(spinnerB1H_E);
                xx += (width1 + pad1);
            }
            {
                spinnerB4H_E = new JSpinner(new SpinnerNumberModel(-0.06, -10, 10, 0.001));
                spinnerB4H_E.setBounds(xx, y1, 100, 24);
                add(spinnerB4H_E);
                xx += (width1 + pad1);
            }
            {
                spinnerB1D_E = new JSpinner(new SpinnerNumberModel(-0.12, -10, 10, 0.001));
                spinnerB1D_E.setBounds(xx, y1, 100, 24);
                add(spinnerB1D_E);
                xx += (width1 + pad1);
            }
            {
                spinnerCO_DIFF = new JSpinner(new SpinnerNumberModel(-0.01, -10, 10, 0.001));
                spinnerCO_DIFF.setBounds(xx, y1, 100, 24);
                add(spinnerCO_DIFF);
                xx += (width1 + pad1);
            }
        }
        {
            JButton btn = new JButton("Calc");
            // btn.setFont(font14);
            btn.setBounds(xx, y1, 100, 24);
            btn.addActionListener(new LongSpanBtnAction(btn, new GUIController() {
                @Override
                public Component parentComponent() {
                    return Candle15MForm.me();
                }
            }) {

                @Override
                public void doClick(ActionEvent event) throws Exception {
                    calc(true);
                }
            });
            add(btn);
        }
        {
            autoTrade.setBounds(xx + 110, y1, 100, 24);
            add(autoTrade);
        }
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

            // table.setShowGrid(false);
            model.initColumnSize(table.getColumnModel());

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //列の入れ替えを禁止
            table.getTableHeader().setReorderingAllowed(false);
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            //table.setComponentPopupMenu(new TablePopupMenu());

            jScrollPane.setBounds(x1, y2, table_width, 300);
            add(jScrollPane);
        }
        {
            simTransCount.setBounds(x1, 400, 100, 20);
            simTransCount.setText("0");
            add(simTransCount);

            simBalance.setBounds(x1 + 110, 400, 100, 20);
            simBalance.setText("0");
            add(simBalance);
        }
    }

    private void initDB(final int startM, final float period) {
        DruidPlugin dp = new DruidPlugin("jdbc:sqlite:bitbank@xrp_jpy.db", "", "");
        //DruidPlugin dp = new DruidPlugin("jdbc:sqlite::memory:", "", "");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("all.sql");
        dp.start();
        arp.start();

        {
            SyncCandle.me().create();
            Date last = SyncCandle.me().last();
            try {
                SyncCandle.me().sync(Config.me().getPair(), CandleType._1MIN, last, new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(2018, startM - 1, 1, 0, 0, 0);
            cal1.set(Calendar.MILLISECOND, 0);
            System.out.println(DateUtil.me().format1(cal1.getTime()));
            System.out.println(cal1.getTimeInMillis());
            List<Record> records = Db.find(Db.getSql("candles"), period, cal1.getTimeInMillis(), cal1.getTimeInMillis());
            datas = new ArrayList<Candle15M>(records.size());
            for (int ii = 0, len = records.size(); ii < len; ii++) {
                Record record = records.get(ii);
                final Candle15M row = new Candle15M(Long.parseLong(record.getStr("open_time")));
                // row.openTime = Long.parseLong(record.getStr("open_time"));
                row.open = new BigDecimal(record.getStr("open"));
                row.high = new BigDecimal(record.getStr("high"));
                row.low = new BigDecimal(record.getStr("low"));
                row.close = new BigDecimal(record.getStr("close"));

                datas.add(row);
            }
        }
    }

    private void calc(boolean insert) {
        BigDecimal B15M_E = new BigDecimal(spinnerB15M_E.getValue().toString()); //
        BigDecimal B1H_E = new BigDecimal(spinnerB1H_E.getValue().toString()); //
        BigDecimal B4H_E = new BigDecimal(spinnerB4H_E.getValue().toString()); //
        BigDecimal B1D_E = new BigDecimal(spinnerB1D_E.getValue().toString()); //
        BigDecimal CO_DIFF = new BigDecimal(spinnerCO_DIFF.getValue().toString()); // Close - Open diff
        calc(B15M_E, B1H_E, B4H_E, B1D_E, CO_DIFF, insert);
    }

    synchronized private void calc(final BigDecimal B15M_E, final BigDecimal B1H_E, final BigDecimal B4H_E, final BigDecimal B1D_E, final BigDecimal CO_DIFF, boolean insert) {
        //        System.out.println(B15M_E);
        //        System.out.println(B1H_E);
        //        System.out.println(B4H_E);
        //        System.out.println(B1D_E);
        //        System.out.println(CO_DIFF);
        int tradeCount = 0;
        BigDecimal startAmount = new BigDecimal("10000");

        final BigDecimal B15M = new BigDecimal(20); // 15m
        final BigDecimal B1H = new BigDecimal(20 * 4); // 1h
        final BigDecimal B4H = new BigDecimal(20 * 4 * 4); // 4h
        final BigDecimal B1D = new BigDecimal(20 * 4 * 4 * 6); // 1d

        {
            final int round = 4;
            final Ifohlc ifc = new Ifohlc() {
                @Override
                public BigDecimal getClose(int ii) {
                    return datas.get(ii).close;
                }
            };
            for (int ii = 0, len = datas.size(); ii < len; ii++) {
                Candle15M row = datas.get(ii);
                row.reset();
                row.ma_20_15M = calcMA(ifc, ii, B15M, round);
                row.ma_20_1H = calcMA(ifc, ii, B1H, round);
                row.ma_20_4H = calcMA(ifc, ii, B4H, round);
                row.ma_20_1D = calcMA(ifc, ii, B1D, round);
                row.close_open(CO_DIFF);
            }

            if (insert) {
                model.clear();
            }
            for (int ii = 0, len = datas.size(); ii < len; ii++) {
                Candle15M row = datas.get(ii);
                if (row.ma_20_15M == null || row.ma_20_1H == null || row.ma_20_4H == null || row.ma_20_1D == null) {
                    continue;
                }
                Candle15M rowP1 = datas.get(ii - 1);
                if (rowP1.ma_20_15M == null || rowP1.ma_20_1H == null || rowP1.ma_20_4H == null || rowP1.ma_20_1D == null) {
                    continue;
                }
                row.dma_20_15M = row.ma_20_15M.subtract(rowP1.ma_20_15M);
                row.dma_20_1H = row.ma_20_1H.subtract(rowP1.ma_20_1H);
                row.dma_20_4H = row.ma_20_4H.subtract(rowP1.ma_20_4H);
                row.dma_20_1D = row.ma_20_1D.subtract(rowP1.ma_20_1D);

                // bb
                BigDecimal bbBase = calcBoll(ifc, ii, 20, 2, 4);
                row.bb_20_high2 = row.ma_20_15M.add(TWO.multiply(bbBase));
                row.bb_20_low2 = row.ma_20_15M.subtract(TWO.multiply(bbBase));

                row.doCheckMA(B15M_E, B1H_E, B4H_E, B1D_E);

                row.buy9 = (row.checkMA) && (rowP1.isUp) && (checkBefore(datas, ii, MAXContinuingBuy));
                /*if (row.buy9 && rowP1.bb_20_high2 != null) {
                    if (rowP1.close.subtract(rowP1.bb_20_high2).compareTo(new BigDecimal("0.7")) >= 0) {
                        row.buy9 = false;
                    }
                }*/
                if (!row.buy9) {
                    // golden cross の場合、強制で買いにする
                    row.buy9 = goldenCross(datas, ii);
                }
                // log(row);
                if (insert) {
                    model.addRow(row);
                }

                if (row.buy9) {
                    tradeCount++;
                    startAmount = startAmount.divide(row.open, 4, RoundingMode.DOWN).multiply(row.close);
                }
            }
            if (insert) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JScrollBar scrollBar = jScrollPane.getVerticalScrollBar();
                        scrollBar.setValue(scrollBar.getMaximum());
                    }
                });
            }
            // System.out.println(String.format("Trade:%d, Balance:%s", tradeCount, startAmount));
            simTransCount.setText(Integer.toString(tradeCount));
            simBalance.setText(startAmount.toPlainString());
        }
    }

    /**
     * 連続買いを止める
     * @param datas
     * @param ii
     * @param period
     * @return
     */
    private final boolean checkBefore(final List<Candle15M> datas, final int ii, final int period) {
        if (ii < period) {
            return true;
        }
        for (int pidx = ii - period; pidx < ii; pidx++) {
            if (!datas.get(pidx).buy9) {
                // 連続じゃない買いの場合
                return true;
            }
        }
        return false;
    }

    private final boolean goldenCross(final List<Candle15M> datas, final int ii) {
        if (ii == 0) {
            return false;
        }
        final Candle15M p = datas.get(ii);
        if (p.ma_20_15M.compareTo(p.ma_20_1H) > 0) {
            final Candle15M p1 = datas.get(ii - 1);
            if (p1.ma_20_1H.compareTo(p1.ma_20_15M) > 0) {
                // logger.debug("gold cross : {}", p1.getOpenTimeDt());
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(Candle15MForm.me());
        frame.pack();
        frame.setLocationRelativeTo(null);
        Font font = new Font("MS Gothic", Font.PLAIN, fontSize);
        SwingUtil.me().updateFont(Candle15MForm.me(), font);
        frame.setVisible(true);
        //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // dispose();
                // System.exit(0); //calling the method is a must
                System.out.println("windowClosing");
            }
        });
    }
}
