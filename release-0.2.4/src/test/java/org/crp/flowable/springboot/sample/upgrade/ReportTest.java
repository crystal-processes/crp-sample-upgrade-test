package org.crp.flowable.springboot.sample.upgrade;

import org.assertj.core.api.Assertions;
import org.crp.flowable.springboot.sample.AcmeApplication;
import org.crp.flowable.springboot.sample.services.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;


@SpringBootTest(classes = {AcmeApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(reason="Run tests on generated data from previous versions.",
        value = "#{environment.getActiveProfiles().length ==0 || environment.getActiveProfiles()[0]!='generateData'}")
public class ReportTest {

    @Autowired
    ReportService reportService;

    @Test
    void getAllItemsFromPreviousVersions() {
        Assertions.assertThat(reportService.accountAmountReport())
                .as("The report must contain all items from the previous versions and all items must be serializable.")
                .hasSize(4);
    }
}
