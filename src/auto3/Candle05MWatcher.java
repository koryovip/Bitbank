package auto3;

public class Candle05MWatcher extends CandleWatcherBase {

    public Candle05MWatcher(Candle15MWatcherUpdater updater) {
        super(updater);
    }

    @Override
    protected String getType() {
        return "5min";
    }

    @Override
    protected String getPair() {
        return "xrp_jpy";
    }

    @Override
    protected boolean isInOpenRange(int min) {
        return min == 0 || min == 5 || min == 10 || min == 15 || min == 20 || min == 25 || min == 30 || min == 35 || min == 40 || min == 45 || min == 50 || min == 55;
    }

    @Override
    protected boolean isInCloseRange(int min) {
        return min == 4 || min == 9 || min == 14 || min == 19 || min == 24 || min == 29 || min == 34 || min == 39 || min == 44 || min == 49 || min == 54 || min == 59;
    }
}
