package betsy.tool;

import betsy.executables.ws.TestPartnerServicePublisher;

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

    private final TestPartnerServicePublisher publisher;

    public PartnerServiceControl() {
        layoutFrame();
        publisher = new TestPartnerServicePublisher();

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
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                publisher.publish();
            }

        });
        this.add(start);
    }

    private void createStopButton() {
        JButton stop = new JButton("stop");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                publisher.unpublish();
            }

        });
        this.add(stop);
    }


}
