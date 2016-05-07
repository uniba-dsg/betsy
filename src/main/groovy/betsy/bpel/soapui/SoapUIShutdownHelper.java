package betsy.bpel.soapui;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.support.http.SoapUIMultiThreadedHttpConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public final class SoapUIShutdownHelper {

    private SoapUIShutdownHelper() {}

    public static void shutdownSoapUIForReal() {
        SoapUI.shutdown();
        SoapUI.getSoapUITimer().cancel();
        JFrame frame = SoapUI.getFrame();
        if (frame != null) {
            frame.dispose();
        }
        SoapUI.getThreadPool().shutdownNow();
        try {
            SoapUI.getThreadPool().awaitTermination(5l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SoapUI.shutdown();

        for (Window w : Frame.getWindows()) {
            w.dispose();
        }
        for (Frame w : Frame.getFrames()) {
            w.dispose();
        }

        // Now to shutdown the monitor thread setup by SoapUI
        Thread[] tarray = new Thread[Thread.activeCount()];
        Thread.enumerate(tarray);
        for (Thread t : tarray) {
            if (t instanceof SoapUIMultiThreadedHttpConnectionManager.IdleConnectionMonitorThread) {
                ((SoapUIMultiThreadedHttpConnectionManager.IdleConnectionMonitorThread) t)
                        .shutdown();
            }
        }
    }
}
