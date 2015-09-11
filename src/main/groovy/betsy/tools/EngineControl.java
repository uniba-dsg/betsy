package betsy.tools;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The GUI to install, start and stop a local engine or all local engines.
 */
public class EngineControl extends Application {

    private static class LogFileUtil {

        /**
         * Requires BPEL or BPMN Engine to be passed!
         */
        public static Path copyLogsToTempFolder(Object e) {
            final Path tmpFolder = createTempFolder(e.toString());
            if (e instanceof AbstractBPELEngine) {
                AbstractBPELEngine eNew = (AbstractBPELEngine) e;
                eNew.storeLogs(new BPELProcess() {
                    @Override
                    public Path getTargetLogsPath() {
                        return tmpFolder;
                    }
                });
            } else if (e instanceof AbstractBPMNEngine) {
                AbstractBPMNEngine eNew = (AbstractBPMNEngine) e;
                eNew.storeLogs(new BPMNProcess() {
                    @Override
                    public Path getTargetLogsPath() {
                        return tmpFolder;
                    }
                });
            }
            return tmpFolder;
        }

        public static Path createTempFolder(String context) {
            try {
                return Files.createTempDirectory("betsy-" + context + "-logs");
            } catch (IOException e1) {
                throw new IllegalStateException("Could not create temp folder", e1);
            }
        }

    }

    private final ObservableList<String> actions = FXCollections.observableList(new LinkedList<>());

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();
        pane.setCenter(new ScrollPane(createCenterPanel()));
        ListView<String> listView = new ListView<>(actions);
        listView.setMaxWidth(Double.MAX_VALUE);
        listView.setPrefWidth(750);
        pane.setTop(new ScrollPane(listView));

        primaryStage.setTitle("ECC - Engine Control Center");

        Scene scene = new Scene(pane, 800, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();

        toast("UP AND RUNNING");
    }

    public static void main(String... args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        launch(args);
    }

    private Pane createCenterPanel() {
        GridPane pane = new GridPane();
        pane.setHgap(1);
        pane.setVgap(3);

        final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        int rowCounter = 0;

        for (EngineLifecycle engine : bpelEngines) {
            List<Node> nodes = createEngineRow(engine);

            pane.addRow(rowCounter, nodes.toArray(new Node[nodes.size()]));
            rowCounter++;
        }

        // empty row
        List<Node> emptyRow = createEmptyRow(7);
        pane.addRow(rowCounter, emptyRow.toArray(new Node[emptyRow.size()]));
        rowCounter++;

        for (EngineLifecycle engine : bpmnEngines) {
            List<Node> nodes = createEngineRow(engine);

            pane.addRow(rowCounter, nodes.toArray(new Node[nodes.size()]));
            rowCounter++;
        }

        // empty row
        List<Node> emptyRow2 = createEmptyRow(7);
        pane.addRow(rowCounter, emptyRow2.toArray(new Node[emptyRow2.size()]));
        rowCounter++;

        List<Node> nodes = getAllEnginesRow(engines);
        pane.addRow(rowCounter, nodes.toArray(new Node[nodes.size()]));

        return pane;
    }

    private List<Node> getAllEnginesRow(List<EngineLifecycle> engines) {
        List<Node> nodes = new LinkedList<>();
        nodes.add(new Label("ALL"));
        nodes.add(createAllButton(engines, "install", EngineLifecycle::install));
        nodes.add(createAllButton(engines, "uninstall", EngineLifecycle::uninstall));
        nodes.add(new Label());
        nodes.add(createAllButton(engines, "start", EngineLifecycle::startup));
        nodes.add(createAllButton(engines, "stop", EngineLifecycle::shutdown));
        nodes.add(new Label());
        return nodes;
    }

    private List<Node> createEngineRow(EngineLifecycle engine) {
        List<Node> nodes = new LinkedList<>();
        nodes.add(new Label(engine.toString()));

        nodes.add(createButton("install", engine, EngineLifecycle::install));
        nodes.add(createButton("uninstall", engine, EngineLifecycle::uninstall));
        nodes.add(createButton("isInstalled?", engine, (e) -> {
            boolean isInstalled = engine.isInstalled();
            Platform.runLater(() -> toast(engine.toString() + " is " + (isInstalled ? "installed" : "uninstalled")));
        }));

        nodes.add(createButton("start", engine, EngineLifecycle::startup));
        nodes.add(createButton("stop", engine, EngineLifecycle::shutdown));
        nodes.add(createButton("isRunning?", engine, (e) -> {
            boolean isRunning = engine.isRunning();
            Platform.runLater(() -> toast(engine.toString() + " is " + (isRunning ? "started" : "shutdown")));
        }));
        nodes.add(createButton("open folder with logs", engine, (e) -> {
            final Path tmpFolder = LogFileUtil.copyLogsToTempFolder(e);
            try {
                Desktop.getDesktop().open(tmpFolder.toFile());
            } catch (IOException e1) {
                throw new IllegalStateException("Could not open folder " + tmpFolder, e1);
            }
        }));
        return nodes;
    }

    private Button createAllButton(List<EngineLifecycle> engines, String name, Consumer<EngineLifecycle> f) {
        Button button = new Button(name);
        button.setOnAction(e -> {
            for (final EngineLifecycle engine : engines) {
                executeEngineAction(name, engine, (x) -> f.accept(engine));
            }
        });
        return button;
    }

    private Button createButton(final String name, final EngineLifecycle engine, Consumer<EngineLifecycle> action) {
        Button button = new Button(name);
        button.setOnAction((e) -> executeEngineAction(name, engine, action));
        return button;
    }

    private void executeEngineAction(String name, EngineLifecycle engine, Consumer<EngineLifecycle> action) {
        executeAction(name + " of " + engine.toString(), () -> action.accept(engine));
    }

    private void executeAction(final String name, final Runnable action) {
        new Thread(new Task<Void>() {
            public Void call() {
                Platform.runLater(() -> toast(name));
                action.run();
                Platform.runLater(() -> toast(name + " DONE"));

                return null;
            }
        }).start();
    }


    private void toast(String message) {
        String time = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).format(LocalTime.now());
        String toastMessage = String.format("[%s] %s", time, message);
        System.out.println(toastMessage);
        actions.add(0, toastMessage);
    }

    private List<Node> createEmptyRow(int columns) {
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < columns; i++) {
            nodes.add(new Label());
        }
        return nodes;
    }
}
