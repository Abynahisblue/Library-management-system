package com.example.lms;

import com.example.lms.HelloApplication;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
    public void testApplicationStart() throws Exception {
        // Delay to ensure the UI is rendered
        Thread.sleep(1000);

        // Verify that the root AnchorPane is present
        verifyThat("#root", NodeMatchers.isNotNull());

        // Verify that the patron ImageView is present
        verifyThat("#patron", NodeMatchers.isNotNull());
        // Verify that the root AnchorPane is present
        verifyThat("#books", NodeMatchers.isNotNull());

        // Verify that the patron ImageView is present
        verifyThat("#issue", NodeMatchers.isNotNull());
        // Verify that the root AnchorPane is present
        verifyThat("#bk_return", NodeMatchers.isNotNull());

        // Verify that the patron ImageView is present
        verifyThat("#bk_search", NodeMatchers.isNotNull());
    }
}
