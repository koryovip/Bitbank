package gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StripeTableRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -6995286170275706604L;
    private static final Color EVEN_COLOR = new Color(240, 240, 255);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(row % 2 == 1 ? EVEN_COLOR : table.getBackground());
        }
        final boolean isNumber = (value instanceof Number);
        setHorizontalAlignment(isNumber ? RIGHT : LEFT);
        if (isNumber) {
            Number n = (Number) value;
            if (n.doubleValue() < 0) {
                setForeground(Color.RED);
            } else {
                //setForeground(table.getSelectionForeground());
            }
        }
        return this;
    }
}