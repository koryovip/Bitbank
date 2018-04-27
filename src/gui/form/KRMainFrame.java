package gui.form;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.commons.math3.util.FastMath;

import auto3.Ifohlc;

public abstract class KRMainFrame extends JPanel {

    private static final long serialVersionUID = -2118304134132744623L;

    final public void updateWIndowTitlen(String title) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setTitle(title);
    }

    /**
     * @param ifc
     * @param index
     * @param period
     * @param round
     * @return
     */
    final protected BigDecimal calcMA(final Ifohlc ifc, final int index, final BigDecimal period, final int round) {
        if (index <= period.intValue()) {
            return null;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (int ii = index - period.intValue() + 1; ii <= index; ii++) {
            result = result.add(ifc.getClose(ii)); // close
        }
        return result.divide(period, round, RoundingMode.HALF_UP);
    }

    final protected BigDecimal ONE = new BigDecimal(1);
    final protected BigDecimal TWO = new BigDecimal(2);

    /**
     * 正規分布を計算
     * @param list
     * @param index
     * @param period
     * @param sigma
     * @param round
     * @return
     */
    final protected BigDecimal calcBoll(final Ifohlc ifc, final int index, final int period, final int sigma, final int round) {
        SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
        for (int ii = index - period + 1; ii <= index; ii++) {
            stats.addValue(ifc.getClose(ii).doubleValue());
        }
        return new BigDecimal(FastMath.sqrt(stats.getPopulationVariance())).setScale(round, RoundingMode.HALF_UP);
    }

}
