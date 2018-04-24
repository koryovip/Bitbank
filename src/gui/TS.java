package gui;

import java.math.BigDecimal;

public class TS {

    // private final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);
    private BigDecimal sellPrice = BigDecimal.ZERO;

    public final long orderId;
    public final BigDecimal bought;
    public final BigDecimal amount;
    public BigDecimal lossCut = BigDecimal.ZERO;
    public BigDecimal tralingStop = BigDecimal.ZERO;
    private boolean onSelling = false;

    public static enum TSState {
        Idle, LossCutSell // lossCutで売り
        , TralingStoppingDown // TSで、買値以下
        , TralingStopUping // TSで買値以上
        , TralingStopSell // TSで売り
        ;
    }

    public TS(long orderId, BigDecimal bought, BigDecimal amount, BigDecimal lossCut, BigDecimal tralingStop) {
        this.orderId = orderId;
        this.bought = bought;
        this.amount = amount;
        this.lossCut = lossCut;
        this.tralingStop = tralingStop;
    }

    public boolean onSelling() {
        return this.onSelling;
    }

    public TSState check2(final BigDecimal buy) {
        if (lossCut.compareTo(BigDecimal.ZERO) > 0 && buy.compareTo(lossCut) <= 0) {
            // lossCut
            this.sellPrice = lossCut;
            this.onSelling = true;
            return TSState.LossCutSell;
        }
        if (tralingStop.compareTo(BigDecimal.ZERO) <= 0) {
            // TS値未設定の場合、何もしない
            this.sellPrice = BigDecimal.ZERO;
            return TSState.Idle;
        }
        if (buy.compareTo(sellPrice) <= 0) {
            this.onSelling = true;
            return TSState.TralingStopSell;
        }
        BigDecimal sell = buy.subtract(tralingStop);
        if (sell.compareTo(sellPrice) > 0) {
            sellPrice = sell;
            return TSState.TralingStopUping;
        }
        if (sellPrice.compareTo(bought) <= 0) {
            // 買値より安い場合、何もしない
            return TSState.TralingStoppingDown;
        }
        return TSState.Idle;
    }

    /**
    public boolean check(final BigDecimal buy) {
    
        if (this.onSelling) {
            return false;
        }
        if (lossCut.compareTo(BigDecimal.ZERO) > 0 && buy.compareTo(lossCut) <= 0) {
            this.onSelling = true;
            return true;
        }
        BigDecimal diff = buy.subtract(tralingStop);
        if ((sellPrice.compareTo(MAX) == 0) || (diff.compareTo(sellPrice) > 0)) {
            sellPrice = diff;
        }
        // tralingStop
        if (sellPrice.compareTo(bought) > 0 && buy.compareTo(sellPrice) <= 0) {
            this.onSelling = true;
            return true;
        }
        return false;
    }
    */

    public void resetTralingStop(BigDecimal tralingStop) {
        this.tralingStop = tralingStop;
    }

    public void resetLostCut(BigDecimal lossCut) {
        this.lossCut = lossCut;
    }

    public BigDecimal getSellPrice() {
        return this.sellPrice;
    }

    public BigDecimal getDistance() {
        if (this.sellPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return this.sellPrice.subtract(this.bought);
    }

    /**
     * 勝利？TP > 買った値段の場合、true
     * @return
     */
    public boolean isVictory() {
        return getDistance().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal profitTS() {
        return getDistance().multiply(this.amount);
    }

    public BigDecimal profitLC() {
        if (this.lossCut.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return this.lossCut.subtract(this.bought).multiply(this.amount);
    }

}
