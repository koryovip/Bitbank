package gui;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.Config;
import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.Ticker;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;

public class TS {

    public static final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);
    private static BigDecimal sellPrice = MAX;

    public static void main(String[] args) {
        Bitbankcc bb = new Bitbankcc();
        bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());

        CurrencyPair pair = CurrencyPair.BTC_JPY;
        final BigDecimal hold = new BigDecimal(0.0575);
        final BigDecimal lostCut = new BigDecimal(869250);
        final BigDecimal tralingStop = new BigDecimal(7000);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                Ticker ticker = bb.getTicker(pair);
                // lostcut
                if (ticker.buy.compareTo(lostCut) <= 0) {
                    Order order = bb.sendOrder(pair, BigDecimal.ZERO, hold, OrderSide.SELL, OrderType.MARKET);
                    if (order != null && order.orderId != 0) {
                        System.out.println(order);
                        System.exit(1);
                    }
                }
                BigDecimal diff = ticker.buy.subtract(tralingStop);
                if ((sellPrice.compareTo(MAX) == 0) || (diff.compareTo(sellPrice) > 0)) {
                    sellPrice = diff;
                }
                // tralingStop
                if (ticker.buy.compareTo(sellPrice) <= 0) {
                    Order order = bb.sendOrder(pair, BigDecimal.ZERO, hold, OrderSide.SELL, OrderType.MARKET);
                    if (order != null && order.orderId != 0) {
                        System.out.println(order);
                        System.exit(1);
                    }
                }
                System.out.println(String.format("%s\t%s\t%s", lostCut, ticker.buy, sellPrice));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

}
