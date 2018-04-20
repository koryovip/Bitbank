package pubnub;

import java.math.BigDecimal;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import cc.Config;
import cc.bitbank.entity.Order;
import gui.TS;
import gui.form.BitBankMainFrame;
import mng.OrderManager;
import mng.TSHandler;
import mng.TSManager;
import pubnub.Seller.KRTransaction;
import utils.DateUtil;
import utils.OtherUtil;

public class TSMonitor extends BBReal {

    private Logger logger = LogManager.getLogger();
    final TSMonitorUpdater updater;

    public TSMonitor(final TSMonitorUpdater updater) {
        this.updater = updater;
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge) {
        // System.out.println(hoge.datetime(hoge.type1min()));
        // System.out.println(message);
    }

    private boolean doUpdate(final pubnub.json.ticker.Message hoge) {
        if (this.updater != null) {
            return this.updater.update(hoge);
        }
        return true;
    }

    private boolean doUpdate(final TS ts) {
        if (this.updater != null) {
            return this.updater.update(ts);
        }
        return true;
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.ticker.Message hoge) {
        this.doUpdate(hoge); // 利益などリアルタイム更新分

        if (TSManager.me().size() < 0) {
            return;
        }
        TSManager.me().handle(new TSHandler() {
            @Override
            public void handle(final TS ts) {

                doUpdate(ts);

                if (ts.onSelling()) {
                    // logger.debug("onSelling");
                    return;
                }
                boolean check = ts.check(hoge.data.buy);

                if (!check) {
                    return;
                }
                final BigDecimal profit = ts.isVictory() ? ts.profit() : BigDecimal.ZERO;
                logger.debug("{} : {} → {} | {}\t{}\t{}", DateUtil.me().format5(hoge.data.timestamp), ts.bought, hoge.data.buy, ts.getSellPrice(), ts.getDistance(), profit);

                new Thread(new Seller(Config.me().getPair(), /*hoge.data.buy*/ts.getSellPrice(), ts.amount, new KRTransaction<Order>() {
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

                })).start();
            }
        });
    }

    @Override
    protected void onError(PubNub pubnub, PNMessageResult message, Throwable t) {
        t.printStackTrace();
    }

    public static void main(String[] args) {
        TSMonitor monitor = new TSMonitor(null);
        monitor.monitor();
    }

}
