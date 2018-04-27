package auto3;

import java.math.BigDecimal;

import gui.tablemodel.ColumnContext;
import gui.tablemodel.KRTableModel;
import utils.DateUtil;

public class Candle15MTableModel extends KRTableModel {

    private static final long serialVersionUID = -834624943297710557L;

    public Candle15MTableModel() {
        super(new ColumnContext[] { //
                new ColumnContext("No", Integer.class, false, 50), //
                new ColumnContext("Time", String.class, false, 90), //
                new ColumnContext("Open", BigDecimal.class, false, 80), //
                new ColumnContext("High", BigDecimal.class, false, 80), //
                new ColumnContext("Low", BigDecimal.class, false, 80), //
                new ColumnContext("Close", BigDecimal.class, false, 80), //
                //
                new ColumnContext("MA(15M)", BigDecimal.class, false, 80), //
                new ColumnContext("MA(1H)", BigDecimal.class, false, 80), //
                new ColumnContext("MA(4H)", BigDecimal.class, false, 80), //
                new ColumnContext("MA(1D)", BigDecimal.class, false, 80), // order date
                //
                new ColumnContext("*MA(15M)", BigDecimal.class, false, 80), //
                new ColumnContext("*MA(1H)", BigDecimal.class, false, 80), //
                new ColumnContext("*MA(4H)", BigDecimal.class, false, 80), //
                new ColumnContext("*MA(1D)", BigDecimal.class, false, 80), // order date
                //
                new ColumnContext("MA", String.class, false, 40), // 距離
                new ColumnContext("C-O", BigDecimal.class, false, 60), // COL_INDEX_PROFIT_NORMAL
                new ColumnContext("Up/Down", String.class, false, 40), // COL_INDEX_PROFIT_BY_TS
                new ColumnContext("買い？", String.class, false, 40), // COL_INDEX_TRALINGSTOP
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
                , row.ma_20_15M //
                , row.ma_20_1H //
                , row.ma_20_4H //
                , row.ma_20_1D //
                , row.dma_20_15M //
                , row.dma_20_1H //
                , row.dma_20_4H //
                , row.dma_20_1D //
                , row.checkMA ? "〇" : "×" //
                , row.closeOpenDiff //
                , row.isUp ? "↑" : "↓" //
                , row.buy9 ? "買" : "―" //
                // 隠し
                , row.openTime //
        });
    }

    private final int COL_INX_OPT = 18;

    public void updRow(Candle15M row) {
        int rows = super.getRowCount();
        for (int ii = rows - 1; ii >= 0; ii--) {
            long openTime = (long) super.getValueAt(ii, COL_INX_OPT);
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
        super.setValueAt(row.ma_20_15M, ii, colIndex++);
        super.setValueAt(row.ma_20_1H, ii, colIndex++);
        super.setValueAt(row.ma_20_4H, ii, colIndex++);
        super.setValueAt(row.ma_20_1D, ii, colIndex++);
        super.setValueAt(row.dma_20_15M, ii, colIndex++);
        super.setValueAt(row.dma_20_1H, ii, colIndex++);
        super.setValueAt(row.dma_20_4H, ii, colIndex++);
        super.setValueAt(row.dma_20_1D, ii, colIndex++);
        super.setValueAt(row.checkMA ? "〇" : "×", ii, colIndex++);
        super.setValueAt(row.closeOpenDiff, ii, colIndex++);
        super.setValueAt(row.isUp ? "↑" : "↓", ii, colIndex++);
        super.setValueAt(row.buy9 ? "買" : "―", ii, colIndex++);
    }
}
