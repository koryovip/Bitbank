package gui;

import java.math.BigDecimal;

public class TS {

    private final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);
    private BigDecimal sellPrice = MAX;

    public final long orderId;
    public final BigDecimal bought;
    public BigDecimal lostCut;
    public BigDecimal tralingStop;

    public TS(long orderId, BigDecimal bought, BigDecimal lostCut, BigDecimal tralingStop) {
        this.orderId = orderId;
        this.bought = bought;
        this.lostCut = lostCut;
        this.tralingStop = tralingStop;
    }

    //    public static void main(String[] args) {
    //        Bitbankcc bb = new Bitbankcc();
    //        bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());
    //
    //        CurrencyPair pair = CurrencyPair.BTC_JPY;
    //
    //        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    //        service.scheduleAtFixedRate(() -> {
    //            try {
    //                Ticker ticker = bb.getTicker(pair);
    //                // lostcut
    //                if (ticker.buy.compareTo(lostCut) <= 0) {
    //                    Order order = bb.sendOrder(pair, BigDecimal.ZERO, hold, OrderSide.SELL, OrderType.MARKET);
    //                    if (order != null && order.orderId != 0) {
    //                        System.out.println(order);
    //                        System.exit(1);
    //                    }
    //                }
    //                BigDecimal diff = ticker.buy.subtract(tralingStop);
    //                if ((sellPrice.compareTo(MAX) == 0) || (diff.compareTo(sellPrice) > 0)) {
    //                    sellPrice = diff;
    //                }
    //                // tralingStop
    //                if (sellPrice.compareTo(bought) > 0 && ticker.buy.compareTo(sellPrice) <= 0) {
    //                    Order order = bb.sendOrder(pair, BigDecimal.ZERO, hold, OrderSide.SELL, OrderType.MARKET);
    //                    if (order != null && order.orderId != 0) {
    //                        System.out.println(order);
    //                        System.exit(1);
    //                    }
    //                }
    //                System.out.println(String.format("%s\t%s\t%s\t%s", lostCut, ticker.buy, sellPrice, ticker.buy.subtract(bought).setScale(0, RoundingMode.HALF_UP)));
    //
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        } , 0, 3, TimeUnit.SECONDS);
    //    }

    public boolean check(final BigDecimal buy) {
        if (buy.compareTo(lostCut) <= 0) {
            return true;
        }
        BigDecimal diff = buy.subtract(tralingStop);
        if ((sellPrice.compareTo(MAX) == 0) || (diff.compareTo(sellPrice) > 0)) {
            sellPrice = diff;
        }
        // tralingStop
        if (sellPrice.compareTo(bought) > 0 && buy.compareTo(sellPrice) <= 0) {
            return true;
        }
        return false;
    }

    public void resetTralingStop(BigDecimal tralingStop) {
        this.tralingStop = tralingStop;
    }

    public void resetLostCut(BigDecimal lostCut) {
        this.lostCut = lostCut;
    }

    public BigDecimal getSellPrice() {
        return this.sellPrice;
    }
}
