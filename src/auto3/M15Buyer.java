package auto3;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto3.CandleWatcherBase.WatchState;
import pubnub.json.candlestick.Candlestick;

public class M15Buyer {

    public static void main(String[] args) {

        new Candle05MWatcher(new Candle15MWatcherUpdater() {
            private Logger logger = LogManager.getLogger();
            final BigDecimal amount = new BigDecimal("10");
            private BigDecimal price = new BigDecimal("87");

            @Override
            public int openRangeSec() {
                return 10;
            }

            @Override
            public int closeRangeSec() {
                return 50;
            }

            @Override
            public void doUpdate(final long timestamp, final Candlestick candle, final WatchState state, final Candlestick lastCandle) {
                // logger.debug(state);
                if (state != WatchState.Opening) {
                    return;
                }
                //logger.debug(lastCandle);
                //logger.debug(candle);
                if (lastCandle.open().compareTo(lastCandle.close()) >= 0) { // 下落
                    if (lastCandle.open().compareTo(price) <= 0) {
                        // 買い注文
                        price = lastCandle.open();
                        logger.debug("{} で買い注文", price);
                    }
                }
            }
        }).monitor();
    }

}
