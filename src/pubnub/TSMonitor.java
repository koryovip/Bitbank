package pubnub;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import cc.Config;
import gui.TS;

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

    private static final List<TS> TS_LIST = new ArrayList<TS>();
    static {
        // TS_LIST.add(new TS(30499297L, new BigDecimal("71.6200"), new BigDecimal(100), new BigDecimal("50"), new BigDecimal("0.3")));
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
        if (TS_LIST.size() <= 0) {
            return;
        }
        for (final TS ts : TS_LIST) {
            this.doUpdate(ts);
            if (ts.onSelling()) {
                // logger.debug("onSelling");
                continue;
            }
            boolean check = ts.check(hoge.data.buy);

            //final BigDecimal profit = ts.isVictory() ? ts.profit() : BigDecimal.ZERO;
            //logger.debug("{} : {} → {} | {}\t{}\t{}", DateUtil.me().format5(hoge.data.timestamp), ts.bought, hoge.data.buy, ts.getSellPrice(), ts.getDistance(), profit);
            if (!check) {
                continue;
            }
            new Thread(new Seller(Config.me().getPair(), hoge.data.buy, ts.amount)).start();
            // TS_LIST.remove(0);
            //            new Thread() {
            //                @Override
            //                public void run() {
            //                    logger.debug("sell(MARKET):{} at {}", ts.amount, hoge.data.buy);
            //                    try {
            //                        Order order = bb.sendOrder(Config.me().getPair(), hoge.data.buy, ts.amount, OrderSide.SELL, OrderType.LIMIT);
            //                        System.out.println(order);
            //                        if (order == null || order.orderId == 0) {
            //                            throw new Exception("order is null");
            //                        }
            //                        do {
            //                            order = bb.getOrder(Config.me().getPair(), order.orderId);
            //                            System.out.println(order);
            //                            sleeeeeep(1000);
            //                        } while (!order.status.equals("FULLY_FILLED"));
            //                    } catch (Exception e) {
            //                        e.printStackTrace();
            //                    }
            //                }
            //            }.start();
        }
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
