package mng;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.OtherUtil;
import utils.OtherUtil.LongRapple;

public class OrderManager {
    private Logger logger = LogManager.getLogger();
    private static final OrderManager singleton = new OrderManager();

    public static OrderManager me() {
        return singleton;
    }

    private OrderManager() {
        this.add(35854854);
        this.add(35995255);
        this.add(35993844);
        this.add(35937748);
        this.add(35968526);
        this.add(35915883);
    }

    final private List<KROrder> orderList = new ArrayList<KROrder>();

    public long[] getOrderIds() {
        int size = this.size();
        logger.debug(size);
        if (size <= 0) {
            return new long[] {};
        }
        return OtherUtil.me().toArray(orderList, new LongRapple<KROrder>() {
            @Override
            public long getValue(KROrder t) {
                return t.orderId;
            }
        });
    }

    final synchronized public void add(final long orderId) {
        KROrder order = new KROrder(orderId);
        if (!orderList.contains(order)) {
            orderList.add(order);
        }
    }

    final public int size() {
        return this.orderList.size();
    }

    //    final synchronized public void remove(final long orderId) {
    //        Iterator<KROrder> it = orderList.iterator();
    //        while (it.hasNext()) {
    //            KROrder order = it.next();
    //            if (orderId == order.orderId) {
    //                it.remove();
    //                logger.debug("removed:" + orderId);
    //            }
    //        }
    //    }

    //    final synchronized public void needWatch9(final long orderId) {
    //
    //    }
    //
    //    final synchronized public void watch(final long orderId) {
    //        Iterator<KROrder> it = orderList.iterator();
    //        while (it.hasNext()) {
    //            KROrder order = it.next();
    //            if (orderId == order.orderId) {
    //                order.watch = true;
    //            }
    //        }
    //    }

    //    public static void main(String[] args) {
    //        // OrderManager.me().remove(null);
    //        OrderManager.me().remove(1L);
    //        OrderManager.me().remove(30588498L);
    //        for (long orderId : OrderManager.me().getOrderIds()) {
    //            System.out.println(orderId);
    //        }
    //    }

}
