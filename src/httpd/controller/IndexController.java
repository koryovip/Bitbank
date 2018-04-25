package httpd.controller;

import java.util.Calendar;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import utils.DateUtil;

public class IndexController extends Controller {
    public void index() {
        super.renderTemplate("index.html");
    }

    public void getData() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2018, 4 - 1, 20, 0, 0, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        System.out.println(DateUtil.me().format1(cal1.getTime()));
        System.out.println(cal1.getTimeInMillis());
        List<Record> records = Db.find(Db.getSql("candles"), 60.0, cal1.getTimeInMillis(), cal1.getTimeInMillis());
        super.renderJson("records", records);
    }
}
