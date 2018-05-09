package auto3;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pubnub.json.candlestick.Candlestick;

public class M15Buyer {

    public static void main(String[] args) {
        final BigDecimal price = new BigDecimal("83.5");
        final BigDecimal amount = new BigDecimal("10");

        new Candle05MWatcher(new Candle15MWatcherUpdater() {
            private Logger logger = LogManager.getLogger();

            @Override
            public int kirikaeSeconds() {
                return 50;
            }

            @Override
            public void doUpdate(final long timestamp, final Candlestick candle, final boolean isNew, final boolean kirikae) {
                if (isNew) {
                    logger.debug("NEW");
                }
                if (kirikae) {
                    logger.debug("kirikae");
                }
                if (candle.open().compareTo(candle.close()) >= 0) { // 下落
                    if (candle.open().compareTo(price) <= 0) {
                        // 買い注文
                    }
                }
            }
        }).monitor();
    }

}
