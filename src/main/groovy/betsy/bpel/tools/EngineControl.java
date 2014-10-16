package betsy.bpel.tools;

import betsy.bpel.repositories.EngineRepository;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The GUI to install, start and stop a local engine or all local engines.
 */
public class EngineControl extends JFrame {

    private final DefaultListModel<String> actions = new DefaultListModel<>();

    private EngineControl() {


        this.setLayout(new BorderLayout());
        this.add(createCenterPanel(), BorderLayout.CENTER);
        JList<String> comp = new JList<>(actions);
        comp.setVisibleRowCount(4);
        this.add(new JScrollPane(comp), BorderLayout.NORTH);

        this.setSize(800, 1000);
        this.setTitle("ECC - Engine Control Center");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        toast("STARTED");
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        new EngineControl().setVisible(true);
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();

        final List<EngineLifecycle> bpelEngines = new EngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        int buttons = 6;
        int columns = 1 + buttons;
        int rows = engines.size() + 2; // one empty, one for all
        panel.setLayout(new GridLayout(rows, columns, 0, 10));

        for (EngineLifecycle engine : engines) {
            panel.add(new JLabel(engine.toString()));

            panel.add(createButton("install", engine, EngineLifecycle::install));
            panel.add(createButton("uninstall", engine, EngineLifecycle::uninstall));
            panel.add(createButton("isInstalled?", engine, (e) -> toast(engine.toString() + " is " + (engine.isInstalled() ? "installed" : "uninstalled"))));

            panel.add(createButton("start", engine, EngineLifecycle::startup));
            panel.add(createButton("stop", engine, EngineLifecycle::shutdown));
            panel.add(createButton("isRunning?", engine, (e) -> toast(engine.toString() + " is " + (engine.isRunning() ? "started" : "shutdown"))));

        }

        addEmptyRow(panel, columns);

        panel.add(new JLabel("ALL"));
        panel.add(createAllButton(engines, "install", EngineLifecycle::install));
        panel.add(createAllButton(engines, "uninstall", EngineLifecycle::uninstall));
        panel.add(new JLabel());
        panel.add(createAllButton(engines, "startup", EngineLifecycle::startup));
        panel.add(createAllButton(engines, "shutdown", EngineLifecycle::shutdown));
        panel.add(new JLabel());

        return panel;
    }

    private JButton createAllButton(List<EngineLifecycle> engines, String name, Consumer<EngineLifecycle> f) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            for (final EngineLifecycle engine : engines) {
                executeEngineAction(name, engine, (x) -> f.accept(engine));
            }
        });
        return button;
    }

    private JButton createButton(final String name, final EngineLifecycle engine, Consumer<EngineLifecycle> action) {
        JButton button = new JButton(name);
        button.addActionListener((e) -> executeEngineAction(name, engine, action));
        return button;
    }

    private void executeEngineAction(String name, EngineLifecycle engine, Consumer<EngineLifecycle> action) {
        executeAction(name + " of " + engine.toString(), () -> action.accept(engine));
    }

    private void executeAction(final String name, final Runnable action) {
        new Thread() {
            public void run() {
                toast("EXECUTE Action " + name);
                action.run();
                toast("EXECUTE Action " + name + " COMPLETED");
            }
        }.start();
    }


    private void toast(String message) {
        String toastMessage = "[" + new Date() + "] " + message;
        System.out.println(toastMessage);
        actions.insertElementAt(toastMessage, 0);
        this.validate();
        this.repaint();
    }

    private void addEmptyRow(JPanel panel, int columns) {
        for (int i = 0; i < columns; i++) {
            panel.add(new JLabel());
        }
    }
}
