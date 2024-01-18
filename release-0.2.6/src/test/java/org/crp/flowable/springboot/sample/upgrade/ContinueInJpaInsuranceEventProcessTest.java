package org.crp.flowable.springboot.sample.upgrade;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.crp.flowable.springboot.sample.services.MoneyService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;
import static org.mockito.Mockito.times;


@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run tests on generated data from previous versions.",
        value = "#{environment.getActiveProfiles().length ==0 || environment.getActiveProfiles()[0]!='generateData'}")
public class ContinueInJpaInsuranceEventProcessTest {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @MockBean
    MoneyService moneyService;

    @Test
    void continueInV1InsuranceEventProcessInstance() {
        ProcessInstance processInsuranceEvent = runtimeService.createProcessInstanceQuery()
                .processInstanceName("Insurance event process instance from release 0.2.5")
                .singleResult();
        Task assessEventTask = taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();
        taskService.complete(assessEventTask.getId(), Map.of("amount", 5));

        assertThat(processInsuranceEvent).doesNotExist()
                .inHistory().isFinished()
                .hasVariableWithValue("amount", 5);

        assertThatThrownBy(() ->
            Mockito.verify(moneyService, times(1)).sendMoney("1234567", 5)
        ).hasMessageContaining("""
                Argument(s) are different! Wanted:
                moneyService bean.sendMoney(
                    "1234567",
                    5
                );
                """);
    }
}
