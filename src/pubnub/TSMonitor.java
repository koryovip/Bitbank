package pubnub;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import cc.Config;
import gui.TS;
import gui.TS.TSState;
import mng.TSHandler;
import mng.TSManager;
import utils.DateUtil;
import utils.OtherUtil;

public class TSMonitor extends BBReal {

    private Logger logger = LogManager.getLogger();
    final TSMonitorUpdater updater;

    public TSMonitor(final TSMonitorUpdater updater) {
        this.updater = updater;
    }

    @Override
    protected List<String> channels() {
        final String pair = Config.me().getPair().getCode();
        return Arrays.asList(super.getFullChannelName(KRPubNubChannel.ticker, pair) //
        //                , super.getFullChannelName(KRPubNubChannels.depth, pair) //
        //                , super.getFullChannelName(KRPubNubChannels.transactions, pair) //
        );
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.depth.Message hoge) {
        System.out.println(message);
    }

    @Override
    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.transactions.Message hoge) {
        System.out.println(message);
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

                TSState state = ts.check2(/*hoge.data.buy*/OtherUtil.me().average(Config.me().getRoundCurrencyPair(), hoge.data.buy, hoge.data.sell)); // 売買の平均値を渡す
                if (!(state == TSState.LossCutSell || state == TSState.TralingStopSell)) {
                    return;
                }

                final BigDecimal profit = ts.isVictory() ? ts.profitTS() : BigDecimal.ZERO;
                logger.debug("{} : {} → {} | {}\t{}\t{}", DateUtil.me().format5(hoge.data.timestamp), ts.bought, hoge.data.buy, ts.getSellPrice(), ts.getDistance(), profit);

                new Thread(new TSSeller(Config.me().getPair(), /*hoge.data.buy*/ts.getSellPrice(), ts.amount)).start();
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
