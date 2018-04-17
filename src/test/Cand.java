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
import t.Twiite;
import utils.BitbankClient;
import utils.DateUtil;
import utils.OtherUtil;

public class Cand implements Runnable, Exchange {

    private static final BigDecimal LENGTH = new BigDecimal(20);

    private static final int OPEN = 0;
    private static final int HIGH = 1;
    private static final int LOW = 2;
    private static final int CLOSE = 3;

    public static void main(String[] args) throws Exception, Exception {
        Cand cand = new Cand();
        cand.execute(cand);
    }

    public void execute(Exchange exchange) throws Exception, Exception {
        final int round = 4;
        List<CandleValue> candleValueList = exchange.getCandles(null, LENGTH.intValue() + 1);
        // calc boll
        List<BollValue> bollValueList = CalcBoll.me().calc(candleValueList, LENGTH, round);

        for (int ii = 0; ii < candleValueList.size(); ii++) {
            // open, high, low, close, volume, date
            CandleValue ohlcv = candleValueList.get(ii);
            System.out.print(String.format("%s\t%s\t%s\t%s\t%s", DateUtil.me().format1(ohlcv.time), ohlcv.open, ohlcv.high, ohlcv.low, ohlcv.close));
            BollValue bollRow = bollValueList.get(ii);
            System.out.print(String.format("\t|\t%s\t%s\t%s\t%s\t%s", bollRow.ma, bollRow.sigmaPlus1, bollRow.sigmaPlus2, bollRow.sigmaMinus1, bollRow.sigmaMinus2));
            if (bollRow.isValid()) {
                this.check(ohlcv, bollRow.sigmaPlus2, bollRow.sigmaMinus2, false);
            }
            System.out.println();
        }
        final CandleValue lastOhlcv = OtherUtil.me().lastItem(candleValueList);
        final BollValue lastBollRow = OtherUtil.me().lastItem(bollValueList);
        this.check(lastOhlcv, lastBollRow.sigmaPlus2, lastBollRow.sigmaMinus2, true);
    }

    private void check(final CandleValue ohlcv, final BigDecimal sigmaPlus2, final BigDecimal sigmaMinus2, boolean twitter) {
        {
            final BigDecimal higher = CalcBoll.me().getHigher(ohlcv);
            if (higher.compareTo(sigmaPlus2) > 0) {
                if (twitter) {
                    new Thread(new Twiite(String.format("下落可能！↓↓↓ 幅[%s]", sigmaPlus2.subtract(sigmaMinus2)))).start();
                } else {
                    System.out.print(String.format("\t|\t%s\t↑↑↑", sigmaPlus2.subtract(sigmaMinus2)));
                }
            }
        }
        {
            final BigDecimal lower = CalcBoll.me().getLower(ohlcv);
            if (lower.compareTo(sigmaMinus2) < 0) {
                if (twitter) {
                    new Thread(new Twiite(String.format("上昇可能！↑↑↑ 幅[%s]", sigmaPlus2.subtract(sigmaMinus2)))).start();
                } else {
                    System.out.print(String.format("\t|\t%s\t↓↓↓", sigmaPlus2.subtract(sigmaMinus2)));
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            this.execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CandleValue> getCandles(KRCandleType candleType, int maxCount) throws Exception {
        final List<BigDecimal[]> list = new ArrayList<BigDecimal[]>();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Candlestick result = null;
        do {
            final String date = DateUtil.me().format3(calendar.getTime());
            System.out.println(date);
            result = BitbankClient.me().bb.getCandlestick(CurrencyPair.XRP_JPY, CandleType._5MIN, date);
            for (int len = result.candlestick[0].ohlcv.length, ii = len - 1; ii >= 0; ii--) {
                list.add(result.candlestick[0].ohlcv[ii]);
            }
            calendar.add(Calendar.DATE, -1);
        } while (list.size() + result.candlestick.length <= maxCount);

        final List<CandleValue> reslt = new ArrayList<CandleValue>();
        // 若い日付を前にする。（リストを逆順にする）
        for (int len = list.size(), ii = len - 1; ii >= 0; ii--) {
            BigDecimal[] ohlcv = list.get(ii);
            reslt.add(new CandleValue(KRCandleType._5MIN, ohlcv[OPEN], ohlcv[HIGH], ohlcv[LOW], ohlcv[CLOSE], ohlcv[5].longValue()));
        }
        return reslt;
    }

}
