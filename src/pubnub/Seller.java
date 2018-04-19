package pubnub;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cc.Config;
import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;
import utils.BitbankClient;

public class Seller implements Runnable {

    private Logger logger = LogManager.getLogger();

    private final CurrencyPair pair;
    private final BigDecimal price;
    private final BigDecimal amount;
    private final KRTransaction<Order> transaction;

    public Seller(final CurrencyPair pair, final BigDecimal price, final BigDecimal amount, final KRTransaction<Order> transaction) {
        this.pair = pair;
        this.price = price;
        this.amount = amount;
        this.transaction = transaction;
    }

    @Override
    public void run() {
        logger.debug("sell(MARKET):{} at {}", amount, price);
        try {
            Order order = BitbankClient.me().bbW.sendOrder(pair, price, amount, OrderSide.SELL, OrderType.LIMIT);
            logger.debug(order);
            if (order == null || order.orderId == 0) {
                throw new Exception("order is null");
            }
            transaction.onTransactionOrder(order);
            int retry = 0;
            boolean cancelOrder = false;
            do {
                order = BitbankClient.me().bbR.getOrder(Config.me().getPair(), order.orderId);
                logger.debug(order);
                if (transaction.onTransacting(order, retry++)) {
                    cancelOrder = true;
                    break;
                }
            } while (!order.status.equals("FULLY_FILLED"));
            if (cancelOrder) {
                // TODO 画面にメッセージ表示？
            } else {
                transaction.onSuccess(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            transaction.onFailed(e);
        }
    }

    public interface KRTransaction<T> {
        public void onTransactionOrder(final T t);

        public void onSuccess(final T t);

        public void onFailed(Throwable t);

        /**
         * オーダーの約定を諦める場合、trueを返す
         * @param t
         * @param times
         * @return
         */
        public boolean onTransacting(final T t, final int times);
    }

}
