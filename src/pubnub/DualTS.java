package pubnub;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

public class DualTS extends BBReal {

    private Logger logger = LogManager.getLogger();

    @Override
    protected void onError(PubNub pubnub, PNMessageResult message, Throwable t) {
        t.printStackTrace();
    }

    @Override
    protected List<String> channels() {
        return Arrays.asList(super.getFullChannelName(KRPubNubChannel.ticker, "xrp_jpy"));
    }

    final private BigDecimal fixBuyPrice = new BigDecimal("100"); // 買いたい価格
    final private BigDecimal fixBuyTS = new BigDecimal("0.10"); // 買いたい価格の調整幅
    private BigDecimal boughtPrice = fixBuyPrice; // 買値
    private BigDecimal sellPrice = BigDecimal.ZERO; // 売値
    final private BigDecimal fixSellTS = new BigDecimal("0.10"); // 売値調整幅

    private State state = State.Init;

    private enum State {
        Init, CanBuy, BuyTS, Bought, SellTS, End;
    }

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.ticker.Message hoge) {
        logger.debug("{}\t{}", hoge.data.buy, hoge.data.sell);
        if (state == State.Init) {
            // 売り価格は買いたい価格より下回った場合、買い可能な状態にする。
            if (hoge.data.sell.compareTo(fixBuyPrice) < 0) {
                logger.debug("買い可能になった");
                state = State.CanBuy;
            }
        } else if (state == State.CanBuy || state == State.BuyTS) {
            // 買い可能な状態で、売り価格は、買価上回った場合、即買い
            if (hoge.data.sell.compareTo(boughtPrice) > 0) {
                if (state != State.BuyTS) {
                    // 1回もTPしなかったら、買わない？
                    return;
                }
                logger.debug(boughtPrice.toPlainString() + "で買い！");
                sellPrice = boughtPrice;
                state = State.Bought;
            } else {
                // 買値と売値の距離を測る
                BigDecimal tmp = hoge.data.sell.add(fixBuyTS);
                if (tmp.compareTo(boughtPrice) < 0) {
                    logger.debug("買値を " + boughtPrice + " → " + tmp + " に更新！");
                    boughtPrice = tmp;
                    state = State.BuyTS;
                }
            }
        } else if (state == State.Bought || state == State.SellTS) {
            if (hoge.data.buy.compareTo(sellPrice) <= 0) {
                if (state != State.SellTS) {
                    // 1回もTPしなかったら、売らない？
                    return;
                }
                logger.debug(sellPrice.toPlainString() + "で売り！");
                state = State.End;
            } else {
                BigDecimal tmp = hoge.data.buy.subtract(fixSellTS);
                if (tmp.compareTo(sellPrice) > 0) {
                    logger.debug("売値を " + sellPrice + " → " + tmp + " に更新！");
                    sellPrice = tmp;
                    state = State.SellTS;
                }
            }
        } else if (state == State.End) {
            logger.debug("１サイクル終了。再起動してください。");
            // state = State.Init;
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new DualTS().monitor();
    }

}
