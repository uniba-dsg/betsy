package betsy.bpel.tools;

import betsy.bpel.ws.DummyAndRegularTestPartnerService;
import betsy.bpel.ws.TestPartnerService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * GUI to start and stop / start and shutdown the partner service.
 */
public class PartnerServiceControl extends Application {

    private final TestPartnerService publisher = new DummyAndRegularTestPartnerService();

    public PartnerServiceControl() {
        layoutFrame();

        createStartButton();

        createStopButton();
    }

    public static void main(String... args) {
        PartnerServiceControl.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(300);
        stage.setHeight(75);
        stage.setTitle("Partner Service Control Center");

        Scene scene = new Scene(new FlowPane(createStartButton(), createStopButton()));
        stage.setScene(scene);
        stage.show();
    }

    private void layoutFrame() {

    }

    private Button createStartButton() {
        Button start = new Button("startup");
        start.setOnAction(e -> publisher.startup());
        return start;
    }

    private Button createStopButton() {
        Button stop = new Button("stop");
        stop.setOnAction(e -> publisher.shutdown());
        return stop;
    }


}
