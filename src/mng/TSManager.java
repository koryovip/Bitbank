package mng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gui.TS;

public class TSManager {
    private Logger logger = LogManager.getLogger();
    private static final TSManager singleton = new TSManager();

    public static TSManager me() {
        return singleton;
    }

    private TSManager() {

    }

    final private List<TS> tsList = new ArrayList<TS>();

    final synchronized public TS addOrUpdateTs(final long orderId, final BigDecimal bought, final BigDecimal amount, final BigDecimal tralingStop) {
        for (TS ts : tsList) {
            if (ts.orderId == orderId) {
                ts.resetTralingStop(tralingStop);
                logger.debug("ResetTralingStop:" + tralingStop);
                return ts;
            }
        }
        TS ts = new TS(orderId, bought, amount, BigDecimal.ZERO, tralingStop);
        this.tsList.add(ts);
        return ts;
    }

    final synchronized public TS addOrUpdateLc(final long orderId, final BigDecimal bought, final BigDecimal amount, final BigDecimal lossCut) {
        for (TS ts : tsList) {
            if (ts.orderId == orderId) {
                ts.resetLostCut(lossCut);
                logger.debug("ResetLossCut:" + lossCut);
                return ts;
            }
        }
        TS ts = new TS(orderId, bought, amount, lossCut, BigDecimal.ZERO);
        this.tsList.add(ts);
        return ts;
    }

    final synchronized public boolean remove(final long orderId) {
        Iterator<TS> it = tsList.iterator();
        while (it.hasNext()) {
            TS ts = it.next();
            if (orderId == ts.orderId) {
                it.remove();
                logger.debug("TS removed:" + orderId);
                return true;
            }
        }
        return false;
    }

    final public int size() {
        return this.tsList.size();
    }

    final public void handle(TSHandler handle) {
        for (final TS ts : tsList) {
            handle.handle(ts);
        }
    }
}
