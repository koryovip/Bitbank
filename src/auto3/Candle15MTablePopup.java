package auto3;

import java.util.Calendar;

import javax.swing.JMenuItem;
import javax.swing.JTable;

import gui.popup.KRTablePopupMenu;

public class Candle15MTablePopup extends KRTablePopupMenu<Candle15MTableModel> {

    private static final long serialVersionUID = 3787050326852654095L;

    private final JMenuItem calc;

    public Candle15MTablePopup(JTable table) {
        super(table);
        calc = add("GetDB");
        super.addAction(calc, new JMenuItemAction<Candle15MTableModel>() {
            @Override
            public void menuClicked(JTable table, Candle15MTableModel model, int selectedRow) {
                System.out.println(model.getOpenTime(selectedRow));
                Calendar cal1 = Calendar.getInstance();
                cal1.setTimeInMillis(model.getOpenTime(selectedRow));
                Candle15MForm.me().getDB(cal1.getTime());
            }
        });
    }

    @Override
    public void show(JTable table, Candle15MTableModel model) {
        calc.setEnabled(table.getSelectedRowCount() == 1);
    }

}
