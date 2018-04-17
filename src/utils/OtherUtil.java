package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OtherUtil {
    private static OtherUtil singleton = new OtherUtil();

    public static OtherUtil me() {
        return singleton;
    }

    public <T> T firstItem(List<T> list) {
        if (list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    public <T> T lastItem(List<T> list) {
        if (list.size() <= 0) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public void testRoundingMode() {
        BigDecimal a = new BigDecimal("3.14561");
        int round = 4;
        System.out.println(a.setScale(round, RoundingMode.CEILING)); // 切り上げ（正の無限大に近づくように丸める）
        System.out.println(a.setScale(round, RoundingMode.DOWN)); // 切り捨て（0に近づくように丸める）
        System.out.println(a.setScale(round, RoundingMode.FLOOR)); // 切り捨て（負の無限大に近づくように丸める）
        System.out.println(a.setScale(round, RoundingMode.HALF_DOWN)); // 五捨六入（もっとも近い数字に丸める）
        System.out.println(a.setScale(round, RoundingMode.HALF_EVEN)); // 偶数の場合はHALF_DOWN、奇数の場合はHALF_UPのように動作する（もっとも近い数字に丸める）
        System.out.println(a.setScale(round, RoundingMode.HALF_UP)); // 四捨五入（もっとも近い数字に丸める）
        System.out.println(a.setScale(round, RoundingMode.UP)); // 切り上げ（0から離れるように丸める）
        // System.out.println(a.setScale(round, RoundingMode.UNNECESSARY)); // 何もしない（丸めが必要でないことを表す）
    }
}
