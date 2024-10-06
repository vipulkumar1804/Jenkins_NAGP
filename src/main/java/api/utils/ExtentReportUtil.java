package api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.*;
import java.io.File;
import java.util.Date;

public class ExtentReportUtil extends TestListenerAdapter implements ISuiteListener {
    public static ExtentSparkReporter reporter =null;
    public static ExtentReports extent = null;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static ThreadLocal<String> screenshotPath = new ThreadLocal<>();
    public static ThreadLocal<String> testCaseNames = new ThreadLocal<>();
    private static ExtentTest testExtent;

    public void onStart(ISuite suite) {
        String htmlFileName = System.getProperty("user.dir")+File.separator+"reports"+File.separator+"Reports_"+new Date().toString().replace(":","_").replace(" ","_")+".html";
        reporter = new ExtentSparkReporter(htmlFileName);
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }
    @Override
    public void onTestStart(ITestResult result) {
        String reportingParameter = getReportingParameter(result);
        String testCaseName = result.getMethod().getMethodName();
        testCaseName = reportingParameter.isEmpty() ?testCaseName:testCaseName+"_"+reportingParameter;
        testCaseNames.set(testCaseName);
        test.set(extent.createTest(testCaseName));
        try {
            String ssPath= System.getProperty("user.dir") + File.separator + "screenshots" ;
            createScreenshotFolder(ssPath);
            screenshotPath.set(ssPath);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    @Override 
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS,"TestCase Passed");
    }
    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL,"TestCase Failed");
        //Add failed test screen shot in the extent report
        test.get().addScreenCaptureFromPath(getScreenshotPath());
    }
    @Override
    public void onFinish(ITestContext testContext) { 
    	
    }
    public void onFinish(ISuite suite) {
        stopReporting();
    }
    public void stopReporting() {
        extent.flush();
    }
    public static void updateReportInfo(boolean status, String step){
        if(status) {
            test.get().log(Status.PASS,step);
        }else{
            test.get().log(Status.FAIL,step);
        }
        Assert.assertTrue(status,step);
    }
    public String getScreenshotPath() {
        return screenshotPath.get();
    }
    public static void createTest(String testName) {
        testExtent = extent.createTest(testName);
    }
    public static ExtentTest getTest() {
        return testExtent;
    }
    public String getReportingParameter(ITestResult result) {
        String output="";
        Object[] parameters = result.getParameters();
        try {
            String reportingParameter = parameters[parameters.length - 1].toString();
            if (reportingParameter.contains("Info_"))
                return reportingParameter.replace("Info_", "").replace("_", "");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return output;
    }
    public void createScreenshotFolder(String path) {
        File ssDir = new File(path);
        if(ssDir.exists()){
            ssDir.delete();
        }
        ssDir.mkdirs();
    }
}
