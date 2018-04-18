package test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.bitbank.entity.Candlestick;
import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import ifc.BollValue;
import ifc.CandleValue;
import ifc.Exchange;
import ifc.KRCandleType;
import ifc.calc.CalcBoll;
import utils.BitbankClient;
import utils.DateUtil;

public class BitbankExchange implements Exchange<CurrencyPair, CandleType> {

    private static final int OPEN = 0;
    private static final int HIGH = 1;
    private static final int LOW = 2;
    private static final int CLOSE = 3;
    private static final int OPEN_TIME = 5;

    @Override
    public List<CandleValue> getCandles(final CurrencyPair pair, final CandleType candleType, final int limit) throws Exception {
        final List<BigDecimal[]> list = new ArrayList<BigDecimal[]>();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Candlestick result = null;
        do {
            final String date = DateUtil.me().format3(calendar.getTime());
            // System.out.println(date);
            result = BitbankClient.me().bbR.getCandlestick(pair, candleType, date);
            for (int len = result.candlestick[0].ohlcv.length, ii = len - 1; ii >= 0; ii--) {
                list.add(result.candlestick[0].ohlcv[ii]);
            }
            calendar.add(Calendar.DATE, -1);
        } while (list.size() + result.candlestick.length <= limit);

        final List<CandleValue> reslt = new ArrayList<CandleValue>();
        // 若い日付を前にする。（リストを逆順にする）
        for (int len = list.size(), ii = len - 1; ii >= 0; ii--) {
            BigDecimal[] ohlcv = list.get(ii);
            reslt.add(new CandleValue(KRCandleType._5MIN, ohlcv[OPEN], ohlcv[HIGH], ohlcv[LOW], ohlcv[CLOSE], ohlcv[OPEN_TIME].longValue()));
        }
        return reslt;
    }

    @Override
    public List<BollValue> getBoll(final List<CandleValue> candleValueList, final CurrencyPair pair, final CandleType candleType, final int period, final int round) throws Exception {
        // List<CandleValue> candleValueList = this.getCandles(pair, candleType, period);
        // calc boll
        return CalcBoll.me().calc(candleValueList, new BigDecimal(period), round);
    }

    public boolean lastBoll(final CandleValue lastOhlcv, final BollValue lastBollRow) throws Exception {
        return false;
    }
}
