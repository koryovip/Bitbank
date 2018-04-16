package test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Candlestick;
import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.exception.BitbankException;
import utils.DateUtil;

public class Cand {

    public static void main(String[] args) throws Exception, Exception {
        final Bitbankcc bb = new Bitbankcc();
        List<BigDecimal[]> list = org(bb, CandleType._30MIN, 300);

        // calc boll
        List<BigDecimal[]> boll = new ArrayList<BigDecimal[]>();
        for (int ii = 0; ii < list.size(); ii++) {
            boll.add(new BigDecimal[5]);
        }
        final BigDecimal LENGTH = new BigDecimal(20);
        for (int ii = 0; ii < list.size(); ii++) {
            if (ii < LENGTH.intValue() - 1) {
                continue;
            }
            BigDecimal[] row = boll.get(ii);
            row[0] = ma(list, ii, LENGTH);
            row[1] = new BigDecimal(ii);
            row[2] = new BigDecimal(ii);
            row[3] = new BigDecimal(ii);
            row[4] = new BigDecimal(ii);
        }
        for (int ii = 0; ii < list.size(); ii++) {
            // open, high, low, close, volume, date
            BigDecimal[] ohlcv = list.get(ii);
            System.out.print(String.format("%s\t%s\t%s\t%s\t%s", DateUtil.me().format1(ohlcv[5].longValue()), ohlcv[0], ohlcv[1], ohlcv[2], ohlcv[3]));
            BigDecimal[] row = boll.get(ii);
            System.out.println(String.format("\t|\t%s\t%s\t%s\t%s\t%s", row[0], row[1], row[2], row[3], row[4]));
        }
        /*
        for (BigDecimal[] ohlcv : list) {
            // open, high, low, close, volume, date
            System.out.println(String.format("%s\t%s\t%s\t%s\t%s", DateUtil.me().format1(ohlcv[5].longValue()), ohlcv[0], ohlcv[1], ohlcv[2], ohlcv[3]));
        }
        */
    }

    private static final List<BigDecimal[]> org(final Bitbankcc bb, final CandleType candleType, final int max) throws BitbankException, IOException {
        final List<BigDecimal[]> list = new ArrayList<BigDecimal[]>();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Candlestick result = null;
        do {
            final String date = DateUtil.me().format3(calendar.getTime());
            System.out.println(date);
            result = bb.getCandlestick(CurrencyPair.XRP_JPY, candleType, date);
            for (int len = result.candlestick[0].ohlcv.length, ii = len - 1; ii >= 0; ii--) {
                list.add(result.candlestick[0].ohlcv[ii]);
            }
            calendar.add(Calendar.DATE, -1);
        } while (list.size() + result.candlestick.length <= max);
        final List<BigDecimal[]> reslt = new ArrayList<BigDecimal[]>();
        // 若い日付を前にする。（リストを逆順にする）
        for (int len = list.size(), ii = len - 1; ii >= 0; ii--) {
            reslt.add(list.get(ii));
        }
        return reslt;
    }

    private static final BigDecimal ma(final List<BigDecimal[]> list, final int index, final BigDecimal length) {
        BigDecimal result = BigDecimal.ZERO;
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            BigDecimal[] ohlcv = list.get(ii);
            result = result.add(ohlcv[3]); // close
        }
        return result.divide(length, 4, RoundingMode.HALF_UP);
    }

}
