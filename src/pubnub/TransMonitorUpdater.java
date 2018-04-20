package pubnub;

import java.math.BigDecimal;

public interface TransMonitorUpdater {
    public boolean update(final long buyCount, final BigDecimal buyTotal, final long sellCount, final BigDecimal sellTotal);
}
