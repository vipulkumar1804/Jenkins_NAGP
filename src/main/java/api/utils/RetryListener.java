package api.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryListener implements IRetryAnalyzer {

    int counter=0;
    int retryCount=2;
    public boolean retry(ITestResult iTestResult) {
        if(counter<retryCount){
            counter++;
            return true;
        }
        return false;
    }
}
