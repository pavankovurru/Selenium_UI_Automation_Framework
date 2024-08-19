package com.company.project.login;
import com.company.project.utilities.selenium.SeleniumUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class LoginTest {

  public static Logger log = LogManager.getLogger();
  private WebDriver driver = null;
  private LoginPage lp;
  private SeleniumUtil seleniumUtil = null;

  @BeforeMethod
  @Parameters({"url", "browser", "browserVersion", "targetRun"})
  public void login(String url, String browser, String browserVersion, String targetRun) {

    seleniumUtil = new SeleniumUtil();

    // Creating driver object based on browser stack configuration
    if (targetRun.equalsIgnoreCase("browserStack")) {
      driver = seleniumUtil.createBrowserStackDriver(url,browser,browserVersion);
    } else if (targetRun.equalsIgnoreCase("local")) {
      driver = seleniumUtil.createDriver(url,browser);
    } else {
      log.info("Invalid `runOn` value present in testNG.xml");
      throw new SkipException("Skipping tests");
    }
    log.info("Selenium Web Driver session initiated on -" + targetRun);

    //Page Objects
    lp = new LoginPage(driver);
  }

  @Test(priority = 1)
  public void validatePageTitle() {
   String title = lp.getTitle();
   Assert.assertEquals(title,"Apple");
  }

  @Test(priority = 2)
  public void validatePageTitleFailure() {
    String title = lp.getTitle();
    Assert.assertEquals(title,"NotApple");
  }

  @AfterMethod(alwaysRun = true)
  public void logout() {
    if (driver != null) {
      driver.quit();
      log.info("Selenium Web Driver session terminated");
    }
  }
}
