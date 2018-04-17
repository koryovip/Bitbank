package pubnub;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import cc.Config;
import cc.bitbank.Bitbankcc;
import cc.bitbank.entity.enums.CurrencyPair;
import gui.TS;
import utils.DateUtil;

public class TSMonitor extends BBReal {

    private Logger logger = LogManager.getLogger();

    final Bitbankcc bb = new Bitbankcc();

    public TSMonitor() {
        bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge) {
        // System.out.println(hoge.datetime(hoge.type1min()));
        // System.out.println(message);
    }

    private static final List<TS> TS_LIST = new ArrayList<TS>();
    static {
        TS_LIST.add(new TS(30047606L, new BigDecimal("70.8800"), new BigDecimal(100), new BigDecimal("50"), new BigDecimal("0.3")));
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.ticker.Message hoge) {
        // System.out.println(hoge.data.buy);
        if (TS_LIST.size() <= 0) {
            return;
        }
        for (final TS ts : TS_LIST) {
            boolean check = ts.check(hoge.data.buy);
            logger.debug("{} : {} , {} , {}, {}", DateUtil.me().format1(hoge.data.timestamp), ts.getSellPrice(), hoge.data.buy, ts.bought, ts.getSellPrice().subtract(ts.bought).multiply(ts.amount));
            if (!check) {
                continue;
            }
            new Thread(new Seller(bb, CurrencyPair.XRP_JPY, hoge.data.buy, ts.amount)).start();
            TS_LIST.remove(0);
            //            new Thread() {
            //                @Override
            //                public void run() {
            //                    logger.debug("sell(MARKET):{} at {}", ts.amount, hoge.data.buy);
            //                    try {
            //                        Order order = bb.sendOrder(CurrencyPair.XRP_JPY, hoge.data.buy, ts.amount, OrderSide.SELL, OrderType.LIMIT);
            //                        System.out.println(order);
            //                        if (order == null || order.orderId == 0) {
            //                            throw new Exception("order is null");
            //                        }
            //                        do {
            //                            order = bb.getOrder(CurrencyPair.XRP_JPY, order.orderId);
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
        TSMonitor monitor = new TSMonitor();
        monitor.monitor();
    }

}
