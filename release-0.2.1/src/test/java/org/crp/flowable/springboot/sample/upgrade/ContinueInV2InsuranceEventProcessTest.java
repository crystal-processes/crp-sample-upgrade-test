package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run tests on generated data from previous versions.",
        value = "#{environment.getActiveProfiles().length ==0 || environment.getActiveProfiles()[0]!='generateData'}")
public class ContinueInV2InsuranceEventProcessTest {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Test
    void continueInV1InsuranceEventProcessInstance() {
        ProcessInstance processInsuranceEvent = runtimeService.createProcessInstanceQuery()
                .processInstanceName("Insurance event process instance from release 0.2.0")
                .singleResult();
        Task assessEventTask = taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();

        assertThatThrownBy(
                () -> taskService.complete(assessEventTask.getId(), Map.of("amount", 5))
        ).isInstanceOf(FlowableException.class)
                .hasMessage("Couldn't deserialize object in variable 'contract'");
    }
}
