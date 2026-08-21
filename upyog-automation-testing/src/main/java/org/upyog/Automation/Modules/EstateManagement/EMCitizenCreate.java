package org.upyog.Automation.Modules.EstateManagement;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component
public class EMCitizenCreate {

    public void emReg(
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
                    "test-config/estateManagement/estateManagement_citizen_module.json"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Estate Management Citizen Flow Failed",
                    e
            );
        }
    }
}
