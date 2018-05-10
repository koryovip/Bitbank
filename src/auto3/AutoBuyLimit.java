package auto3;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;
import pubnub.TransactionController;
import utils.OtherUtil;

public abstract class AutoBuyLimit extends TransactionController {

    public AutoBuyLimit(CurrencyPair pair, BigDecimal price, BigDecimal amount) {
        super(pair, price, amount);
    }

    @Override
    protected OrderSide getOrderSide() {
        return OrderSide.BUY;
    }

    @Override
    protected OrderType getOrderType() {
        return OrderType.LIMIT;
    }

    public abstract void onSuccessed(Order order);

    public abstract boolean onGiveUped(Order order);

    public void execute() {
        super.execute(new KRTransaction<Order>() {
            private Logger logger = LogManager.getLogger();

            @Override
            public void onTransactionOrder(Order order) {
                logger.debug(order);
            }

            @Override
            public boolean onTransacting(Order order, int times) {
                if (times >= 60) {
                    return true;
                }
                logger.debug(order);
                OtherUtil.me().sleeeeeep(1000);
                return false;
            }

            @Override
            public void onSuccess(Order order) {
                onSuccessed(order);
            }

            @Override
            public void onFailed(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public boolean onGiveUp(Order order) {
                return onGiveUped(order);
            }
        });
    }
}
