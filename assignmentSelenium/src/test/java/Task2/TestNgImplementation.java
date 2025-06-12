package Task2;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.*;
import java.time.Duration;

public class TestNgImplementation {

    WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @DataProvider(name = "testData")
    public Object[][] getTestData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("testdata/test_data.csv"));
        String line;
        int rows = 0;
        while ((line = br.readLine()) != null) rows++;
        br.close();

        Object[][] data = new Object[rows - 1][3];
        BufferedReader reader = new BufferedReader(new FileReader("testdata/test_data.csv"));
        reader.readLine(); // skip header
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            data[i][0] = parts[0]; // param1
            data[i][1] = parts[1]; // param2
            data[i][2] = parts[2]; // expected
            i++;
        }
        reader.close();
        return data;
    }

    @Test(dataProvider = "testData")
    public void testPythonAddition(String input1, String input2, String expectedOutput) throws InterruptedException {
        driver.get("https://www.codechef.com/ide");

        // Select Python3
        driver.findElement(By.xpath("//div[@id='language-select']")).click();
        driver.findElement(By.xpath("//li[normalize-space()='Python3']")).click();

        // Load Python code from file
        String pythonCode = "";
        try {
            pythonCode = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("scripts/generated_function.py")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Paste Python code into editor
        WebElement editor = driver.findElement(By.xpath("//div[@class='ace_content']"));
        new Actions(driver).moveToElement(editor).click()
                .sendKeys(Keys.CONTROL + "a", Keys.DELETE)
                .sendKeys(pythonCode).perform();

        // Enter test input
        WebElement inputBox = driver.findElement(By.xpath("//textarea[@placeholder='Enter Input here']"));
        inputBox.clear();
        inputBox.sendKeys(input1 + " " + input2);

        // Click Run
        driver.findElement(By.xpath("//button[@id='compile_btn']")).click();
        Thread.sleep(8000); // wait for execution

        // Fetch output
        String outputRaw = driver.findElement(By.xpath("(//span[contains(@class, 'value_58rxo')])[4]")).getText();
        String actualResult = outputRaw.replace("Result: ", "").trim();

        // Assertion
        Assert.assertEquals(actualResult, expectedOutput, "Test failed for input: " + input1 + " + " + input2);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}

