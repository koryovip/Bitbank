package mng;

public class KROrder {

    final public long orderId;
    public boolean watch = false;

    public KROrder(long orderId) {
        this.orderId = orderId;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (orderId ^ (orderId >>> 32));
        return result;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KROrder other = (KROrder) obj;
        if (orderId != other.orderId)
            return false;
        return true;
    }

}
