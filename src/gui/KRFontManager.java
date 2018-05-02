package gui;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JFrame;

public class KRFontManager {
    private static final KRFontManager singleton = new KRFontManager();

    public static KRFontManager me() {
        return singleton;
    }

    private final int width;
    private final int height;
    private int fontSize = 14;
    private final Font font;
    private int strWidth;

    private KRFontManager() {
        // (a) 画面の解像度の取得方法
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.DisplayMode displayMode = env.getDefaultScreenDevice().getDisplayMode();
        // 変数widthとheightに画面の解像度の幅と高さを代入
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        //System.out.println(width);
        //System.out.println(height);

        // (b) デスクトップのサイズの取得方法
        // 変数desktopBoundsにデスクトップ領域を表すRectangleが代入される
        // java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        // (a)でタスクトレイも含んだ画面全体のサイズが取得できる。(b)は、タスクトレイを除いたデスクトップの領域が取得できる。

        if (width < 3000) {
            fontSize = 14;
        } else {
            fontSize = 20;
        }

        font = new Font("MS Gothic", Font.PLAIN, fontSize);
        // FontRenderContext frc = new FontRenderContext(null, true, true);
        //FontMetrics fontMetrics = new FontMetrics(font);
    }

    public void init(final JFrame p) {
        FontMetrics fontMetrics = p.getFontMetrics(font);
        strWidth = fontMetrics.stringWidth("W") + 1;
        // System.out.println(strWidth);
    }

    public Font font() {
        return this.font;
    }

    public int fontSize() {
        return this.fontSize;
    }

    public int stringWidth() {
        return this.strWidth;
    }

    public int columnWidth(int charCount) {
        return this.strWidth * charCount;
    }
}
