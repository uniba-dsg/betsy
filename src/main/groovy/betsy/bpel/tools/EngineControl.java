package betsy.bpel.tools;

import betsy.bpel.engines.Engine;
import betsy.engines.EngineAPI;
import betsy.bpel.repositories.EngineRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * The GUI to install, start and stop a local engine or all local engines.
 */
public class EngineControl extends JFrame {
    public static void main(String[] args) {
        new EngineControl().setVisible(true);
    }

    public EngineControl() {
        final List<Engine> engineList = new EngineRepository().getByName("LOCALS");
        final List<EngineAPI> engines = new LinkedList<>();
        for (Engine engine : engineList) {
            engines.add(engine);
        }

        this.setLayout(new GridLayout(engines.size() + 1, 4, 0, 10));
        this.setSize(400, 300);
        this.setTitle("Engine Control Center");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        for (EngineAPI tmpEngine : engines) {
            final EngineAPI engine = tmpEngine;

            add(new JLabel(engine.getName()));

            JButton startButton = new JButton("install");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread() {
                        public void run() {
                            engine.install();
                            System.out.println("Installation of " + engine.getName() + " is complete");
                        }

                    }.start();
                }

            });
            add(startButton);

            startButton = new JButton("startup");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread() {
                        public void run() {
                            engine.startup();
                        }

                    }.start();
                }

            });
            add(startButton);

            startButton = new JButton("shutdown");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread() {
                        public void run() {
                            engine.shutdown();
                        }

                    }.start();
                }

            });
            add(startButton);
        }


        add(new JLabel("ALL"));

        JButton startButton = new JButton("install");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (final EngineAPI engine : engines) {
                    new Thread() {
                        public void run() {
                            engine.install();
                            System.out.println("Installation of " + engine.getName() + " is complete");
                        }

                    }.start();
                }

            }

        });
        this.add(startButton);

        startButton = new JButton("startup");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (final EngineAPI engine : engines) {
                    new Thread() {
                        public void run() {
                            engine.startup();
                        }

                    }.start();
                }

            }

        });
        this.add(startButton);

        startButton = new JButton("shutdown");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (final EngineAPI engine : engines) {
                    new Thread() {
                        public void run() {
                            engine.shutdown();
                        }

                    }.start();
                }

            }

        });
        this.add(startButton);
    }
}
