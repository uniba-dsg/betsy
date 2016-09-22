package peal.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import betsy.common.tasks.FileTasks;
import betsy.common.util.IOCapture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import peal.identifier.EngineId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractEngineServiceCleanup {

    public abstract EngineId getEngineId();

    @Before
    public void truncateServerPathFolder() {
        IOCapture.captureIO(
                () -> {
                    FileTasks.deleteDirectory(Paths.get("test"));

                    Path serverPath = Paths.get("server");
                    FileTasks.deleteDirectory(serverPath);
                    assertFalse(Files.isDirectory(serverPath));
                    FileTasks.mkdirs(serverPath);
                    assertTrue(Files.isDirectory(serverPath));
                    System.out.println("\n\nPREPARATION DONE\n");
                }
        );
    }

    @BeforeClass
    public static void ensureEnginesAreShutdown() {
        IOCapture.captureIO(
                () -> {
                    new EngineServiceImpl().getSupportedEngines().forEach((e) -> {
                        try {
                            new EngineServiceImpl().stop(e);
                        } catch (Exception ignored) {
                            // ignore
                        }
                    });
                }
        );
    }

    @After
    public void ensureEngineIsShutdown() {
        IOCapture.captureIO(
                () -> {
                    System.out.println("\n\nSHUTTING DOWN AFTER TEST\n");
                    try {
                        new EngineServiceImpl().stop(getEngineId());
                    } catch (Exception ignored) {
                        // ignore
                    }
                }
        );
    }

}
