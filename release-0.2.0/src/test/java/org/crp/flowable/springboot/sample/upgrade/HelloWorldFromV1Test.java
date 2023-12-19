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

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run tests on generated data from previous versions.",
        value = "#{environment.getActiveProfiles().length ==0 || environment.getActiveProfiles()[0]!='generateData'}")
public class HelloWorldFromV1Test {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Test
    void continueInV4HelloWorldProcess() {
        ProcessInstance helloWorldV4 = runtimeService.createProcessInstanceQuery().processInstanceName("instance from the release 0.1.0")
                .singleResult();

        assertThat(helloWorldV4)
                .as("The v0.1.0 process instance must keep the same state.")
                .isRunning()
                .userTasks().extracting("name")
                .containsExactly("Say hello world");

        Task taskV4 = taskService.createTaskQuery().processInstanceId(helloWorldV4.getId()).singleResult();
        taskService.complete(taskV4.getId());

        assertThat(helloWorldV4)
                .as("The process instance from the previous app version must allow to be continued after application upgrade.")
                .doesNotExist()
                .inHistory()
                .isFinished();
    }
}
