package ExtentReport;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


public class ExtentReport1st {
	
	public ExtentReports extent;
	public ExtentTest test;
	WebDriver driver;
	@BeforeTest
	public void config()
	
	{
	String path = System.getProperty("user.dir")+"\\reports\\index.html";	
	ExtentSparkReporter reporter = new ExtentSparkReporter(path);
	//this class is for configuration, also called a helper class
	//Everything is configurable in Extent reports
	reporter.config().setReportName("Web Automation Results");
	reporter.config().setDocumentTitle("Test Results");
	reporter.config().setTheme(Theme.DARK);
	
	//main class which is responsible to drive all our reporting
	//execution
	//The report contains 2 parts/sections :
	//1st :for defining the environments and configurations we have ExtentReports class
	
	 extent = new ExtentReports();
	//attaching the generated report to the extentReports
	//ExtentReports is responsible for all the consolidation and execution
	extent.attachReporter(reporter);
	extent.setSystemInfo("Tester", "Hardeep Singh");
	extent.setSystemInfo("Environment", "QA");
	extent.setSystemInfo("BrowserName", "Chrome");
	
		
	}
	
	@Test
	public void titleValidation()
	{
		//create test will automatically keep in monitoring our test case
		//it will mark it as pass or fail, we need not to do anything
		//extent.createTest("Initial Demo"); this works but what actually happens
		//is that an object of Extent Test class is created as below which would be unique to
		//our test method, this object is responsible for listening and reporting
		//to the extent reports
		//2nd part(BODY) : how mant test cases passed, failed , skipped, logging, add screenshot  etc
		// extent.createTest = will create a new entry in our report i.e a new test case
		test = extent.createTest("titleValidation");
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\HARDEEP SINGH\\Desktop\\selenium\\chromedriver.exe");
		driver = new ChromeDriver();	
		driver.get("https://rahulshettyacademy.com/dropdownsPractise/");
		Assert.assertEquals(driver.getTitle(), "QAClickJet - Flight Booking for Domestic and International, Cheap Air Ticket");
		//deliberately failing, ideally we won't do it
		//test.fail("Result do not match");

		
		//to notify that test is done and no more monitoring is required
		//extent.flush();
	}
	
	@Test(dependsOnMethods={"titleValidation"})
	public void logoTest()
	{
		
		test = extent.createTest("logoTest");
		
		Assert.assertTrue(driver.findElement(By.xpath("//*[@name='ctl00$mainContent$btn_FindFlights']")).isDisplayed());

	}
	//skipping all the non browser tests
	@Test
	public void AnormalMethod()
	{
		test = extent.createTest("Anormal method");//this name would be visible in report
		//not the actual method name
		Assert.assertTrue(true);
		//test.log(LogStatus.,"Pass"); initial SDET extent report lecture, 
		//at that time is used to reflect pass or fail in the report
		
	}
	
	@Test
	public void CnormalMethod1()
	{
		test = extent.createTest("Cnormalmethod1");
		Assert.assertTrue(false);

	}
	
	@Test
	public void BnormalMethod2()
	{
		test = extent.createTest("Bnormalmethod2");
		Assert.assertTrue(true);
		test.fail("Result do not match");//rahul shetty taught this to deliberately fail in report
	}
	
	@Test
	public void validateUrl()
	{
		test = extent.createTest("validateUrl");
		Assert.assertTrue(driver.findElement(By.xpath("//*[@name='ctl00$mainContent$btn_FindFlights']")).isDisplayed());
		//we can create multiple nodes, generally for checking multiple data
		test.createNode("login with valid input");
		Assert.assertTrue(true);
		test.createNode("login with Invalid input");
		Assert.assertTrue(false);

	}
	/*
	@Test
	public void EnormalMethod3()
	{
		test = extent.createTest("Enormalmethod3 failure assertion");
		//why failure with assertion not showing in extent report ? only showing after the status.fail logic
		//Assert.assertTrue(true); //changing to true now
		Assert.assertTrue(true);

	}
	@Test
	public void DnormalMethod4()
	{
		test = extent.createTest("Dnormalmethod4");
		Assert.assertTrue(true);

	}
	@Test
	public void FnormalMethod5()
	{
		test = extent.createTest("Fnormalmethod5");
		Assert.assertTrue(true);

	}*/
	
	//Itestresult is a class with result as object, 
	//result will have the status of the every test method
	//if paased then it wpuld be in the ontest success method 
	//below in case this would come from the on test failure
	@AfterMethod
	public void tearDownn(ITestResult result) throws IOException
	{

		if (result.getStatus()==ITestResult.FAILURE)
			
		{
			test.log(Status.FAIL, "THE FAILED TEST CASE IS " + result.getName());// to get the name
			test.log(Status.FAIL, "THE FAILED TEST CASE IS " + result.getThrowable());//to get the exception	
			String screenshotPath = getScreenshot(result.getName());
			//to add screenshot from the path
			test.addScreenCaptureFromPath(screenshotPath);
		}
		else if (result.getStatus()==ITestResult.SKIP)
		{
			test.log(Status.SKIP, "THE SKIPPED TEST SKIPPED IS " + result.getName());
			
		}
		
		else if (result.getStatus()==ITestResult.SUCCESS)
		{
			test.log(Status.PASS, "THE PASSED TEST CASE IS" + result.getName());
			
		}
	}
	
	public String getScreenshot(String screenshotname) throws IOException
	
	{
		Date d1 = new Date();
		//not working with hh:ss:mm
		SimpleDateFormat sdf = new SimpleDateFormat("MMddYYYYhhmmss");
		String datename = sdf.format(d1);
		//new not because maybe because driver object is already there and we are casting it
		TakesScreenshot ts = (TakesScreenshot)driver;
		File src = ts.getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir")+"\\Screenshots\\"+ screenshotname + datename + ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(src, finalDestination);
		return destination;
		
		
	}
	
	
	@AfterTest
	public void stopMonitoring()
	{

		driver.close();
		extent.flush();
	}
	
	

}
