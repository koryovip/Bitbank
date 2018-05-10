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

public abstract class TransactionController {

    private Logger logger = LogManager.getLogger();

    private final CurrencyPair pair;
    private final BigDecimal price;
    private final BigDecimal amount;

    protected abstract OrderSide getOrderSide();

    protected abstract OrderType getOrderType();

    public TransactionController(final CurrencyPair pair, final BigDecimal price, final BigDecimal amount) {
        this.pair = pair;
        this.price = price;
        this.amount = amount;
    }

    public void execute(final KRTransaction<Order> transaction) {
        logger.debug("sell(MARKET):{} at {}", amount, price);
        try {
            Order order = BitbankClient.me().bbW.sendOrder(pair, price, amount, getOrderSide(), getOrderType());
            if (order == null || order.orderId == 0) {
                throw new Exception("order is null");
            }
            logger.debug(order);
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
                boolean doCancel = transaction.onGiveUp(order);
                if (doCancel) {
                    Order order2 = BitbankClient.me().bbW.cancelOrder(Config.me().getPair(), order.orderId);
                    logger.debug("Cancel Order : {}", order2);
                }
            } else {
                transaction.onSuccess(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            transaction.onFailed(e);
        }
    }

    public interface KRTransaction<T> {
        /** オーダー注文正常終了 */
        public void onTransactionOrder(final T t);

        /** オーダー約定正常終了 */
        public void onSuccess(final T t);

        /**
         * オーダーの約定を諦める場合、trueを返す
         * @param t
         * @param times
         * @return
         */
        public boolean onTransacting(final T t, final int times);

        /** オーダー諦めた後 */
        public boolean onGiveUp(T order);

        /** オーダー異常終了 */
        public void onFailed(Throwable t);
    }

}
