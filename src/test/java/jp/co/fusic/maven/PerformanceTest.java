package jp.co.fusic.maven;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import java.util.HashMap;
import java.util.Map;
import org.fluentd.logger.FluentLogger;

public class PerformanceTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PerformanceTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PerformanceTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testPerformance()
    {
        String target_url = System.getProperty("target_url");
        String fluentd_tag = System.getProperty("fluentd_tag");
        String fluentd_host = System.getProperty("fluentd_host");
        FluentLogger logger;
        try
        {
            logger = FluentLogger.getLogger(fluentd_tag, fluentd_host, 24224);
        }
        finally
        {
        }
        WebDriver driver = new FirefoxDriver();
        Wait<WebDriver> wait = new WebDriverWait(driver, 60);
        try {
            driver.get(target_url);
            wait.until(new ExpectedCondition<Boolean>()
            {
                public Boolean apply(WebDriver driver)
                {
                    return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
                }
            });
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Long loadEventEnd = (Long) js.executeScript("return window.performance.timing.loadEventEnd;");
            Long navigationStart = (Long) js.executeScript("return window.performance.timing.navigationStart;");
            Long pageLoadTime = loadEventEnd-navigationStart;
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("page_load_time", pageLoadTime);
            logger.log(fluentd_tag + ".test", data);
        }
        finally
        {
            driver.quit();
        }
    }
}
