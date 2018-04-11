package auto3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Ticker;
import cc.bitbank.entity.enums.CurrencyPair;

public class A {

    public static void main(String[] args) throws Exception, Exception {
        final Bitbankcc bb = new Bitbankcc();
        // bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());
        final BigDecimal jpy = new BigDecimal(10000);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(() -> {
            try {
                Ticker btcjpy = bb.getTicker(CurrencyPair.MONA_JPY);
                Ticker monabtc = bb.getTicker(CurrencyPair.MONA_BTC);
                Ticker monajpy = bb.getTicker(CurrencyPair.BTC_JPY);

                System.out.print(String.format("%s\t%s\t", btcjpy.buy, btcjpy.sell));
                System.out.print(String.format("%s\t%s\t", monabtc.buy, monabtc.sell));
                System.out.print(String.format("%s\t%s\t", monajpy.buy, monajpy.sell));
                System.out.print("|\t");
                ddd(jpy, btcjpy, 4, monabtc, 8, monajpy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private static void ddd(BigDecimal jpy, Ticker btcjpy, int round1, Ticker monabtc, int round2, Ticker monajpy) {
        BigDecimal aa = jpy.divide(btcjpy.sell, round1, RoundingMode.HALF_DOWN);
        System.out.print(String.format("%s\t", aa));
        //BigDecimal bb = aa.divide(monabtc.sell, round2, RoundingMode.HALF_DOWN);
        BigDecimal bb = aa.multiply(monabtc.buy).setScale(round2, RoundingMode.HALF_DOWN);
        System.out.print(String.format("%s\t", bb));
        BigDecimal cc = bb.multiply(monajpy.buy);
        System.out.print(String.format("%s\t", cc));
        System.out.println(String.format("%s", cc.subtract(jpy)));
    }

}
