package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run only to generate data for the next releases.",
        value = "#{environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0]=='generateData'}")
public class GenerateDataTest {
    @Autowired
    RuntimeService runtimeService;

    @Test
    void generateDataForHelloWorldProcess() {
        runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P001-helloWorld")
                .name("instance from release 0.0.5")
                .start();
    }
}