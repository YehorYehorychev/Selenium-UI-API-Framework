package com.yehorychev.selenium.runner;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

/**
 * Dynamically wires TestNG thread counts to TestConfig.PARALLEL_THREADS so -DPARALLEL_THREADS takes effect.
 */
public class DynamicThreadListener implements IAlterSuiteListener {

    private static final Logger log = new Logger(DynamicThreadListener.class);

    @Override
    public void alter(List<XmlSuite> suites) {
        int threads = TestConfig.PARALLEL_THREADS;

        for (XmlSuite suite : suites) {
            suite.setParallel(XmlSuite.ParallelMode.METHODS);
            suite.setThreadCount(threads);
            suite.setDataProviderThreadCount(threads);
            log.info("Configured TestNG suite with parallel=methods, threadCount=" + threads);
        }
    }
}

