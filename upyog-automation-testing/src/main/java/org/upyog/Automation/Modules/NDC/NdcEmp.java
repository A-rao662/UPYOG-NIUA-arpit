package org.upyog.Automation.Modules.NDC;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component

public class NdcEmp {

    public void ndcInboxEmp(
            WebDriver driver,
            WebDriverWait wait,
            JavascriptExecutor js) {

        try {

            TestEngine engine =
                    new TestEngine(
                            driver,
                            "config/dev.properties"
                    );

            engine.executeModule(
                    "test-config/noDueCertificate/ndc_employee_module.json"
            );
        }
        catch(Exception e) {

            throw new RuntimeException(
                    "Garbage Collection Employee Flow Failed",
                    e
            );
        }
    }
}
