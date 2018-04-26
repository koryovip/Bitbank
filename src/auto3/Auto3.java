package auto3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

import utils.DateUtil;

public class Auto3 {

    public static void main(String[] args) {
        DruidPlugin dp = new DruidPlugin("jdbc:sqlite:bitbank@xrp_jpy.db", "", "");
        //DruidPlugin dp = new DruidPlugin("jdbc:sqlite::memory:", "", "");
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("all.sql");
        try {
            dp.start();
            arp.start();

            int tradeCount = 0;
            BigDecimal startAmount = new BigDecimal("10000");

            final float period = 15.0f;
            final BigDecimal B15M = new BigDecimal(20); // 15m
            final BigDecimal B1H = new BigDecimal(20 * 4); // 1h
            final BigDecimal B4H = new BigDecimal(20 * 4 * 4); // 4h
            final BigDecimal B1D = new BigDecimal(20 * 4 * 4 * 6); // 1d

            final BigDecimal B15M_E = new BigDecimal("-0.06"); // 
            final BigDecimal B1H_E = new BigDecimal("-0.06"); // 
            final BigDecimal B4H_E = new BigDecimal("-0.06"); // 
            final BigDecimal B1D_E = new BigDecimal("-0.12"); //
            final BigDecimal CO_DIFF = new BigDecimal("-0.01"); // Close - Open diff
            {
                Calendar cal1 = Calendar.getInstance();
                cal1.set(2018, 1 - 1, 1, 0, 0, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                System.out.println(DateUtil.me().format1(cal1.getTime()));
                System.out.println(cal1.getTimeInMillis());
                List<Record> records = Db.find(Db.getSql("candles"), period, cal1.getTimeInMillis(), cal1.getTimeInMillis());
                final Ifohlc ifc = new Ifohlc() {
                    @Override
                    public BigDecimal getClose(int ii) {
                        return new BigDecimal(records.get(ii).getStr("close"));
                    }
                };
                List<Candle15M> infos = new ArrayList<Candle15M>(records.size());
                for (int ii = 0, len = records.size(); ii < len; ii++) {
                    Record record = records.get(ii);
                    Candle15M row = new Candle15M();
                    row.openTime = Long.parseLong(record.getStr("open_time"));
                    row.open = new BigDecimal(record.getStr("open"));
                    row.high = new BigDecimal(record.getStr("high"));
                    row.low = new BigDecimal(record.getStr("low"));
                    row.close = new BigDecimal(record.getStr("close"));

                    row.ma_20_15M = calcMA(ifc, ii, B15M, 4);
                    row.ma_20_1H = calcMA(ifc, ii, B1H, 4);
                    row.ma_20_4H = calcMA(ifc, ii, B4H, 4);
                    row.ma_20_1D = calcMA(ifc, ii, B1D, 4);

                    row.close_open(CO_DIFF);

                    infos.add(row);
                }

                for (int ii = 0, len = infos.size(); ii < len; ii++) {
                    Candle15M row = infos.get(ii);
                    if (row.ma_20_15M == null || row.ma_20_1H == null || row.ma_20_4H == null || row.ma_20_1D == null) {
                        continue;
                    }
                    Candle15M rowP1 = infos.get(ii - 1);
                    if (rowP1.ma_20_15M == null || rowP1.ma_20_1H == null || rowP1.ma_20_4H == null || rowP1.ma_20_1D == null) {
                        continue;
                    }
                    row.dma_20_15M = row.ma_20_15M.subtract(rowP1.ma_20_15M);
                    row.dma_20_1H = row.ma_20_1H.subtract(rowP1.ma_20_1H);
                    row.dma_20_4H = row.ma_20_4H.subtract(rowP1.ma_20_4H);
                    row.dma_20_1D = row.ma_20_1D.subtract(rowP1.ma_20_1D);

                    row.doCheckMA(B15M_E, B1H_E, B4H_E, B1D_E);

                    row.buy9 = (row.checkMA) && (rowP1.isUp);

                    if (row.buy9) {
                        tradeCount++;
                        startAmount = startAmount.divide(row.open, 4, RoundingMode.DOWN).multiply(row.close);
                    }

                    log(row);
                }
                System.out.println(String.format("Trade:%d, Balance:%s", tradeCount, startAmount));
            }
        } finally {
            dp.stop();
            arp.stop();
        }
    }

    private static final BigDecimal calcMA(final Ifohlc ifc, final int index, final BigDecimal length, final int round) {
        if (index <= length.intValue()) {
            return null;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (int ii = index - length.intValue() + 1; ii <= index; ii++) {
            result = result.add(ifc.getClose(ii)); // close
        }
        return result.divide(length, round, RoundingMode.HALF_UP);
    }

    private static final void log(Candle15M row) {
        System.out.println(String.format("%s\t\"%s\"\t%7s\t%7s\t%7s\t%7s\t|\t%8s\t%8s\t%8s\t%8s\t|\t%6s\t%6s\t%6s\t%6s\t|\t%s\t%6s\t%s\t%s" //
                , row.openTime //
                , DateUtil.me().format0(row.getOpenTimeDt()) //
                , row.open, row.high, row.low, row.close //
                , row.ma_20_15M, row.ma_20_1H, row.ma_20_4H, row.ma_20_1D //
                , row.dma_20_15M, row.dma_20_1H, row.dma_20_4H, row.dma_20_1D //
                , row.checkMA ? "〇" : "×" //
                , row.closeOpenDiff //
                , row.closeOpenDiff.compareTo(BigDecimal.ZERO) >= 0 ? "↑" : "↓" //
                , row.buy9 ? "買" : "―" //
        ));
    }
}
