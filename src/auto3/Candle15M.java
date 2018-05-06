package auto3;

import java.math.BigDecimal;

public class Candle15M extends CandleBase {

    public Candle15M(long openTime) {
        super(openTime);
    }

    /**15m*/
    public BigDecimal ma_15M;
    /**1h*/
    public BigDecimal ma_1H;
    /**4h*/
    public BigDecimal ma_4H;
    /**1d*/
    public BigDecimal ma_1D;

    /**15m*/
    public BigDecimal dma_15M;
    /**1h*/
    public BigDecimal dma_1H;
    /**4h*/
    public BigDecimal dma_4H;
    /**1d*/
    public BigDecimal dma_1D;

    /** bb +2 */
    public BigDecimal bb_high2;
    /** bb -2 */
    public BigDecimal bb_low2;

    /** MA判断条件 */
    public boolean checkMA;
    /** close-open */
    public BigDecimal closeOpenDiff;
    public boolean isUp;
    /** 買い？ */
    public boolean buy9;

    public void close_open(BigDecimal base) {
        this.closeOpenDiff = this.close.subtract(this.open);
        this.isUp = this.closeOpenDiff.compareTo(base) > 0;
    }

    public void setUp() {
        this.closeOpenDiff = this.close.subtract(this.open);
    }

    public void doCheckMA(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d) {
        this.checkMA = this.dma_15M.compareTo(a) > 0 //
                && this.dma_1H.compareTo(b) > 0 //
                && this.dma_4H.compareTo(c) > 0 //
                && this.dma_1D.compareTo(d) > 0 //
                ;
    }

    public void reset() {
        ma_15M = null;
        ma_1H = null;
        ma_4H = null;
        ma_1D = null;
        dma_15M = null;
        dma_1H = null;
        dma_4H = null;
        dma_1D = null;
        bb_high2 = null;
        bb_low2 = null;
        checkMA = false;
        closeOpenDiff = null;
        isUp = false;
        buy9 = false;
    }
}
