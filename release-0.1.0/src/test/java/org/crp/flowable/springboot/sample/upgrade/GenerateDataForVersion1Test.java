package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(value = "#{environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0]=='generateData'}")
public class GenerateDataForVersion1Test {
    @Autowired
    RuntimeService runtimeService;

    @Test
    void generateDataForHelloWorldProcess() {
        runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P001-helloWorld")
                .name("instance from the release 0.1.0")
                .start();
    }
}