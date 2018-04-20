package pubnub;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import pubnub.json.transactions.Transaction;
import utils.OtherUtil;

public class TransMonitor extends BBReal {

    private final TransMonitorUpdater updater;

    public TransMonitor(final TransMonitorUpdater updater) {
        this.updater = updater;
    }

    @Override
    protected void onError(PubNub pubnub, PNMessageResult message, Throwable t) {
        t.printStackTrace();
    }

    //List<Transaction> buy_List = new ArrayList<Transaction>();
    private long buyCount = 0;
    private BigDecimal buyTotal = BigDecimal.ZERO;
    //List<Transaction> sellList = new ArrayList<Transaction>();
    private long sellCount = 0;
    private BigDecimal sellTotal = BigDecimal.ZERO;

    protected void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.transactions.Message hoge) {
        for (Transaction row : hoge.data.transactions) {
            if (row.side.equals("buy")) {
                buyCount++;
                buyTotal = buyTotal.add(row.amount.multiply(row.price));
                //buy_List.add(row);
            } else if (row.side.equals("sell")) {
                sellCount++;
                sellTotal = sellTotal.add(row.amount.multiply(row.price));
                //sellList.add(row);
            }
        }
        if (this.updater == null) {
            System.out.println(String.format("%10d\t%10s\t|\t%10d\t%10s", buyCount, OtherUtil.me().scale(buyTotal, 1).toPlainString(), sellCount, OtherUtil.me().scale(sellTotal, 1).toPlainString()));
        } else {
            this.updater.update(buyCount, buyTotal, sellCount, sellTotal);
        }
    };

    @Override
    protected List<String> channels() {
        return Arrays.asList(super.getFullChannelName(KRPubNubChannel.transactions, "xrp_jpy"));
    }

    public static void main(String[] args) {
        new TransMonitor(null).monitor();
    }
}
