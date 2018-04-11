package pubnub;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public abstract class BBReal {

    protected abstract void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.candlestick.Message hoge);

    protected abstract void onMessage(PubNub pubnub, PNMessageResult message, pubnub.json.ticker.Message hoge);

    protected abstract void onError(PubNub pubnub, PNMessageResult message, Throwable t);

    private final ObjectMapper mapper = new ObjectMapper();

    final public void monitor() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-e12e9174-dd60-11e6-806b-02ee2ddab7fe");
        // pnConfiguration.setPublishKey("candlestick_btc_jpy");
        pnConfiguration.setSecure(false);

        PubNub pubnub = new PubNub(pnConfiguration);

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
                try {
                    // System.out.println(message);
                    if ("candlestick_xrp_jpy".equals(message.getChannel())) {
                        pubnub.json.candlestick.Message hoge = mapper.readValue(message.getMessage().toString(), pubnub.json.candlestick.Message.class);
                        onMessage(pubnub, message, hoge);
                    } else if ("ticker_xrp_jpy".equals(message.getChannel())) {
                        pubnub.json.ticker.Message hoge = mapper.readValue(message.getMessage().toString(), pubnub.json.ticker.Message.class);
                        onMessage(pubnub, message, hoge);
                    }
                } catch (Throwable t) {
                    onError(pubnub, message, t);
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubnub.subscribe().channels(Arrays.asList("candlestick_xrp_jpy", "ticker_xrp_jpy")).execute();
    }

    public static void sleeeeeep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
