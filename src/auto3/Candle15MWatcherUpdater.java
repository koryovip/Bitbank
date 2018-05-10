package auto3;

import auto3.CandleWatcherBase.WatchState;
import pubnub.json.candlestick.Candlestick;

public interface Candle15MWatcherUpdater {

    public void doUpdate(final long timestamp, final Candlestick candle, final WatchState state, final Candlestick lastCandle);

    public int openRangeSec();

    public int closeRangeSec();
}
