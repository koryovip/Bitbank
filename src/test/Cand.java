package test;

import java.math.BigDecimal;
import java.util.List;

import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import ifc.BollValue;
import ifc.CandleValue;
import ifc.Exchange;
import ifc.calc.CalcBoll;
import t.Twiite;
import utils.DateUtil;
import utils.OtherUtil;

public class Cand implements Runnable {

    public void execute() throws Exception {
        final int period = 20;
        final int limit = period + 10;
        final CandleType candleType = CandleType._5MIN;
        final CurrencyPair pair = CurrencyPair.XRP_JPY;
        final int round = 4;
        Exchange<CurrencyPair, CandleType> xxx = new BitbankExchange();

        final List<CandleValue> candleValueList = xxx.getCandles(pair, candleType, limit);
        List<BollValue> bollValueList = xxx.getBoll(candleValueList, pair, candleType, period, round);

        for (int ii = 0; ii < candleValueList.size(); ii++) {
            // open, high, low, close, volume, date
            CandleValue ohlcv = candleValueList.get(ii);
            System.out.print(String.format("%s\t%s\t%s\t%s\t%s", DateUtil.me().format1(ohlcv.time), ohlcv.open, ohlcv.high, ohlcv.low, ohlcv.close));
            BollValue bollRow = bollValueList.get(ii);
            System.out.print(String.format("\t|\t%s\t%s\t%s\t%s\t%s", bollRow.ma, bollRow.high1, bollRow.high2, bollRow.low1, bollRow.low2));
            if (bollRow.isValid()) {
                this.check(ohlcv, bollRow.high2, bollRow.low2, false);
            }
            System.out.println();
        }
        final CandleValue lastOhlcv = OtherUtil.me().lastItem(candleValueList);
        final BollValue lastBollRow = OtherUtil.me().lastItem(bollValueList);
        this.check(lastOhlcv, lastBollRow.high2, lastBollRow.low2, true);
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
            this.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception, Exception {
        Cand cand = new Cand();
        cand.execute();
    }
}
