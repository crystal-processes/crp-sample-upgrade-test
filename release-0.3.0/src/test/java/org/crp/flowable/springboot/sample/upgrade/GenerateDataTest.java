package org.crp.flowable.springboot.sample.upgrade;

import java.util.Map;

import org.crp.flowable.springboot.sample.AcmeApplication;
import org.crp.flowable.springboot.sample.entities.jpa.AccountEntity;
import org.crp.flowable.springboot.sample.entities.jpa.AccountRepository;
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity;
import org.crp.flowable.springboot.sample.entities.jpa.ContractRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run only to generate data for the next releases.",
        value = "#{environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0]=='generateData'}")
public class
GenerateDataTest {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Autowired
    ContractRepository contractRepository;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void createDemoContract() {
		if (accountRepository.count() == 0) {
			var accountEntity = accountRepository.save(AccountEntity.builder()
					.number("1234567")
					.owner("jlong")
					.build()
			);
			contractRepository.save(
					ContractEntity.builder()
							.maxAmount(10000)
							.contractId("testContractId")
							.account(accountEntity).build()
			);
			contractRepository.flush();
		}
    }

    @Test
    void generateDataForProcessInsuranceEvent() {
        assertThat(
                runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P030-processInsuranceEvent")
                .name("Insurance event process instance from release 0.3.0") // the same as 0.2.5 version
                .variable("contractId", "testContractId")
                .variable("eventDescription", "Test event description")
                .variable("amount", 1000)
                .start()
        ).as("Generated data from version 0.3.0 for version 0.3.1. Continue in the process execution after complete task.")
                .isRunning();
    }

    @Test
    void completeProcessInsuranceEvent() {
        ProcessInstance processInsuranceEvent = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P030-processInsuranceEvent")
                .name("Insurance event process instance from release 0.3.0 completed")
                .variable("contractId", "testContractId")
                .variable("eventDescription", "Test event description")
                .variable("amount", 1000)
                .start();
        Task assessEventTask = taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();
        taskService.complete(assessEventTask.getId(), Map.of("amount", 5));

        assertThat(
                processInsuranceEvent
        ).as("Generated data from version 0.3.0 for version 0.3.1, must be completed")
                .doesNotExist()
                .inHistory().isFinished().hasVariableWithValue("amount", 5);
    }

}