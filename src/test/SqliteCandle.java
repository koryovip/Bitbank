package test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

import cc.bitbank.entity.Candlestick;
import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.exception.BitbankException;
import utils.BitbankClient;
import utils.DateUtil;

public class SqliteCandle {

    public static void main(String[] args) throws Exception {
        DruidPlugin dp = new DruidPlugin("jdbc:sqlite:bitbank@xrp_jpy.db", "", "");
        //DruidPlugin dp = new DruidPlugin("jdbc:sqlite::memory:", "", "");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("all.sql");
        try {
            dp.start();
            arp.start();

            boolean init = false;
            if (init) {
                Db.update("create table if not exists xrp_jpy (open_time integer not null, open real not null, high real not null, low real not null, close real not null, volume real not null, close_time integer, primary key(open_time))");
            }
            {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                //cal1.set(2017, 5 - 1, 25);
                //cal1.set(2018, 4 - 1, 20);
                cal1.setTime(new Date());
                cal2.setTime(new Date());
                get(CurrencyPair.XRP_JPY, CandleType._1MIN, cal1.getTime(), cal2.getTime());
            }

            //            Candlestick aaa = BitbankClient.me().bbR.getCandlestick(CurrencyPair.XRP_JPY, CandleType._1MIN, "20180423");
            //            Db.tx(Connection.TRANSACTION_SERIALIZABLE, new IAtom() {
            //                @Override
            //                public boolean run() throws SQLException {
            //                    for (BigDecimal[] bbb : aaa.candlestick[0].ohlcv) {
            //                        // System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s", bbb[5], bbb[0], bbb[1], bbb[2], bbb[3], DateUtil.me().format0(bbb[5].longValue())));
            //                        Db.update("REPLACE into xrp_jpy values (?,?,?,?,?,null)", bbb[5], bbb[0], bbb[1], bbb[2], bbb[3]);
            //                    }
            //                    return true;
            //                }
            //            });
            {
                Calendar cal1 = Calendar.getInstance();
                cal1.set(2018, 4 - 1, 1, 0, 0, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                System.out.println(DateUtil.me().format1(cal1.getTime()));
                System.out.println(cal1.getTimeInMillis());
                List<Record> records = Db.find(Db.getSql("candles"), 60.0, cal1.getTimeInMillis(), cal1.getTimeInMillis());
                BigDecimal ppp = new BigDecimal(18);
                for (int ii = 0, len = records.size(); ii < len; ii++) {
                    Record record = records.get(ii);
                    if (ii < ppp.intValue() - 1) {
                        System.out.println(String.format("%s\t\"%s\"\t%s\t%s\t%s\t%s", //
                                record.getStr("open_time"), record.getStr("open_time2"), //
                                record.getStr("open"), record.getStr("high"), //
                                record.getStr("low"), record.getStr("close")));
                        continue;
                    }
                    BigDecimal totalH = BigDecimal.ZERO;
                    BigDecimal totalL = BigDecimal.ZERO;
                    for (int jj = ii - ppp.intValue() + 1; jj <= ii; jj++) {
                        totalH = totalH.add(new BigDecimal(records.get(jj).getStr("high")));
                        totalL = totalL.add(new BigDecimal(records.get(jj).getStr("low")));
                    }
                    BigDecimal aH = totalH.divide(ppp, 3, RoundingMode.HALF_UP);
                    BigDecimal aL = totalL.divide(ppp, 3, RoundingMode.HALF_UP);
                    System.out.println(String.format("%s\t\"%s\"\t%s\t%s\t%s\t%s\t%s\t%s", //
                            record.getStr("open_time"), record.getStr("open_time2"), //
                            record.getStr("open"), record.getStr("high"), //
                            record.getStr("low"), record.getStr("close"), //
                            aH, aL //
                    ));
                }
            }

        } finally {
            dp.stop();
            arp.stop();
        }
    }

    private static final void get(CurrencyPair pair, CandleType candleType, Date from, Date to) throws BitbankException, IOException {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        //Candlestick result = null;
        do {
            final String date = DateUtil.me().format3(calendar.getTime());
            System.out.println(date + "\t" + calendar.getTime());
            if (true) {
                Candlestick result = BitbankClient.me().bbR.getCandlestick(pair, candleType, date);
                Db.tx(Connection.TRANSACTION_SERIALIZABLE, new IAtom() {
                    @Override
                    public boolean run() throws SQLException {
                        for (BigDecimal[] bbb : result.candlestick[0].ohlcv) {
                            // System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s", bbb[5], bbb[0], bbb[1], bbb[2], bbb[3], DateUtil.me().format0(bbb[5].longValue())));
                            final String sql = "REPLACE into " + pair.getCode() + " values (?,?,?,?,?,?,null)";
                            Db.update(sql, bbb[5], bbb[0], bbb[1], bbb[2], bbb[3], bbb[4]);
                        }
                        return true;
                    }
                });
            }
            calendar.add(Calendar.DATE, 1);
        } while (calendar.getTime().compareTo(to) <= 0);

    }
}
