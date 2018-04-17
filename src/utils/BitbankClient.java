package utils;

import cc.Config;
import cc.bitbank.Bitbankcc;

public class BitbankClient {
    private static BitbankClient singleton = new BitbankClient();

    public static BitbankClient me() {
        return singleton;
    }

    public final Bitbankcc bb;

    private BitbankClient() {
        this.bb = new Bitbankcc();
        this.bb.setKey(Config.me().getApiKey(), Config.me().getApiSecret());
    }

}
