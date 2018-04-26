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
                new ColumnContext("Time", String.class, false, 100), //
                new ColumnContext("Open", BigDecimal.class, false, 90), //
                new ColumnContext("High", BigDecimal.class, false, 90), //
                new ColumnContext("Low", BigDecimal.class, false, 90), //
                new ColumnContext("Close", BigDecimal.class, false, 90), //
                //
                new ColumnContext("MA20(15M)", BigDecimal.class, false, 90), //
                new ColumnContext("MA80(1H)", BigDecimal.class, false, 90), //
                new ColumnContext("MA320(4H)", BigDecimal.class, false, 90), //
                new ColumnContext("MA1920(1D)", BigDecimal.class, false, 90), // order date
                //
                new ColumnContext("MA20(15M)", BigDecimal.class, false, 90), //
                new ColumnContext("MA80(1H)", BigDecimal.class, false, 90), //
                new ColumnContext("MA320(4H)", BigDecimal.class, false, 90), //
                new ColumnContext("MA1920(1D)", BigDecimal.class, false, 90), // order date
                //
                new ColumnContext("MA", String.class, false, 60), // 距離
                new ColumnContext("C-O", BigDecimal.class, false, 60), // COL_INDEX_PROFIT_NORMAL
                new ColumnContext("Up/Down", String.class, false, 60), // COL_INDEX_PROFIT_BY_TS
                new ColumnContext("買い？", String.class, false, 60), // COL_INDEX_TRALINGSTOP
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
        });
    }
}
