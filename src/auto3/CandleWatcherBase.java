package auto3;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import pubnub.BBReal;
import pubnub.KRPubNubChannel;
import pubnub.json.candlestick.Candlestick;

abstract public class CandleWatcherBase extends BBReal {
    final private Candle15MWatcherUpdater updater;

    public CandleWatcherBase(Candle15MWatcherUpdater updater) {
        this.updater = updater;
    }

    private Candlestick last = null;

    abstract protected String getType();

    abstract protected String getPair();

    abstract protected boolean ckeckKirikae(int min);

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge) {
        for (final Candlestick candle : hoge.data.candlestick) {
            if (candle == null || candle.type == null) {
                continue;
            }
            if (getType().equals(candle.type)) {
                //System.out.print(".");
                final long timestamp = hoge.data.timestamp;
                if (last == null) {
                    last = candle;
                }
                boolean isNew = (last.openTime() != candle.openTime());
                if (isNew) {
                    last = candle;
                }
                updater.doUpdate(timestamp, candle, isNew, isKirikaeMae(timestamp, updater.kirikaeSeconds()));
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
        return Arrays.asList(super.getFullChannelName(KRPubNubChannel.candlestick, getPair()) //
                , super.getFullChannelName(KRPubNubChannel.ticker, getPair()) //
        );
    }

    protected boolean isKirikaeMae(long timestamp, int seconds) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp);
        int min = cal1.get(Calendar.MINUTE);
        if (!(ckeckKirikae(min))) {
            return false;
        }
        int sec = cal1.get(Calendar.SECOND);
        if (sec < seconds) {
            return false;
        }
        return true;
    }
}
