package pubnub;

public enum KRPubNubChannel {

    candlestick("candlestick"), ticker("ticker"), depth("depth"), transactions("transactions");

    private final String channel;

    KRPubNubChannel(final String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

}
