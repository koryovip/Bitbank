package test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.bitbank.entity.enums.CandleType;
import cc.bitbank.entity.enums.CurrencyPair;
import ifc.BollValue;
import ifc.CandleValue;
import ifc.Exchange;
import utils.DateUtil;
import utils.OtherUtil;

public class Cand2 {

    public static void main(String[] args) throws Exception {
        Exchange<CurrencyPair, CandleType> xxx = new BitbankExchange();
        final int period = 20;
        final int limit = period + 10;
        final CandleType candleType = CandleType._5MIN;
        Map<CurrencyPair, Integer> datas = new HashMap<CurrencyPair, Integer>();
        datas.put(CurrencyPair.XRP_JPY, 3 + 1);
        datas.put(CurrencyPair.BTC_JPY, 0);
        datas.put(CurrencyPair.MONA_JPY, 3 + 1);
        for (Map.Entry<CurrencyPair, Integer> row : datas.entrySet()) {
            final CurrencyPair pair = row.getKey();
            final Integer round = row.getValue();
            final List<CandleValue> candleValueList = xxx.getCandles(pair, candleType, limit);
            List<BollValue> bollValueList = xxx.getBoll(candleValueList, pair, candleType, period, round);
            final CandleValue lastOhlcv = OtherUtil.me().lastItem(candleValueList);
            final BollValue lastBollRow = OtherUtil.me().lastItem(bollValueList);
            // check(lastOhlcv, lastBollRow.sigmaPlus2, lastBollRow.sigmaMinus2, false);
            show(pair, lastOhlcv, lastBollRow, round);
        }
        //        {
        //            final CurrencyPair pair = CurrencyPair.XRP_JPY;
        //            final List<CandleValue> candleValueList = xxx.getCandles(pair, candleType, limit);
        //            List<BollValue> bollValueList = xxx.getBoll(candleValueList, pair, candleType, period, 3 + 1);
        //            final CandleValue lastOhlcv = OtherUtil.me().lastItem(candleValueList);
        //            final BollValue lastBollRow = OtherUtil.me().lastItem(bollValueList);
        //            // check(lastOhlcv, lastBollRow.sigmaPlus2, lastBollRow.sigmaMinus2, false);
        //            show(pair, lastOhlcv, lastBollRow);
        //        }
        //        {
        //            final CurrencyPair pair = CurrencyPair.BTC_JPY;
        //            final List<CandleValue> candleValueList = xxx.getCandles(pair, candleType, limit);
        //            List<BollValue> bollValueList = xxx.getBoll(candleValueList, pair, candleType, period, 0);
        //            final CandleValue lastOhlcv = OtherUtil.me().lastItem(candleValueList);
        //            final BollValue lastBollRow = OtherUtil.me().lastItem(bollValueList);
        //            // check(lastOhlcv, lastBollRow.sigmaPlus2, lastBollRow.sigmaMinus2, false);
        //            show(pair, lastOhlcv, lastBollRow);
        //        }
    }

    private static final BigDecimal B100 = new BigDecimal(100);

    private static void show(CurrencyPair pair, final CandleValue ohlcv, final BollValue boll, final int round) {
        final BigDecimal bbWidth = boll.high2.subtract(boll.low2);
        System.out.println(String.format("%s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s", //
                DateUtil.me().format0(ohlcv.getTime()) //
                , pair.getCode().toUpperCase() //
                , ohlcv.close, boll.ma, boll.high2, boll.low2 //
                , bbWidth //
                , bbWidth.multiply(B100).divide(boll.ma, 2, RoundingMode.HALF_UP) //
                // https://wiki.profittrailer.com/doku.php?id=buy_and_sell_strategies
                , OtherUtil.me().scale(boll.low2.subtract(bbWidth.multiply(new BigDecimal(10 / 100))), round) //
                , OtherUtil.me().scale(boll.low2.add(bbWidth.multiply(new BigDecimal(10 / 100))), round) //
        ));
    }

}
