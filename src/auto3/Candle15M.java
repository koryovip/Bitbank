package auto3;

import java.math.BigDecimal;

public class Candle15M extends CandleBase {

    /**15m*/
    public BigDecimal ma_20_15M;
    /**1h*/
    public BigDecimal ma_20_1H;
    /**4h*/
    public BigDecimal ma_20_4H;
    /**1d*/
    public BigDecimal ma_20_1D;

    /**15m*/
    public BigDecimal dma_20_15M;
    /**1h*/
    public BigDecimal dma_20_1H;
    /**4h*/
    public BigDecimal dma_20_4H;
    /**1d*/
    public BigDecimal dma_20_1D;

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
        this.checkMA = this.dma_20_15M.compareTo(a) > 0 //
                && this.dma_20_1H.compareTo(b) > 0 //
                && this.dma_20_4H.compareTo(c) > 0 //
                && this.dma_20_1D.compareTo(d) > 0 //
        ;
    }

    public void reset() {
        ma_20_15M = null;
        ma_20_1H = null;
        ma_20_4H = null;
        ma_20_1D = null;
        dma_20_15M = null;
        dma_20_1H = null;
        dma_20_4H = null;
        dma_20_1D = null;
        closeOpenDiff = null;
        checkMA = false;
        isUp = false;
        buy9 = false;
    }
}
