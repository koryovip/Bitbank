package auto3;

import java.math.BigDecimal;
import java.util.Date;

public abstract class CandleBase {
    public long openTime;
    public BigDecimal open;
    public BigDecimal high;
    public BigDecimal low;
    public BigDecimal close;

    private Date openTimeDt = null;

    public Date getOpenTimeDt() {
        if (openTimeDt == null) {
            openTimeDt = new Date(openTime);
        }
        return this.openTimeDt;
    }
}
