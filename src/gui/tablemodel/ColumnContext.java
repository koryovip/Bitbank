package gui.tablemodel;

import javax.swing.SwingConstants;

public final class ColumnContext {
    public final String columnName;
    public final Class<?> columnClass;
    public final boolean isEditable;
    public final int columnWidth;
    public final int horizontalAlignment;

    public ColumnContext(String columnName, Class<?> columnClass, boolean isEditable, int columnWidth) {
        this(columnName, columnClass, isEditable, columnWidth, SwingConstants.LEFT);
    }

    /**
     * @param columnName
     * @param columnClass
     * @param isEditable
     * @param columnWidth
     * @param horizontalAlignment SwingConstants.LEFT, RIGHT, CENTER
     */
    public ColumnContext(String columnName, Class<?> columnClass, boolean isEditable, int columnWidth, int horizontalAlignment) {
        this.columnName = columnName;
        this.columnClass = columnClass;
        this.isEditable = isEditable;
        this.columnWidth = columnWidth;
        this.horizontalAlignment = horizontalAlignment;
    }

}
