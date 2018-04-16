package test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Candlestick;
import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.exception.BitbankException;
import utils.DateUtil;

public class Cand {

    private static final BigDecimal LENGTH = new BigDecimal(20);
    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final int OPEN = 0;
    private static final int HIGH = 1;
    private static final int LOW = 2;
    private static final int CLOSE = 3;

    public static void main(String[] args) throws Exception, Exception {
        final Bitbankcc bb = new Bitbankcc();
        List<BigDecimal[]> list = org(bb, CandleType._5MIN, LENGTH.intValue() + 1);

        // calc boll
        List<BigDecimal[]> boll = new ArrayList<BigDecimal[]>();
        for (int ii = 0; ii < list.size(); ii++) {
            boll.add(new BigDecimal[5]);
        }
        for (int ii = 0; ii < list.size(); ii++) {
            if (ii < LENGTH.intValue() - 1) {
                continue;
            }
            BigDecimal[] row = boll.get(ii);
            final BigDecimal ma = ma(list, ii, LENGTH);
            row[0] = ma;
            final BigDecimal boll1 = boll(list, ii, LENGTH, 1);
            row[1] = ma.add(ONE.multiply(boll1));
            row[2] = ma.add(TWO.multiply(boll1));
            row[3] = ma.subtract(ONE.multiply(boll1));
            row[4] = ma.subtract(TWO.multiply(boll1));
        }
        for (int ii = 0; ii < list.size(); ii++) {
            // open, high, low, close, volume, date
            BigDecimal[] ohlcv = list.get(ii);
            System.out.print(String.format("%s\t%s\t%s\t%s\t%s", DateUtil.me().format1(ohlcv[5].longValue()), ohlcv[OPEN], ohlcv[HIGH], ohlcv[LOW], ohlcv[CLOSE]));
            BigDecimal[] row = boll.get(ii);
            System.out.print(String.format("\t|\t%s\t%s\t%s\t%s\t%s", row[0], row[1], row[2], row[3], row[4]));
            if (row[0] != null && row[1] != null && row[2] != null && row[3] != null && row[4] != null) {
                if (getHigher(ohlcv).compareTo(row[2]) > 0) {
                    System.out.print(String.format("\t|\t%s\t↑↑↑", getHigher(ohlcv).subtract(row[2])));
                } else if (getLower(ohlcv).compareTo(row[4]) < 0) {
                    System.out.print(String.format("\t|\t%s\t↓↓↓", getLower(ohlcv).subtract(row[4])));
                }
            }
            System.out.println();
        }

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
            result = result.add(ohlcv[CLOSE]); // close
        }
        return result.divide(length, 4, RoundingMode.HALF_UP);
    }

    private static final BigDecimal boll(final List<BigDecimal[]> list, final int index, final BigDecimal length, final int sigma) {
        SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            BigDecimal[] ohlcv = list.get(ii);
            stats.addValue(ohlcv[3].doubleValue());
        }
        return new BigDecimal(FastMath.sqrt(stats.getPopulationVariance())).setScale(4, RoundingMode.HALF_UP);
    }

    private static final BigDecimal getHigher(BigDecimal[] ohlcv) {
        if (ohlcv[OPEN].compareTo(ohlcv[CLOSE]) >= 0) {
            return ohlcv[OPEN];
        }
        return ohlcv[CLOSE];
    }

    private static final BigDecimal getLower(BigDecimal[] ohlcv) {
        if (ohlcv[OPEN].compareTo(ohlcv[CLOSE]) <= 0) {
            return ohlcv[OPEN];
        }
        return ohlcv[CLOSE];
    }

    protected Double calculateStandardDiviation(List<Double> scores) {
        SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
        for (Double score : scores) {
            stats.addValue(score);
        }
        Double stdev = stats.getStandardDeviation();
        return Precision.round(stdev, 2);
    }

    protected Double calculatePopulationStandardDiviation(List<Double> scores) {
        SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
        for (Double score : scores) {
            stats.addValue(score);
        }
        Double varp = stats.getPopulationVariance();
        Double stdevp = FastMath.sqrt(varp);
        return Precision.round(stdevp, 2);
    }

}
