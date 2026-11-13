package org.crp.flowable.springboot.sample.upgrade;

import java.util.Map;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.crp.flowable.springboot.sample.services.MoneyService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;
import static org.mockito.Mockito.only;


@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run tests on generated data from previous versions.",
        value = "#{environment.getActiveProfiles().length ==0 || environment.getActiveProfiles()[0]!='generateData'}")
public class ContinueInJpaInsuranceEventProcessTest {

	@Autowired
	ProcessEngineConfiguration configuration;
	@Autowired
	RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @MockitoBean
    MoneyService moneyService;

    @Test
    void continueInV1InsuranceEventProcessInstance() {
		ProcessDefinition latestDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("P030-processInsuranceEvent").latestVersion().singleResult();
		JobTestHelper.waitForJobExecutorOnCondition(
				configuration, 10_000, 200,
				() -> runtimeService.createProcessInstanceQuery().processDefinitionId(latestDefinition.getId()).count() == 1);
		ProcessInstance processInsuranceEvent = runtimeService.createProcessInstanceQuery().processDefinitionId(latestDefinition.getId()).singleResult();
		assertThat(processInsuranceEvent)
				.hasVariable("insuranceEventId")
				.doesNotHaveVariable("contractId")
				.doesNotHaveVariable("eventDescription")
				.doesNotHaveVariable("amount");

        Task assessEventTask = taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();
        taskService.complete(assessEventTask.getId(), Map.of("amount", 5));

        assertThat(processInsuranceEvent).doesNotExist()
                .inHistory().isFinished()
                .hasVariableWithValue("amount", 5);

        Mockito.verify(moneyService, only()).sendMoney("1234567", 5);
    }
}
