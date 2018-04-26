package gui.tablemodel;

public final class ColumnContext {
    public final String columnName;
    public final Class<?> columnClass;
    public final boolean isEditable;
    public final int columnWidth;

    public ColumnContext(String columnName, Class<?> columnClass, boolean isEditable, int columnWidth) {
        this.columnName = columnName;
        this.columnClass = columnClass;
        this.isEditable = isEditable;
        this.columnWidth = columnWidth;
    }
}
