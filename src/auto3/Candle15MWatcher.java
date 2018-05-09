package auto3;

public class Candle15MWatcher extends CandleWatcherBase {

    public Candle15MWatcher(Candle15MWatcherUpdater updater) {
        super(updater);
    }

    @Override
    protected String getType() {
        return "15min";
    }

    @Override
    protected String getPair() {
        return "xrp_jpy";
    }

    @Override
    protected boolean ckeckKirikae(int min) {
        return min == 14 || min == 29 || min == 44 || min == 59;
    }
}
