package mng;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    final synchronized public TS addOrUpdate(final long orderId, final BigDecimal bought, final BigDecimal amount, final BigDecimal lostCut, final BigDecimal tralingStop) {
        for (TS ts : tsList) {
            if (ts.orderId == orderId) {
                ts.resetTralingStop(tralingStop);
                logger.debug("ResetTralingStop:" + tralingStop);
                return ts;
            }
        }
        TS ts = new TS(orderId, bought, amount, lostCut, tralingStop);
        this.tsList.add(ts);
        return ts;
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