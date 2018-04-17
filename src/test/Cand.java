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
import t.Twiite;
import utils.DateUtil;
import utils.OtherUtil;

public class Cand implements Runnable {

    private static final BigDecimal LENGTH = new BigDecimal(20);
    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final int OPEN = 0;
    private static final int HIGH = 1;
    private static final int LOW = 2;
    private static final int CLOSE = 3;
    final Bitbankcc bb = new Bitbankcc();

    public static void main(String[] args) throws Exception, Exception {
        Cand cand = new Cand();
        cand.execute();
    }

    public void execute() throws Exception, Exception {
        List<BigDecimal[]> list = this.org(bb, CandleType._5MIN, LENGTH.intValue() + 1);

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
            BigDecimal[] bollRow = boll.get(ii);
            System.out.print(String.format("\t|\t%s\t%s\t%s\t%s\t%s", bollRow[0], bollRow[1], bollRow[2], bollRow[3], bollRow[4]));
            if (bollRow[0] != null && bollRow[1] != null && bollRow[2] != null && bollRow[3] != null && bollRow[4] != null) {
                this.check(ohlcv, bollRow[2], bollRow[4], false);
            }
            System.out.println();
        }
        final BigDecimal[] lastOhlcv = OtherUtil.me().lastItem(list);
        final BigDecimal[] lastBollRow = OtherUtil.me().lastItem(boll);
        this.check(lastOhlcv, lastBollRow[2], lastBollRow[4], true);
    }

    private void check(final BigDecimal[] ohlcv, final BigDecimal sigmaPlus2, final BigDecimal sigmaMinus2, boolean twitter) {
        {
            final BigDecimal higher = getHigher(ohlcv);
            if (higher.compareTo(sigmaPlus2) > 0) {
                if (twitter) {
                    new Thread(new Twiite(String.format("下落可能！↓↓↓", higher.subtract(sigmaPlus2)))).start();
                } else {
                    System.out.print(String.format("\t|\t%s\t↑↑↑", higher.subtract(sigmaPlus2)));
                }
            }
        }
        {
            final BigDecimal lower = getLower(ohlcv);
            if (lower.compareTo(sigmaMinus2) < 0) {
                if (twitter) {
                    new Thread(new Twiite(String.format("上昇可能！↑↑↑", lower.subtract(sigmaMinus2)))).start();
                } else {
                    System.out.print(String.format("\t|\t%s\t↓↓↓", lower.subtract(sigmaMinus2)));
                }
            }
        }
    }

    private final List<BigDecimal[]> org(final Bitbankcc bb, final CandleType candleType, final int max) throws BitbankException, IOException {
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

    private final BigDecimal ma(final List<BigDecimal[]> list, final int index, final BigDecimal length) {
        BigDecimal result = BigDecimal.ZERO;
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            BigDecimal[] ohlcv = list.get(ii);
            result = result.add(ohlcv[CLOSE]); // close
        }
        return result.divide(length, 4, RoundingMode.HALF_UP);
    }

    private final BigDecimal boll(final List<BigDecimal[]> list, final int index, final BigDecimal length, final int sigma) {
        SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            BigDecimal[] ohlcv = list.get(ii);
            stats.addValue(ohlcv[3].doubleValue());
        }
        return new BigDecimal(FastMath.sqrt(stats.getPopulationVariance())).setScale(4, RoundingMode.HALF_UP);
    }

    private final BigDecimal getHigher(BigDecimal[] ohlcv) {
        if (ohlcv[OPEN].compareTo(ohlcv[CLOSE]) >= 0) {
            return ohlcv[OPEN];
        }
        return ohlcv[CLOSE];
    }

    private final BigDecimal getLower(BigDecimal[] ohlcv) {
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

    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
