package org.upyog.Automation.Modules.NDC;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component

public class NdcCitizenCreate {
    public void ndcReg(
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
                    "test-config/noDueCertificate/ndc_citizen__module.json"
            );

        }catch(Exception e) {

            throw new RuntimeException(
                    "No Due Certificate Citizen Flow Failed",
                    e
            );
        }
    }
}
