package db;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import cc.bitbank.entity.Candlestick;
import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.exception.BitbankException;
import utils.BitbankClient;
import utils.DateUtil;

public class SyncCandle {

    private Logger logger = LogManager.getLogger();

    private static final SyncCandle singleton = new SyncCandle();

    public static SyncCandle me() {
        return singleton;
    }

    private SyncCandle() {

    }

    final public void create() {
        Db.update("create table if not exists xrp_jpy (open_time integer not null, open real not null, high real not null, low real not null, close real not null, volume real not null, close_time integer, primary key(open_time))");
    }

    final public Date last() {
        List<Record> records = Db.find("select open_time from xrp_jpy order by open_time desc limit 1");
        Calendar cal1 = Calendar.getInstance();
        if (records.size() <= 0) {
            cal1.set(2017, 5 - 1, 25); // Bitbank は2017/05/25からXRPを扱う
        } else {
            cal1.setTime(new Date(Long.parseLong(records.get(0).getStr("open_time"))));
        }
        return cal1.getTime();
    }

    final public void sync(final CurrencyPair pair, final CandleType candleType, final Date from, final Date to) throws BitbankException, IOException {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        //Candlestick result = null;
        do {
            final String date = DateUtil.me().format3(calendar.getTime());
            logger.debug("sync db : " + date + "\t" + calendar.getTime());
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
