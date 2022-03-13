import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AmazonTestScenario {
	String CSV_PATH = "/Users/bhushah/Downloads/TestData.csv";
    WebDriver driver;
    BufferedReader br;
    String[] csvkeyValues;
    Map<String,String> map = new HashMap<String,String>();
    
	@BeforeTest
    public void setup() throws Exception {
		driver=new ChromeDriver();
		System.setProperty("webdriver.chrome.driver", "//Users//bhushah//Documents//Work//chromedriver");
		driver.get("https://amazon.in");
		driver.manage().window().maximize();
		System.out.println(driver.getTitle());
		br = new BufferedReader(new FileReader(new File(CSV_PATH)));
		//csvReader = new CSVReader(new FileReader(CSV_PATH));
		String[] csvkeyValues=br.readLine().split(",");
		for(String csvkeyValue : csvkeyValues) {
			String csvkey=csvkeyValue.split("=")[0];
			String csvValue=csvkeyValue.split("=")[1];
			map.put(csvkey, csvValue);
		}		
    }
	
	@Test
	public void test() {
		
		driver.findElement(By.xpath("//*[contains(text(),'Hello, Sign in')]")).click();
		driver.findElement(By.name("email")).sendKeys(map.get("Email"));
		driver.findElement(By.id("continue")).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.name("password")).sendKeys(map.get("Password"));
		driver.findElement(By.cssSelector("#signInSubmit")).click();
		
		if(map.containsKey("verifyEmptyCart")) {
			driver.findElement(By.id("nav-cart")).click();
			WebDriverWait wait=new WebDriverWait(driver,30);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Your Amazon Cart is empty.')]")));
			Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'Your Amazon Cart is empty.')]")).isDisplayed(),"Your cart is not Empty");
			
		}
		if(map.containsKey("searchItemAndClick")) {
			WebElement ele=driver.findElement(By.id("twotabsearchtextbox"));
			ele.sendKeys(map.get("searchItemAndClick"));
			ele.sendKeys(Keys.ENTER);
			List<WebElement> allElements=driver.findElements(By.xpath("//*[contains(@class,'s-line-clamp-2')]"));
			allElements.get(2).click();
		}
		if(map.containsKey("addToCart")) {
			ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
		    driver.switchTo().window(tabs2.get(1));
			driver.findElement(By.xpath("//*[@title='Add to Shopping Cart']")).click();
			driver.findElement(By.id("attachSiNoCoverage")).click();
			Assert.assertEquals(driver.findElement(By.xpath("//*[contains(@aria-label,'1 item')]")).isDisplayed(), true);
		
		}
		if(map.containsKey("deleteFromCart")) {
			driver.findElement(By.id("nav-cart")).click();
			driver.findElement(By.xpath("//*[@data-action='quantity']//following-sibling::*[@data-action='delete']//input")).click();
			Assert.assertEquals(driver.findElement(By.xpath("//*[@id='nav-cart-count' and text()='0']")).isDisplayed(), true);
		}
		driver.close();
	}
}
