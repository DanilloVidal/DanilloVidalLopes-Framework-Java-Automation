package com.automation.framework.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/DBS")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.automation.framework.presentation.steps")
public class RunCucumberTest {
}