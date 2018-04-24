package pubnub;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
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
import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import pubnub.json.transactions.Transaction;
import utils.DateUtil;
import utils.OtherUtil;
import utils.SwingUtil;

public class TransationForm extends JPanel {

    private static final long serialVersionUID = 3126844664533449219L;
    private Logger logger = LogManager.getLogger();
    private static final TransationForm singleton = new TransationForm();

    public static TransationForm me() {
        return singleton;
    }

    //    private final JLabel buyCountLbl = new JLabel();
    //    private final JLabel buyTotalLbl = new JLabel();
    //    private final JLabel sellCountLbl = new JLabel();
    //    private final JLabel sellTotalLbl = new JLabel();
    //    private final JProgressBar buySellPower = new JProgressBar(0, 100);

    private TransationForm() {
        logger.debug("TransationForm start");
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new Dimension(1120, 480));

        initGUI();

        start();
    }

    private void start() {
        //DruidPlugin dp = new DruidPlugin("jdbc:sqlite:R:/sample.db", "", "");
        DruidPlugin dp = new DruidPlugin("jdbc:sqlite::memory:", "", "");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("all.sql");

        try {
            dp.start();
            arp.start();

            Db.update("create table if not exists transactions (transaction_id integer, side string, price real, amount real, executed_at integer)");
            //arp.addMapping("transactions", TblTransactions.class);

            new BBReal() {
                @Override
                protected void onError(PubNub pubnub, PNMessageResult message, Throwable t) {
                    t.printStackTrace();
                }

                @Override
                protected List<String> channels() {
                    return Arrays.asList(super.getFullChannelName(KRPubNubChannel.transactions, "xrp_jpy"));
                }

                protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.transactions.Message hoge) {
                    int deleteCount = Db.update("delete from transactions where executed_at < strftime('%s','now','-1 hours')*1000");
                    for (Transaction row : hoge.data.transactions) {
                        //                        new TblTransactions() //
                        //                                .set("transaction_id", row.transaction_id) //
                        //                                .set("side", row.side) //
                        //                                .set("price", row.price) //
                        //                                .set("amount", row.amount) //
                        //                                .set("executed_at", row.executed_at) //
                        //                                //.set("executed_at2", sdf.format(new Date(row.executed_at))) //
                        //                                .save();
                        Db.update("insert into transactions values (?,?,?,?,?)", row.transaction_id, row.side, row.price, row.amount, row.executed_at);
                    }
                    List<Record> users = Db.find(Db.getSql("findGirl"));
                    for (Record record : users) {
                        String type = record.getStr("type");
                        if ("01min".equals(type)) {
                            a01.update(record);
                        } else if ("05min".equals(type)) {
                            a05.update(record);
                        } else if ("10min".equals(type)) {
                            a10.update(record);
                        } else if ("15min".equals(type)) {
                            a15.update(record);
                        } else if ("20min".equals(type)) {
                            a20.update(record);
                        }
                    }
                }
            }.monitor();
        } finally {
            //dp.stop();
            //arp.stop();
        }
    }

    final private MyComponet a01 = new MyComponet("1");
    final private MyComponet a05 = new MyComponet("5");
    final private MyComponet a10 = new MyComponet("10");
    final private MyComponet a15 = new MyComponet("15");
    final private MyComponet a20 = new MyComponet("20");

    private void initGUI() {
        final int width = 350;
        final int padding = 10;
        final int x1 = 20;
        final int x2 = x1 + width + padding;
        final int x3 = x2 + width + padding;
        final int x4 = x3 + width + padding;
        final int x5 = x4 + width + padding;
        a01.place(this, x1, 10);
        a05.place(this, x2, 10);
        a10.place(this, x3, 10);
        a15.place(this, x1, 200);
        a20.place(this, x2, 200);
    }

    protected class MyComponet {
        private final JLabel from = new JLabel();
        private final JLabel buyCountLbl = new JLabel();
        private final JLabel buyTotalLbl = new JLabel();
        private final JLabel sellCountLbl = new JLabel();
        private final JLabel sellTotalLbl = new JLabel();
        private final JProgressBar buySellPower = new JProgressBar(0, 100);

        protected final String name;

        protected MyComponet(String name) {
            this.name = name;
        }

        protected final void place(final Container container, final int x, final int y) {
            final int x1 = 25;
            final int height = 20;
            final int padding = 10;
            final int y1 = 30;
            final int y2 = y1 + height + padding;
            final int y3 = y2 + height + padding;
            final int y4 = y3 + height + padding;
            final int width1 = 100;
            final int width2 = 200;
            final int width3 = 300;

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBounds(x, y, 350, 160);
            panel.setBorder(BorderFactory.createTitledBorder(name));
            container.add(panel);

            buyCountLbl.setText("0");
            buyCountLbl.setBounds(x1, y1, width1, height);
            buyCountLbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            buyCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(buyCountLbl);

            buyTotalLbl.setText("0");
            buyTotalLbl.setBounds(x1 + width1 - 1, y1, width2, height);
            buyTotalLbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            buyTotalLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            buyTotalLbl.setForeground(Color.BLUE);
            panel.add(buyTotalLbl);

            sellCountLbl.setText("0");
            sellCountLbl.setBounds(x1, y2, width1, height);
            sellCountLbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            sellCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(sellCountLbl);

            sellTotalLbl.setText("0");
            sellTotalLbl.setBounds(x1 + width1 - 1, y2, width2, height);
            sellTotalLbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            sellTotalLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            sellTotalLbl.setForeground(Color.PINK);
            panel.add(sellTotalLbl);

            buySellPower.setBounds(x1, y3, width3, height);
            buySellPower.setStringPainted(true);// このプロパティーは、進捗バーが進捗文字列を描画するかどうかを指定します
            buySellPower.setValue(0);
            panel.add(buySellPower);

            from.setBounds(x1, y4, width3, height);
            from.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(from);

        }

        protected final void update(Record record) {
            BigDecimal buyTotal = new BigDecimal(record.getStr("buy1"));
            BigDecimal sellTotal = new BigDecimal(record.getStr("sell1"));

            buyCountLbl.setText(Long.toString(record.getLong("buy2")));
            buyTotalLbl.setText(OtherUtil.me().scale(buyTotal, 1).toString());
            sellCountLbl.setText(Long.toString(record.getLong("sell2")));
            sellTotalLbl.setText(OtherUtil.me().scale(sellTotal, 1).toString());
            buySellPower.setValue(OtherUtil.me().persent(buyTotal, sellTotal).intValue());

            from.setText(DateUtil.me().format5(record.getLong("from_")));
        }
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
        frame.getContentPane().add(TransationForm.me());
        frame.pack();
        frame.setLocationRelativeTo(null);
        Font font = new Font("MS Gothic", Font.PLAIN, 24);
        SwingUtil.me().updateFont(TransationForm.me(), font);
        frame.setVisible(true);
    }
}
