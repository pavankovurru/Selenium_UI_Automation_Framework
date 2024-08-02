package com.company.project;

import org.testng.*;
import org.testng.ITestResult;

public class RetryLogic implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int maxRetryCount = 2; // Change this to the number of retries you want

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}
