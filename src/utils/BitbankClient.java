package utils;

import cc.Config;
import cc.bitbank.Bitbankcc;

public class BitbankClient {
    private static BitbankClient singleton = new BitbankClient();

    public static BitbankClient me() {
        return singleton;
    }

    public final Bitbankcc bbR;
    public final Bitbankcc bbW;

    private BitbankClient() {
        this.bbR = new Bitbankcc();
        this.bbR.setKey(Config.me().getApiKeyR(), Config.me().getApiSecretR());

        this.bbW = new Bitbankcc();
        this.bbW.setKey(Config.me().getApiKeyW(), Config.me().getApiSecretW());
    }

}
