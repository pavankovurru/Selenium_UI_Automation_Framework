package com.company.project.utilities;

import com.company.project.constants.global.GlobalConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeleniumUtil {

    public static Logger log = LogManager.getLogger();
    public WebDriver driver;
    public String parentHandle;
    public Alert alert;
    public WebDriverWait wait;
    public ChromeOptions chromeOptions;

    // ********* BROWSER STACK CONFIGURATION *************//

    // creating browser stack driver
    // use this URL to configure other browser configurations
    // https://www.browserstack.com/automate/java#configure-capabilities

    public WebDriver createBrowserStackDriver(String url, String browser, String browserVersion) {

        final String USERNAME = "USERNAME"; // TODO Update browser stack account user name & key
        final String AUTOMATE_KEY = "BrowserStackKey";
        final String URL =
                "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities();

        // use this URL to configure other browser configurations
        // https://www.browserstack.com/automate/java#configure-capabilities

        // LATEST CHROME CONFIGURATION ON MAC
        caps.setCapability("browser", browser);
        caps.setCapability("browser_version", browserVersion);
        caps.setCapability("os", "OS X");
        caps.setCapability("os_version", "Sierra");
        caps.setCapability("resolution", "1920x1080");

        // Maximising chrome browser on browser stack
        if (browser.equalsIgnoreCase("chrome")) {
            chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--kiosk");
            chromeOptions.addArguments("--start-maximized");
            caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }

        // IE and Safari pop ups configuration

        caps.setCapability("browserstack.ie.enablePopups", "true");
        caps.setCapability("browserstack.safari.enablePopups", "true");
        caps.setCapability("browserstack.safari.allowAllCookies", "true");
        caps.setCapability("browserstack.debug", "true");
        // caps.setCapability(FirefoxDriver.PROFILE, firefoxProfile());

        try {
            driver = new RemoteWebDriver(new URL(URL), caps);
            // Remote File upload support
            ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        // Launch URL and Maximize window
        driver.get(url);
        log.info("Created Web Driver");
        log.info("navigated to url -" + url);
        // driver.manage().window().maximize();
        log.info("browser window maximised");

        return driver;
    }

    // *********** DRIVER HANDLING **************************//

    // Creating Web Driver

    public WebDriver createDriver(String url, String browser) {
        log.info("Initiating driver on browser name -" + browser);

        // Initialize driver object to FIREFOX BROWSER
        if (browser.equalsIgnoreCase("FIREFOX")) {
            System.setProperty("webdriver.gecko.driver", GlobalConstants.MAC_FIREFOX_GECKO_DRIVER_PATH);
            System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");  // Disabling firefox logs

            //Firefox Options and profile
            FirefoxOptions options = new FirefoxOptions();
            options.setProfile(firefoxProfile());
            driver = new FirefoxDriver(options);

            log.info("firefox driver created");
        }

        // Initialize driver object to CHROME BROWSER
        else if (browser.equalsIgnoreCase("CHROME")) {

            chromeOptions = new ChromeOptions();
            //chromeOptions.setAcceptInsecureCerts(true);
            //chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);

            //Handling ssl cert issues with desired capabilities
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability (CapabilityType.ACCEPT_SSL_CERTS, true);
            chromeOptions.merge(capabilities);

            // configuring driver based on operating system
            if (Platform.getCurrent().toString().matches(".*?(mac|MAC).*?")) {
                System.setProperty("webdriver.chrome.driver", GlobalConstants.MAC_CHROME_DRIVER_PATH);
                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "null");  // Disabling logs
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
            }

            if (Platform.getCurrent().toString().matches(".*?(win|WIN).*?")) {
                System.setProperty("webdriver.chrome.driver", GlobalConstants.WINDOWS_CHROME_DRIVER_PATH);
                System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");  // Disabling logs
                log.info("Chrome driver created : WINDOWS OS");
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");

                log.info("browser window maximised");
            }

            driver = new ChromeDriver(chromeOptions);
            log.info("Chrome driver created : MAC OS");
        }

        // Initialize driver object to IE
        else if (browser.equalsIgnoreCase("IE")) {
            System.setProperty("webdriver.ie.driver", GlobalConstants.MICROSOFT_WEB_DRIVER_DRIVER_PATH);
            driver = new InternetExplorerDriver();
        }

        // Initialize driver object to EDGE
        else if (browser.equalsIgnoreCase("EDGE")) {

            //Handling SSL cert issue
            EdgeOptions edgeOptions = new EdgeOptions();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            edgeOptions.merge(capabilities);

            System.setProperty("webdriver.ie.driver", GlobalConstants.MICROSOFT_WEB_DRIVER_DRIVER_PATH);
            System.setProperty(EdgeDriverService.EDGE_DRIVER_LOG_PROPERTY,"null"); // Disabling logs
            driver = new EdgeDriver(edgeOptions);
        }

        //  TODO  - Initialize driver object to SAFARI
        else if (browser.equalsIgnoreCase("SAFARI")) {
            driver = new SafariDriver();
        }

        // Launch URL and Maximize window
        log.info("launching browser");

        driver.get(url);
        //driver.get("javascript:document.getElementById('overridelink').click();");     //handle certificate issues
        log.info("navigated to url-"+url);
        driver.manage().window().fullscreen();
        log.info("browser window - FULL SCREEN MODE");
        log.info("Created Web Driver");
        return driver;
    }

    // Creating Fire fox Profile

    public FirefoxProfile firefoxProfile() {

        FirefoxProfile profile = new FirefoxProfile();

        // Handling downloads - saving files to "downloads" folder present in the project
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference(
                "browser.download.dir",
                System.getProperty("user.dir") + "/src/main/java/com/phunware/maas/downloads");
        profile.setPreference(
                "browser.helperApps.neverAsk.openFile",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
        profile.setPreference(
                "browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.closeWhenDone", false);

        // Handling Certificate issues
        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(false);

        // Enable native events
        profile.shouldLoadNoFocusLib();

        //Disable notifications
        profile.setPreference("dom.webnotifications.enabled", false);

        return profile;
    }

    // ************** NORMAL CLICK *******************//

    public void click(WebDriver driver, By locator) {
        wait_until_ElementIs_Present(driver, locator).click();
        waitForPageToLoad(driver);
    }

    // ******************** ACTIONS ***********************//

    // Hover over an element
    public void hover(WebDriver driver, By locator) {
        Actions action = new Actions(driver);
        action.moveToElement(wait_until_ElementIs_Visible(driver, locator)).build().perform();
        log.info("Hover action done on element -" + locator);
        waitForPageToLoad(driver);
    }

    // Hover over an element and click
    public void hoverAndClick(WebDriver driver, By locator) {
        Actions action = new Actions(driver);
        action.moveToElement(wait_until_ElementIs_Visible(driver, locator)).click().build().perform();
        log.info("click action on -" + locator + " performed");
        waitForPageToLoad(driver);
    }

    public void hoverAndClear(WebDriver driver, By locator) {
        Actions action = new Actions(driver);
        WebElement el = wait_until_ElementIs_Visible(driver, locator);
        action.moveToElement(el).click().build().perform();
        el.clear();
        log.info("click and clear actions on -" + el + " performed");
        waitForPageToLoad(driver);
    }

    // Hover over an element, click and press enter
    public void hoverClickAndPressEnter(WebDriver driver, By locator) {
        Actions action = new Actions(driver);
        action
                .moveToElement(wait_until_ElementIs_Visible(driver, locator))
                .click()
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        waitForPageToLoad(driver);
    }

    // Hover over an element click and send data
    public void hoverClickAndSendData(WebDriver driver, By locator, String data) {
        Actions action = new Actions(driver);
        action
                .moveToElement(wait_until_ElementIs_Visible(driver, locator))
                .click()
                .sendKeys(data)
                .build()
                .perform();
        waitForPageToLoad(driver);
    }

    // Hover over an element click, send data and press enter
    public void hoverClickSendDataAndPressEnter(WebDriver driver, By locator, String data) {
        Actions action = new Actions(driver);
        action
                .moveToElement(wait_until_ElementIs_Visible(driver, locator))
                .click()
                .sendKeys(data)
                .sendKeys(Keys.ENTER)
                .build()
                .perform();
        waitForPageToLoad(driver);
    }

    // sendkeys
    public void hoverAndSendData(WebDriver driver, By locator, String data) {
        Actions action = new Actions(driver);
        action
                .moveToElement(wait_until_ElementIs_Visible(driver, locator))
                .sendKeys(data)
                .build()
                .perform();
        waitForPageToLoad(driver);
    }

    // Double click
    public void doubleClick(WebDriver driver, By locator) {
        Actions doubleClick = new Actions(driver);
        doubleClick.doubleClick(wait_until_ElementIs_Visible(driver, locator)).build().perform();
        waitForPageToLoad(driver);
    }

    // Drag and Drop by offset
    public void dragAndDropOffset(WebDriver driver, By locator, int offsetX, int offsetY) {
        WebElement el = wait_until_ElementIs_Visible(driver, locator);
        Actions builder = new Actions(driver);
        builder.dragAndDropBy(el, offsetX, offsetY).build().perform();
        waitForPageToLoad(driver);
    }

    // Drag and drop Elements
    public void dragAndDropToElementContainner(
            WebDriver driver, WebElement source, WebElement target) {
        Actions builder = new Actions(driver);
        builder.dragAndDrop(source, target).build().perform();
        waitForPageToLoad(driver);
    }

    // *********** JAVA SCRIPT CLICK **********************************//

    public void jsClick(WebDriver driver, By locator) {
        String code =
                "var fireOnThis = arguments[0];"
                        + "var evObj = document.createEvent('MouseEvents');"
                        + "evObj.initEvent( 'click', true, true );"
                        + "fireOnThis.dispatchEvent(evObj);";

        WebElement el = wait_until_ElementIs_Visible(driver, locator);
        ((JavascriptExecutor) driver).executeScript(code, el);
        waitForPageToLoad(driver);
    }

    public void jsFocusAndClick(WebDriver driver, By locator) {
        WebElement element = wait_until_ElementIs_Present(driver, locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        waitForPageToLoad(driver);
    }

    public void jsFocusAndClick(WebDriver driver, WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", el);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        waitForPageToLoad(driver);
    }

    // ************ PAGE LOAD STATE ****************************//

    // Get Page State
    public String getPageState(WebDriver driver) {
        WebElement el = driver.findElement(By.cssSelector("body"));
        String code = "return document.readyState";
        String result = (String) ((JavascriptExecutor) driver).executeScript(code, el);
        log.info("PageState-" + result);
        return result;
    }

    // Wait For Page to Load Completely
    public void waitForPageToLoad(WebDriver driver) {
        while (!getPageState(driver).equals("complete")) {
            sleep(1);
        }
    }

    // Wait for Page title to change
    public void waitForPageTitleToChange(WebDriver driver, String title) {
        while (driver.getTitle().equalsIgnoreCase(title)) {
            sleep(1);
        }
        while (driver.getTitle().matches(".*?" + title + ".*?")) {
            sleep(1);
        }
    }

    // ******************** WINDOW HANDLES ******************************//

    public int getWindowHandlesSize(WebDriver driver) {
        return driver.getWindowHandles().size();
    }

    public void switchToNewWindowHandle_After_ClickingOnGivenElement(WebDriver driver, By locator) {

        parentHandle = driver.getWindowHandle();
        wait_until_ElementIs_Clickable(driver, locator).click();
        waitForPageToLoad(driver);

        if (driver.getWindowHandles().size()
                >= 2) { // switch to a new window handle if there more than 1 window handles.
            // Switch to new window opened
            for (String winHandle : driver.getWindowHandles()) {
                if (!winHandle.equals(parentHandle)) {
                    driver.switchTo().window(winHandle);
                    log.info("Switching to Child Window handle");
                }
                driver.manage().window().maximize();
            }
        }
    }

    public void switchToParentWindowHandle(WebDriver driver) {
        driver.switchTo().window(parentHandle);
        driver.manage().window().maximize();
    }

    // ******************** FRAMES *************//

    public int getNumberOfFrames(WebDriver driver) {
        return driver.findElements(By.tagName("iframe")).size();
    }

    public void SwitchToFrame_ByNumber(int n) {
        driver.switchTo().frame(n);
    }

    public void SwitchToFrame_ByNAME_OR_ByID(String nameorID) {
        driver.switchTo().frame(nameorID);
    }

    // Switch To default Content - Works to get back from a frame
    public void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    // ************* ALERTS *********************//

    public boolean isAlertPresent_SwitchToAlert(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException ex) {
            return false;
        }
    }

    public String getAlertText(WebDriver driver) {
        alert = driver.switchTo().alert();
        return alert.getText();
    }

    public void sendTextToAlert(WebDriver driver, String text) {
        alert = driver.switchTo().alert();
        alert.sendKeys(text);
    }

    public void closeAlert(WebDriver driver, boolean acceptAlert) {
        alert = driver.switchTo().alert();
        if (acceptAlert) {
            alert.accept();
        } else {
            alert.dismiss();
        }
    }

    // ******* EXPLICIT WAITS ON SINGLE ELEMENT ******************//

    // WAIT FOR MAX TIME 15 SECS TILL THE ELEMENT IS CLICKABLE - DISPLAYED AND ENABLED
    public WebElement wait_until_ElementIs_Clickable(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 15);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // WAIT FOR MAX TIME 15 SECS TILL THE ELEMENT IS VISIBLE
    public WebElement wait_until_ElementIs_Visible(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 15);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // WAIT FOR MAX TIME 15 SECS TILL THE ELEMENT IS PRESENT
    public WebElement wait_until_ElementIs_Present(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 15);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // WAIT FOR MAX TIME 15 SECS TILL SELENIUM FINDS 2 WINDOWS
    public void wait_until_Two_Windows_Are_Available(WebDriver driver) {
        wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    }

    // ************* EXPLICIT WAITS ON MULTIPLE ELEMENTS ***************//

    // WAIT FOR MAX TIME 15 SECS TILL THE ELEMENT IS PRESENT
    public List<WebElement> wait_until_ElementsAre_Present(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 15);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // WAIT FOR MAX TIME 15 SECS TILL THE ELEMENT IS VISIBLE
    public List<WebElement> wait_until_ElementsAre_Visible(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 15);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    // ******** EXPLICIT WAITS ON PAGE TITLE,URL AND ELEMENT_NOT_PRESENT ************//

    public boolean wait_until_TitleContains(WebDriver driver, String keyword) {
        wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.titleContains(keyword));
    }

    public boolean wait_until_URL_Matches(WebDriver driver, String regex) {
        wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.urlMatches(regex));
    }

    public boolean IS_Element_NotPresent(WebDriver driver, By locator) {
        wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // *************** EXTRAS ********************//

    // REFRESH PAGE
    public void refreshPage() {
        driver.navigate().refresh();
    }

    // Sleep
    public void sleep(int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException ex) {
        } catch (IllegalArgumentException ex) {
        }
    }

    public boolean verify_Element_NotPresent(WebDriver driver, By locator) {
        return driver.findElements(locator).size() == 0;
    }

    public void wait_until_Element_is_Not_Present(WebDriver driver, By locator) {
        if (driver.findElements(locator).size() > 0) {
            sleep(2);
        }
    }

    public String randomString() {
        Date date = new Date();
        Format dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateformat.format(date);
    }

    public void clickText(String text) { // used to get focus out of a text box
        hoverAndClick(driver, By.xpath("//*[contains(text(),'" + text + "')]"));
        sleep(2);
    }

    // ************ PERFORMANCE/NETWORK **************************//

    public String getNetworkData() {
        String scriptToExecute =
                "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
        String netData = ((JavascriptExecutor) driver).executeScript(scriptToExecute).toString();
        return netData;
    }

    // ************ ROBOT **************************//

    public void setClipboardData(String string) {
        // StringSelection is a class that can be used for copy and paste operations.
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public void uploadFile(String fileLocation) {

        if (Platform.getCurrent().toString().matches(".*?(win|WIN).*?")) {
            try {
                setClipboardData(fileLocation);
                Robot robot = new Robot();

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        }

        if (Platform.getCurrent().toString().matches(".*?(mac|MAC).*?")) {
            try {
                setClipboardData(fileLocation);
                Robot robot = new Robot();

                // Cmd + Tab is needed since it launches a Java app and the browser looses focus

                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_META);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.delay(500);

                // Open Goto window

                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_META);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_G);

                // Paste the clipboard value

                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_META);
                robot.keyRelease(KeyEvent.VK_V);

                // Press Enter key to close the Goto window and Upload window

                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                robot.delay(500);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        }
    }
}
