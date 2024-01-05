package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.Map;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run only to generate data for the next releases.",
        value = "#{environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0]=='generateData'}")
public class GenerateDataTest {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Test
    void generateDataForProcessInsuranceEvent() {
        assertThat(
                runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P002-processInsuranceEvent")
                .name("Insurance event process instance from release 0.2.2")
                .variable("contractId", "testContractId")
                .start()
        ).as("Generated data from version 0.2.2 for version 0.2.3. Continue in the process execution after complete task.")
                .isRunning();
    }

    @Test
    void completeProcessInsuranceEvent() {
        ProcessInstance processInsuranceEvent = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P002-processInsuranceEvent")
                .name("Insurance event process instance from release 0.2.2 completed")
                .variable("contractId", "testContractId")
                .start();
        Task assessEventTask = taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();
        taskService.complete(assessEventTask.getId(), Map.of("amount", 5));

        assertThat(
                processInsuranceEvent
        ).as("Generated data from version 0.2.2 for version 0.2.3, must be completed")
                .doesNotExist()
                .inHistory().isFinished().hasVariableWithValue("amount", 5);
    }

}