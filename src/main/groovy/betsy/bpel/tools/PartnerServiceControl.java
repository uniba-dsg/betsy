package betsy.bpel.tools;

import betsy.bpel.ws.DummyAndRegularTestPartnerService;
import betsy.bpel.ws.TestPartnerService;
import betsy.bpel.ws.TestPartnerServicePublisherInternalDummy;

import javax.swing.*;
import java.awt.*;

/**
 * GUI to start and stop / start and shutdown the partner service.
 */
public class PartnerServiceControl extends JFrame {

    public static void main(String[] args) {
        new PartnerServiceControl().setVisible(true);
    }

    private final TestPartnerService publisher = new DummyAndRegularTestPartnerService();

    public PartnerServiceControl() {
        layoutFrame();

        createStartButton();

        createStopButton();
    }

    private void layoutFrame() {
        this.setLayout(new FlowLayout());
        this.setSize(300, 75);
        this.setTitle("Partner Service Control Center");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createStartButton() {
        JButton start = new JButton("startup");
        start.addActionListener(e -> publisher.startup());
        this.add(start);
    }

    private void createStopButton() {
        JButton stop = new JButton("stop");
        stop.addActionListener(e -> publisher.shutdown());
        this.add(stop);
    }


}
