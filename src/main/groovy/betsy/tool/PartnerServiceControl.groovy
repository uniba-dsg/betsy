package betsy.tool

import betsy.executables.ws.TestPartnerServicePublisher

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * Created with IntelliJ IDEA.
 * User: joerg
 * Date: 29.08.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
class PartnerServiceControl extends JFrame {

    TestPartnerServicePublisher publisher

    public static void main(String[] args) {
        new PartnerServiceControl().setVisible(true)
    }

    public PartnerServiceControl() {
        layoutFrame()
        publisher = new TestPartnerServicePublisher()

        createStartButton()

        createStopButton()
    }

    private void layoutFrame() {
        this.setLayout(new FlowLayout())
        this.setSize(300, 75)
        this.setTitle("Partner Service Control Center")
        this.setDefaultCloseOperation(EXIT_ON_CLOSE)
    }

    private void createStartButton() {
        JButton start = new JButton("startup")
        start.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                publisher.publish()
            }
        })
        this.add(start)
    }

    private void createStopButton() {
        JButton stop = new JButton("stop")
        stop.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                publisher.unpublish()
            }
        })
        this.add(stop)
    }

}
