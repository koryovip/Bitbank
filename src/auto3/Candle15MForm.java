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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

import gui.action.GUIController;
import gui.action.LongSpanBtnAction;
import gui.renderer.StripeTableRenderer;
import pubnub.json.candlestick.Candlestick;
import utils.DateUtil;
import utils.OtherUtil;
import utils.SwingUtil;

public class Candle15MForm extends JPanel {

    private static final long serialVersionUID = 8307509386386126712L;
    private Logger logger = LogManager.getLogger();
    private static final Candle15MForm singleton = new Candle15MForm();

    public static Candle15MForm me() {
        return singleton;
    }

    private Candle15MForm() {
        logger.debug("Candle15MForm start");
        setLayout(null);
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new Dimension(1600, 480));

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
                    public void doUpdate(Candlestick candle) {
                        // System.out.println(candle);
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
                        } else {
                            last.open = candle.open();
                            last.high = candle.high();
                            last.low = candle.low();
                            last.close = candle.close();
                            calc(false);
                            model.updRow(last);
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
                spinnerB15M_E = new JSpinner(new SpinnerNumberModel(-0.06, -10, 10, 0.001));
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
            btn.setFont(font14);
            btn.setBounds(xx, y1, 100, tableRowHight);
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

            jScrollPane.setBounds(x1, y2, 1600, 300);
            add(jScrollPane);
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

                row.doCheckMA(B15M_E, B1H_E, B4H_E, B1D_E);

                row.buy9 = (row.checkMA) && (rowP1.isUp);

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
        }
    }

    private final BigDecimal calcMA(final Ifohlc ifc, final int index, final BigDecimal length, final int round) {
        if (index <= length.intValue()) {
            return null;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            result = result.add(ifc.getClose(ii)); // close
        }
        return result.divide(length, round, RoundingMode.HALF_UP);
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
