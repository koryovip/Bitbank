package auto3;

import pubnub.json.candlestick.Candlestick;

public interface Candle15MWatcherUpdater {

    public void doUpdate(final long timestamp, final Candlestick candle);

}
