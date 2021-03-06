package auto3;

import java.math.BigDecimal;

import javax.swing.SwingConstants;

import gui.KRFontManager;
import gui.tablemodel.ColumnContext;
import gui.tablemodel.KRTableModel;
import utils.DateUtil;

public class Candle15MTableModel extends KRTableModel {

    private static final long serialVersionUID = -834624943297710557L;

    public Candle15MTableModel() {
        super(new ColumnContext[] { //
                new ColumnContext("No", Integer.class, false, KRFontManager.me().columnWidth(5)), //
                new ColumnContext("Time", String.class, false, KRFontManager.me().columnWidth(12)), //
                new ColumnContext("Open", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("High", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("Low", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("Close", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                // MA
                new ColumnContext("MA(15M)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("MA(1H)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("MA(4H)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("MA(1D)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), // order date
                // MA-Calc
                new ColumnContext("*MA(15M)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("*MA(1H)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("*MA(4H)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), //
                new ColumnContext("*MA(1D)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), // order date
                // BB
                new ColumnContext("*BB(L2)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), // BB 20 -2
                new ColumnContext("*BB(H2)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), // BB 20 +2
                new ColumnContext("*BB(H-L)", BigDecimal.class, false, KRFontManager.me().columnWidth(8)), // BB 20 +2 - -2
                //
                new ColumnContext("MA", String.class, false, KRFontManager.me().columnWidth(3), SwingConstants.CENTER), // 距離
                new ColumnContext("C-O", BigDecimal.class, false, KRFontManager.me().columnWidth(6)), // COL_INDEX_PROFIT_NORMAL
                new ColumnContext("Up/Down", String.class, false, KRFontManager.me().columnWidth(3), SwingConstants.CENTER), // COL_INDEX_PROFIT_BY_TS
                new ColumnContext("買", String.class, false, KRFontManager.me().columnWidth(3), SwingConstants.CENTER), // COL_INDEX_TRALINGSTOP
                // 隠し
                new ColumnContext("OPENTIME", Long.class, false, 0), // COL_INDEX_TRALINGSTOP
        });
    }

    private int rowIndex = 1;

    synchronized public final void clear() {
        this.rowIndex = 1;
        super._clear();
    }

    public void addRow(Candle15M row) {
        super.addRow(new Object[] { rowIndex++ //
        , DateUtil.me().format0(row.getOpenTimeDt()) //
        , row.open //
        , row.high //
        , row.low //
        , row.close //
        , row.ma_15M //
        , row.ma_1H //
        , row.ma_4H //
        , row.ma_1D //
        , row.dma_15M //
        , row.dma_1H //
        , row.dma_4H //
        , row.dma_1D //
        , row.bb_low2 //
        , row.bb_high2 //
        , row.bb_high2.subtract(row.bb_low2) //
        , row.checkMA ? "〇" : "×" //
        , row.closeOpenDiff //
        , row.isUp ? "↑" : "↓" //
        , row.buy9 ? "買" : "―" //
                // 隠し
                , row.openTime //
        });
    }

    private final int COL_INX_OPT = 21;

    final public long getOpenTime(int row) {
        return (long) super.getValueAt(row, COL_INX_OPT);
    }

    public void updRow(Candle15M row) {
        int rows = super.getRowCount();
        for (int ii = rows - 1; ii >= 0; ii--) {
            long openTime = getOpenTime(ii); //(long) super.getValueAt(ii, COL_INX_OPT);
            if (row.openTime == openTime) {
                // System.out.println("Find Data at " + ii);
                updateValue(row, ii);
                break;
            }
        }
    }

    private void updateValue(final Candle15M row, final int ii) {
        int colIndex = 2;
        super.setValueAt(row.open, ii, colIndex++);
        super.setValueAt(row.high, ii, colIndex++);
        super.setValueAt(row.low, ii, colIndex++);
        super.setValueAt(row.close, ii, colIndex++);
        super.setValueAt(row.ma_15M, ii, colIndex++);
        super.setValueAt(row.ma_1H, ii, colIndex++);
        super.setValueAt(row.ma_4H, ii, colIndex++);
        super.setValueAt(row.ma_1D, ii, colIndex++);
        super.setValueAt(row.dma_15M, ii, colIndex++);
        super.setValueAt(row.dma_1H, ii, colIndex++);
        super.setValueAt(row.dma_4H, ii, colIndex++);
        super.setValueAt(row.dma_1D, ii, colIndex++);
        super.setValueAt(row.bb_low2, ii, colIndex++);
        super.setValueAt(row.bb_high2, ii, colIndex++);
        super.setValueAt(row.bb_high2.subtract(row.bb_low2), ii, colIndex++);
        super.setValueAt(row.checkMA ? "〇" : "×", ii, colIndex++);
        super.setValueAt(row.closeOpenDiff, ii, colIndex++);
        super.setValueAt(row.isUp ? "↑" : "↓", ii, colIndex++);
        super.setValueAt(row.buy9 ? "買" : "―", ii, colIndex++);
    }
}
