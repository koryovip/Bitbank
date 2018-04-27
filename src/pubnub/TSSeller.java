package pubnub;

import java.math.BigDecimal;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cc.bitbank.entity.Order;
import cc.bitbank.entity.enums.CurrencyPair;
import cc.bitbank.entity.enums.OrderSide;
import cc.bitbank.entity.enums.OrderType;
import gui.form.BitBankMainFrame;
import mng.OrderManager;
import mng.TSManager;
import utils.OtherUtil;

public class TSSeller extends TransactionController implements Runnable {

    public TSSeller(final CurrencyPair pair, final BigDecimal price, final BigDecimal amount) {
        super(pair, price, amount);
    }

    @Override
    protected OrderSide getOrderSide() {
        return OrderSide.SELL;
    }

    @Override
    protected OrderType getOrderType() {
        return OrderType.LIMIT;
    }

    @Override
    public void run() {
        super.execute(new KRTransaction<Order>() {
            private Logger logger = LogManager.getLogger();

            @Override
            public void onTransactionOrder(final Order order) {
                OrderManager.me().add(order.orderId);
                BitBankMainFrame.me().addOrder(order);
            }

            @Override
            public boolean onTransacting(final Order order, final int times) {
                if (times >= 60) {
                    return true;
                }
                BitBankMainFrame.me().updOrder(order);
                OtherUtil.me().sleeeeeep(1000);
                return false;
            }

            @Override
            public void onSuccess(final Order order) {
                logger.debug("onSuccess!");
                BitBankMainFrame.me().updOrder(order);
                if (TSManager.me().remove(order.orderId)) {
                    BitBankMainFrame.me().resetRowDataTS(order);
                }
                JOptionPane.showMessageDialog(BitBankMainFrame.me(), "TP is OK", "OK", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onFailed(Throwable t) {
                JOptionPane.showMessageDialog(BitBankMainFrame.me(), t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void onGiveUp(Order order) {
                // TODO どうする？
            }
        });
    }

}
