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

    abstract protected boolean isInOpenRange(int min);

    abstract protected boolean isInCloseRange(int min);

    public enum WatchState {
        Init, Idle //
        , Opening, Opened //
        , Closeing, Closed //
        ;
    }

    private WatchState state = WatchState.Init;

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge) {
        for (final Candlestick candle : hoge.data.candlestick) {
            if (candle == null || candle.type == null) {
                continue;
            }
            if (getType().equals(candle.type)) {
                //System.out.print(".");
                final long timestamp = hoge.data.timestamp;
                final boolean isInOpenRange = __isInOpenRange(timestamp, updater.openRangeSec());
                if (!isInOpenRange) {
                    this.last = candle;
                }
                final boolean isInCloseRange = __isInCloseRange(timestamp, updater.closeRangeSec());

                if (isInOpenRange) {
                    if (this.state == WatchState.Opening) {
                        this.state = WatchState.Opened;
                    }
                    if (this.state != WatchState.Opened) {
                        this.state = WatchState.Opening;
                    }
                } else if (isInCloseRange) {
                    if (this.state == WatchState.Closeing) {
                        this.state = WatchState.Closed;
                    }
                    if (this.state != WatchState.Closed) {
                        this.state = WatchState.Closeing;
                    }
                } else {
                    this.state = WatchState.Idle;
                }
                updater.doUpdate(timestamp, candle, this.state, this.last);
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
        return Arrays.asList( //
                super.getFullChannelName(KRPubNubChannel.candlestick, getPair()) //
                , super.getFullChannelName(KRPubNubChannel.ticker, getPair()) //
        );
    }

    protected boolean __isInOpenRange(long timestamp, int seconds) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp);
        int min = cal1.get(Calendar.MINUTE);
        if (!(isInOpenRange(min))) {
            return false;
        }
        int sec = cal1.get(Calendar.SECOND);
        if (sec > seconds) {
            return false;
        }
        return true;
    }

    protected boolean __isInCloseRange(long timestamp, int seconds) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp);
        int min = cal1.get(Calendar.MINUTE);
        if (!(isInCloseRange(min))) {
            return false;
        }
        int sec = cal1.get(Calendar.SECOND);
        if (sec < seconds) {
            return false;
        }
        return true;
    }
}
