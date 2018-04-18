package pubnub;

import gui.TS;

public interface TSMonitorUpdater {
    public boolean update(final pubnub.json.ticker.Message hoge);

    public boolean update(final TS ts);

}