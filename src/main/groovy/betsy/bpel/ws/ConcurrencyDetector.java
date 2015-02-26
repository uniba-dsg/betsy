package betsy.bpel.ws;

import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Detects concurrency from the perspective of the partner service.
 * <p>
 * This is done by checking whether the partner service can detect multiple parallel pending requests.
 * A pending request has already sent its request to the partner service, but the caller is still waiting for the reply to arrive.
 * During this time, another request may enter the partner service.
 * If this is the case, we can be sure that the calls were done in parallel, as the caller has to wait for the response normally.
 */
public class ConcurrencyDetector {

    private static final Logger log = Logger.getLogger(ConcurrencyDetector.class);

    // value in ms
    public static final int CONCURRENCY_TIMEOUT = 1000;

    // only for internal use
    private final AtomicInteger concurrentAccesses = new AtomicInteger(0);

    // these two are observable from the outside
    private final AtomicInteger totalConcurrentAccesses = new AtomicInteger(0);
    private final AtomicInteger totalAccesses = new AtomicInteger(0);

    /**
     * resets all counters, as if the instance would have been created anew
     */
    public void reset() {
        concurrentAccesses.set(0);
        totalConcurrentAccesses.set(0);
        totalAccesses.set(0);
    }

    public int access() {
        log.info("call to detect concurrency");

        // access detected
        totalAccesses.incrementAndGet();

        //magic number for tracking concurrent accesses
        concurrentAccesses.incrementAndGet();
        int result = -1;
        try {
            WaitTasks.sleep(CONCURRENCY_TIMEOUT);

            if (isAccessedConcurrently()) {
                log.info("no concurrency detected");
                result = 0;
            } else {
                log.info("concurrency detected");
                result = 100;
                totalConcurrentAccesses.incrementAndGet();
            }
        } finally {
            concurrentAccesses.decrementAndGet();
        }

        return result;

    }

    public boolean isAccessedConcurrently() {
        return concurrentAccesses.get() == 1;
    }

    public int getNumberOfCalls() {
        return totalAccesses.get();
    }

    public int getNumberOfConcurrentCalls() {
        return totalConcurrentAccesses.get();
    }

}
