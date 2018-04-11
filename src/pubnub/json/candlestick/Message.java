package pubnub.json.candlestick;

import java.math.BigDecimal;

public class Message {
    public long pid;
    public Data data;

    //final private String type = "1min";//1min,5min,15min,30min,1hour,4hour,8hour,12hour,1day,1week,1month
    public final String type1min() {
        return "1min";
    }

    public final String type15min() {
        return "15min";
    }

    public BigDecimal open(String type) {
        Candlestick candlestick = this.find(type);
        if (candlestick == null) {
            return null;
        }
        return new BigDecimal(candlestick.ohlcv.get(0)[0].toString());
    }

    public BigDecimal close(String type) {
        Candlestick candlestick = this.find(type);
        if (candlestick == null) {
            return null;
        }
        return new BigDecimal(candlestick.ohlcv.get(0)[3].toString());
    }

    public Long datetime(String type) {
        Candlestick candlestick = this.find(type);
        if (candlestick == null) {
            return null;
        }
        return Long.parseLong(candlestick.ohlcv.get(0)[5].toString());
    }

    private Candlestick find(String type) {
        for (Candlestick candlestick : this.data.candlestick) {
            if (candlestick == null || candlestick.type == null) {
                continue;
            }
            if (type.equals(candlestick.type)) {
                return candlestick;
            }
        }
        return null;
    }
}
