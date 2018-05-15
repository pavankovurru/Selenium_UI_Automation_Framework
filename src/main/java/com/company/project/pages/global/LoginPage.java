package com.company.project.pages.global;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class LoginPage {

  public WebDriver driver;
  public static Logger log = LogManager.getLogger();

  public LoginPage(WebDriver driver) {
    this.driver = driver;
  }

  public String logIn() {
    log.info("PAGE TITLE BEFORE USER LOGIN-" + driver.getTitle());
    return driver.getTitle();
  }
}
