package auto3;

import java.util.Arrays;
import java.util.List;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import pubnub.BBReal;
import pubnub.KRPubNubChannel;
import pubnub.json.candlestick.Candlestick;

public class Candle15MWatcher extends BBReal {
    final private Candle15MWatcherUpdater updater;

    public Candle15MWatcher(Candle15MWatcherUpdater updater) {
        this.updater = updater;
    }

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge) {
        for (Candlestick candle : hoge.data.candlestick) {
            if (candle == null || candle.type == null) {
                continue;
            }
            if ("15min".equals(candle.type)) {
                updater.doUpdate(hoge.data.timestamp, candle);
                break;
            }
        }
    };

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.ticker.Message hoge) {
    };

    @Override
    protected void onError(PubNub pubnub, PNMessageResult message, Throwable t) {
        t.printStackTrace();
    }

    @Override
    protected List<String> channels() {
        return Arrays.asList(super.getFullChannelName(KRPubNubChannel.candlestick, "xrp_jpy") //
        , super.getFullChannelName(KRPubNubChannel.ticker, "xrp_jpy") //
        );
    }
}
