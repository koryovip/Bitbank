package pubnub.json.candlestick;

import java.util.List;

public class Candlestick {
    public String type;
    public List<Object[]> ohlcv;

    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Candlestick [type=%s, ohlcv=%s]", type, ohlcv);
    }

}