package gui.tablemodel;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public abstract class KRTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 4763042087092219123L;

    final protected ColumnContext[] COLUMN_ARRAY;

    public KRTableModel(ColumnContext[] COLUMN_ARRAY) {
        this.COLUMN_ARRAY = COLUMN_ARRAY;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return COLUMN_ARRAY[col].isEditable;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_ARRAY[column].columnClass;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_ARRAY.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_ARRAY[column].columnName;
    }

    final public void initColumnSize(TableColumnModel tableColumnModel) {
        int index = 0;
        for (ColumnContext context : COLUMN_ARRAY) {
            TableColumn tableColumn = tableColumnModel.getColumn(index++);
            if (context.columnWidth == 0) {
                tableColumn.setMinWidth(0);
            } else {
                tableColumn.setMinWidth(10);
            }
            tableColumn.setPreferredWidth(context.columnWidth);
            /*if (index == COL_INDEX_NO) {
                tableColumn.setWidth(context.columnWidth);
            }*/
        }
    }

    final public void _clear() {
        super.setRowCount(0);
    }

    public final int getHorizontalAlignment(int column) {
        return COLUMN_ARRAY[column].horizontalAlignment;
    }
}
