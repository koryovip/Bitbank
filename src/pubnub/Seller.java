package pubnub;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;

public class Seller implements Runnable {

    private Logger logger = LogManager.getLogger();

    private final Bitbankcc bb;
    private final CurrencyPair pair;
    private final BigDecimal price;
    private final BigDecimal amount;

    public Seller(final Bitbankcc bb, final CurrencyPair pair, final BigDecimal price, final BigDecimal amount) {
        this.bb = bb;
        this.pair = pair;
        this.price = price;
        this.amount = amount;
    }

    @Override
    public void run() {
        logger.debug("sell(MARKET):{} at {}", amount, price);
        try {
            Order order = bb.sendOrder(pair, price, amount, OrderSide.SELL, OrderType.LIMIT);
            System.out.println(order);
            if (order == null || order.orderId == 0) {
                throw new Exception("order is null");
            }
            do {
                order = bb.getOrder(CurrencyPair.XRP_JPY, order.orderId);
                System.out.println(order);
                sleeeeeep(1000);
            } while (!order.status.equals("FULLY_FILLED"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleeeeeep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}