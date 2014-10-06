package betsy.tool;

import betsy.executables.ws.TestPartnerServicePublisherInternal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI to start and stop / publish and unpublish the partner service.
 */
public class PartnerServiceControl extends JFrame {

    public static void main(String[] args) {
        new PartnerServiceControl().setVisible(true);
    }

    private final TestPartnerServicePublisherInternal publisher;

    public PartnerServiceControl() {
        layoutFrame();
        publisher = new TestPartnerServicePublisherInternal();

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
        start.addActionListener(e -> publisher.publish());
        this.add(start);
    }

    private void createStopButton() {
        JButton stop = new JButton("stop");
        stop.addActionListener(e -> publisher.unpublish());
        this.add(stop);
    }


}
