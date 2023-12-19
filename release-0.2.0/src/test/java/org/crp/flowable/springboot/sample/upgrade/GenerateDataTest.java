package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run only to generate data for the next releases.",
        value = "#{environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0]=='generateData'}")
public class GenerateDataTest {
    @Autowired
    RuntimeService runtimeService;

    @Test
    void generateDataForProcessInsuranceEvent() {
        assertThat(
                runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P002-processInsuranceEvent")
                .name("Insurance event process instance from release 0.2.0")
                .variable("contractId", "testContractId")
                .start()
        ).as("Generated data from version 0.2.0 for version 0.2.1. Continue in the process execution after complete task.")
                .isRunning();
    }
}