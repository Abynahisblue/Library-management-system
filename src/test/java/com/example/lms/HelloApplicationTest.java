package com.example.lms;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;

class HelloApplicationTest extends ApplicationTest {

    @BeforeAll
    public static void setupSpec() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    @AfterAll
    public static void tearDownSpec() throws Exception {
        FxToolkit.hideStage();
    }

    @Override
    public void start(Stage stage) throws Exception {
        new HelloApplication().start(stage);
    }

    @Test
    public void testApplicationStart() throws InterruptedException {
        Thread.sleep(2000);

        verifyThat("#root", NodeMatchers.isVisible());
        verifyThat("#patron", NodeMatchers.isNotNull());
    }
}
