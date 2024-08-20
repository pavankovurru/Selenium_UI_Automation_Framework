# SELENIUM WEB DRIVER - UI AUTOMATION FRAMEWORK


## FEATURES

```
Compatible with Major Latest Selenium Release - 4
```

##  FRAME WORK STACK 

```
1. Selenium 4.20.0
2. LOG4J 2.23.1
3. TestNG 6.14.3
4. Gradle 8.5
```


## IMPORTANT FILES  

1) `src/test/resources/testNG.xml` -- All Environment related data goes here including `url` , `browser` , `browser version` & `targetRun`
   `targetRun` - can be `local or  browserStack`, browser version is ignored if `targetRun=local`
 
2) `src/main/java/com/company/project/utilities/SeleniumUtil.java`  -- Has Utility functions that can be used in Selenium tests including Selenium Web Driver Creation.

3) `src/main/java/com/company/project/pages` -- All Web Pages Business logic goes here.

4) `src/test/java/com/company/project/tests` -- All Tests go here, will have function calls to the page methods and will have assertions accordingly.

`NOTE : ALL OTHER ENVIRONMENT RELATED DETAILS SHOULD BE IN TESTNG XML's so that tests can run in any environment without any code changes`

                                   `CI --> GRADLE TASK --> TESTNG XML --> TESTS`

