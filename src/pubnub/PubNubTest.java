package pubnub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import pubnub.json.candlestick.Message;
import utils.DateUtil;

public class PubNubTest {

    private static PubNubTest singleton = new PubNubTest();

    public static PubNubTest me() {
        return singleton;
    }

    private final PubNub pubnub;

    private PubNubTest() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-e12e9174-dd60-11e6-806b-02ee2ddab7fe");
        // pnConfiguration.setPublishKey("candlestick_btc_jpy");
        pnConfiguration.setSecure(false);

        pubnub = new PubNub(pnConfiguration);

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() != null) {
                    switch (status.getOperation()) {
                    // let's combine unsubscribe and subscribe handling for ease of use
                    case PNSubscribeOperation:
                    case PNUnsubscribeOperation:
                        // note: subscribe statuses never have traditional
                        // errors, they just have categories to represent the
                        // different issues or successes that occur as part of subscribe
                        switch (status.getCategory()) {
                        case PNConnectedCategory:
                            // this is expected for a subscribe, this means there is no error or issue whatsoever
                        case PNReconnectedCategory:
                            // this usually occurs if subscribe temporarily fails but reconnects. This means
                            // there was an error but there is no longer any issue
                        case PNDisconnectedCategory:
                            // this is the expected category for an unsubscribe. This means there
                            // was no error in unsubscribing from everything
                        case PNUnexpectedDisconnectCategory:
                            // this is usually an issue with the internet connection, this is an error, handle appropriately
                        case PNAccessDeniedCategory:
                            // this means that PAM does allow this client to subscribe to this
                            // channel and channel group configuration. This is another explicit error
                        default:
                            // More errors can be directly specified by creating explicit cases for other
                            // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                        }

                    case PNHeartbeatOperation:
                        // heartbeat operations can in fact have errors, so it is important to check first for an error.
                        // For more information on how to configure heartbeat notifications through the status
                        // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                        if (status.isError()) {
                            // There was an error with the heartbeat operation, handle here
                        } else {
                            // heartbeat operation was successful
                        }
                    default: {
                        // Encountered unknown status type
                    }
                    }
                } else {
                    // After a reconnection see status.getCategory()
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // System.out.println(message);
                try {
                    // System.out.println(message.getMessage().toString());
                    Message hoge = mapper.readValue(message.getMessage().toString(), Message.class);
                    // System.out.println(hoge);
                    // System.out.println(String.format("%s %s %s", DateUtil.me().format1(hoge.datetime()), hoge.open(), hoge.close()));
                    // Date dt = new Date(hoge.datetime());
                    final Long datetime = hoge.datetime(hoge.type1min());
                    if (datetime == null) {
                        return;
                    }
                    cl.setTimeInMillis(datetime);
                    int mm = cl.get(Calendar.MINUTE);
                    if (mm == 0 || mm == 15 || mm == 30 || mm == 45) {
                        // buy
                        if (hold.compareTo(BigDecimal.ZERO) > 0) {
                            return;
                        }
                        if (holding) {
                            return;
                        }
                        final BigDecimal open = hoge.open(hoge.type15min());
                        hold = amount.divide(open, 3, RoundingMode.DOWN);
                        amount = BigDecimal.ZERO;
                        holding = true;
                        System.out.println(String.format("%s \t buy \t %s \t (%s)", DateUtil.me().format1(datetime), hold, open));
                    } else if (mm == 14 || mm == 29 || mm == 44 || mm == 59) {
                        // sell
                        if (hold.compareTo(BigDecimal.ZERO) <= 0) {
                            return;
                        }
                        if (!holding) {
                            return;
                        }
                        final BigDecimal close = hoge.close(hoge.type15min());
                        amount = hold.multiply(close);
                        hold = BigDecimal.ZERO;
                        holding = false;
                        System.out.println(String.format("%s \t sell \t %s \t (%s)", DateUtil.me().format1(datetime), amount, close));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    private Calendar cl = Calendar.getInstance();
    private boolean holding = false;
    private BigDecimal amount = new BigDecimal(100000);
    private BigDecimal hold = new BigDecimal(0);

    private final ObjectMapper mapper = new ObjectMapper();

    public void watchChannel() {
        pubnub.subscribe().channels(Arrays.asList("candlestick_xrp_jpy")) // subscribe to channels
                .execute();
    }

    public static void main(String[] args) {
        PubNubTest.me().watchChannel();
    }

}
