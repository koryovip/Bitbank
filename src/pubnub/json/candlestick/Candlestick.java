package pubnub.json.candlestick;

import java.math.BigDecimal;
import java.util.List;

public class Candlestick {
    public static final int OPEN = 0;
    public static final int HIGH = 1;
    public static final int LOW = 2;
    public static final int CLOSE = 3;
    public static final int VOL = 4;
    public static final int OPEN_TIME = 5;
    public String type;
    public List<Object[]> ohlcv;

    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Candlestick [type=%s, ohlcv=%s]", type, this.ohlcv(ohlcv));
    }

    private String ohlcv(List<Object[]> ohlcv) {
        Object[] values = ohlcv.get(0);
        return String.format("Open:%s, High:%s, Low:%s, Close:%s, Vol: %s, TimeStamp: %s", values[OPEN], values[HIGH], values[LOW], values[CLOSE], values[VOL], values[OPEN_TIME]);
    }

    public BigDecimal open() {
        return new BigDecimal(this.ohlcv.get(0)[OPEN].toString());
    }

    public BigDecimal high() {
        return new BigDecimal(this.ohlcv.get(0)[HIGH].toString());
    }

    public BigDecimal low() {
        return new BigDecimal(this.ohlcv.get(0)[LOW].toString());
    }

    public BigDecimal close() {
        return new BigDecimal(this.ohlcv.get(0)[CLOSE].toString());
    }

    public BigDecimal vol() {
        return new BigDecimal(this.ohlcv.get(0)[VOL].toString());
    }

    public long openTime() {
        return Long.parseLong(this.ohlcv.get(0)[OPEN_TIME].toString());
    }
}