package Task1;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;

public class UIAutomation {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait;
        
        try {
            // Configuration
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            
         // Step 1: Open ChatGPT and request code
//            driver.get("https://chat.openai.com");
//
//            WebElement promptBox = driver.findElement(By.xpath("//div[@id='prompt-textarea']"));
//            promptBox.sendKeys("Please provide a Python function that accepts parameters from the command line, performs addition on those parameters, and return the result.");
//
//            WebElement sendButton = driver.findElement(By.xpath("//button[@id='composer-submit-button']"));
//            sendButton.click();
//            Thread.sleep(10000); // wait for response
//
//            WebElement copyBtn = driver.findElement(By.xpath("(//button[@aria-label='Copy'])[1]"));
//            copyBtn.click();
            
            // Step 1: Get Python code
            String pythonCode = "def add_numbers():\n" +
                    "    \"\"\"\n" +
                    "    Reads two numbers from stdin and returns their sum.\n" +
                    "    \"\"\"\n" +
                    "    try:\n" +
                    "        num1, num2 = map(float, input().split())\n" +
                    "        return num1 + num2\n" +
                    "    except ValueError:\n" +
                    "        return \"Error: Please enter two valid numbers separated by a space.\"\n" +
                    "\n" +
                    "if __name__ == \"__main__\":\n" +
                    "    print(add_numbers())";
            
            // Save Python script
            savePythonScript(pythonCode, "scripts/generated_function.py");
            
            // Step 2: Open CodeChef IDE
            driver.get("https://www.codechef.com/ide");
            wait.until(ExpectedConditions.titleContains("CodeChef"));
            
            // Step 3: Select Python3
            selectLanguage(driver, wait, "Python3");
            
            // Step 4: Paste the code
            pasteCodeToEditor(driver, wait, pythonCode);
            
            // Step 5: Process test data and update existing report
            updateHtmlReport(driver, wait, "reports/task1_report.html");
            
        } catch (Exception e) {
            e.printStackTrace();
            takeScreenshot(driver, "error_screenshot.png");
        } finally {
//            driver.quit();
        }
    }
    
    // Helper Methods
    
    private static void savePythonScript(String code, String path) throws IOException {
        Path scriptPath = Paths.get(path);
        Files.createDirectories(scriptPath.getParent());
        Files.write(scriptPath, code.getBytes(StandardCharsets.UTF_8));
        System.out.println("Script saved to: " + scriptPath.toAbsolutePath());
    }
    
    private static void selectLanguage(WebDriver driver, WebDriverWait wait, String language) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='language-select']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[normalize-space()='" + language + "']"))).click();
    }
    
    private static void pasteCodeToEditor(WebDriver driver, WebDriverWait wait, String code) {
        String pythonCode = "";
        try {
            pythonCode = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("scripts/generated_function.py")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Paste Python code into editor
        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ace_content']")));
        new Actions(driver).moveToElement(editor).click()
                .sendKeys(Keys.CONTROL + "a", Keys.DELETE)
                .sendKeys(pythonCode).perform();
    }
    
    
    private static void updateHtmlReport(WebDriver driver, WebDriverWait wait, String reportPath) throws Exception {
        Path reportFile = Paths.get(reportPath);
        
        // 1. Verify report file exists with proper marker
        if (!Files.exists(reportFile)) {
            throw new FileNotFoundException("Report template not found at: " + reportFile.toAbsolutePath());
        }
        
        // 2. Read existing report content
        String reportContent = new String(Files.readAllBytes(reportFile));
        
        // 3. Verify the marker exists
        String marker = "<!-- Test rows will be inserted dynamically in code -->";
        if (!reportContent.contains(marker)) {
            throw new Exception("Could not find insertion marker in HTML report");
        }
        
        // 4. Process test data
        List<String> reportRows = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new FileReader("testdata/test_data.csv"))) {
            csvReader.readLine(); // skip header
            
            String row;
            int index = 0;
            while ((row = csvReader.readLine()) != null) {
                index++;
                String[] data = row.split(",");
                String input = data[0].trim() + " " + data[1].trim();
                String expected = data[2].trim();
                
                // Enter input and run test (your existing code)
                WebElement inputBox = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//textarea[@placeholder='Enter Input here']")));
                inputBox.clear();
                inputBox.sendKeys(input);
                driver.findElement(By.id("compile_btn")).click();
                waitForExecutionComplete(wait);
                
                // Get results
                Map<String, String> results = getExecutionResults(wait);
                
                // Create report row (preserving your HTML structure)
                String testStatus = results.get("output").equals(expected) ? "PASS" : "FAIL";
                reportRows.add(String.format(
                    "<tr class='%s'>" +
                    "<td>%d</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "<td>%s</td>" +
                    "</tr>",
                    testStatus.toLowerCase(), 
                    index, 
                    input, 
                    expected, 
                    results.get("output"), 
                    testStatus, 
                    results.get("time"), 
                    results.get("memory")
                ));
            }
        }
        
        // 5. Update report content
        String updatedContent = reportContent.replace(marker, String.join("\n", reportRows));
        
        // 6. Write updated report (backup original first)
        Files.copy(reportFile, Paths.get(reportPath + ".bak"), StandardCopyOption.REPLACE_EXISTING);
        Files.write(reportFile, updatedContent.getBytes());
        
        System.out.println("Report successfully updated at: " + reportFile.toAbsolutePath());
    }
    
    private static void waitForExecutionComplete(WebDriverWait wait) {
        wait.until(d -> {
            String status = d.findElement(By.xpath("(//span[contains(@class, 'value_')])[1]")).getText();
            return !status.equals("Running...");
        });
    }
    
    private static Map<String, String> getExecutionResults(WebDriverWait wait) {
        Map<String, String> results = new HashMap<>();
        
        // Get status
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//span[contains(@class, 'value_')])[1]")));
        results.put("status", statusElement.getText());
        
        // Get time
        WebElement timeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//span[contains(@class, 'value_')])[2]")));
        results.put("time", timeElement.getText());
        
        // Get memory
        WebElement memoryElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//span[contains(@class, 'value_')])[3]")));
        results.put("memory", memoryElement.getText());
        
        // Get output
        WebElement outputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//span[contains(@class, 'value_')])[4]")));
        results.put("output", outputElement.getText().replace("Result: ", "").trim());
        
        return results;
    }
    
    private static void takeScreenshot(WebDriver driver, String filename) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("screenshots/" + filename));
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}

