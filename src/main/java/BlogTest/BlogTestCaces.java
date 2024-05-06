package BlogTest;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BlogTestCaces {
    static WebDriver webDriver;
    @BeforeAll
    static void SetUp() {
        webDriver = new ChromeDriver();
    }
    @AfterAll
    static void TearDown() {
        webDriver.quit();
    }

    // 测试博客登录功能
    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = "/LoginTest.csv")
    void LoginTest(String username, String password, String blog_list_url) throws InterruptedException {
        webDriver.get("http://127.0.0.1:8080/login.html");
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        webDriver.findElement(By.cssSelector("#username")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        webDriver.findElement(By.cssSelector("#submit")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        // 验证登录之后的 url 和 username 是否正确
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(blog_list_url, cur_url);
        String cur_username = webDriver.findElement(By.cssSelector("#username")).getText();
        Assertions.assertEquals(username, cur_username);
    }

    // 测试博客数量是否正确
    @Order(2)
    @Test
    void BlogList() {
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        int title_num = webDriver.findElements(By.cssSelector(".blog")).size();
        int show_num = Integer.parseInt(webDriver.findElement(By.cssSelector("#artCount")).getText());
        // 如果文章数量与左侧文章数相同则测试通过
        System.out.println(title_num);
        Assertions.assertEquals(show_num, title_num);
    }

    // 测试查看全文功能
    public static Stream<Arguments> Generator() {
        return Stream.of(Arguments.arguments("http://127.0.0.1:8080/blog_content.html?id=1", "博客正文", "Java"));
    }
    @Order(3)
    @ParameterizedTest
    @MethodSource("Generator")
    void BlogDetail(String expected_url, String expected_title, String expected_blog_title) {
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // 点击查看全文按钮（这里用css选择器会比较麻烦）
        webDriver.findElement(By.xpath("//*[@id=\"artDiv\"]/div/a[1]")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // 校验博客列表页的链接、标题、博客标题
        String cur_url = webDriver.getCurrentUrl();
        String cur_title = webDriver.getTitle();
        String cur_blog_title = webDriver.findElement(By.cssSelector("#title")).getText();
        Assertions.assertEquals(expected_url,cur_url);
        Assertions.assertEquals(expected_title, cur_title);
        Assertions.assertEquals(expected_blog_title, cur_blog_title);
    }

    // 测试写博客功能
    @Order(4)
    @Test
    void AddBlog() throws InterruptedException {
        webDriver.findElement(By.xpath("/html/body/div[1]/a[2]")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        ((JavascriptExecutor) webDriver).executeScript("document.getElementById(\"title\").value=\"测试\";");

        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webDriver.findElement(By.xpath("/html/body/div[2]/div[1]/button")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        String cur_url = webDriver.getCurrentUrl();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        Assertions.assertEquals("http://127.0.0.1:8080/myblog_list.html", cur_url);
    }

    // 测试博客删除功能
    @Order(5)
    @Test
    void DeleteBlog() {
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webDriver.findElement(By.xpath("//*[@id=\"artDiv\"]/div[1]/a[1]")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webDriver.findElement(By.xpath("//*[@id=\"artDiv\"]/div[1]/a[3]")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // 弹出框的问题尚未解决
    }

    // 测试退出登录功能
    @Order(6)
    @Test
    void Logout() {
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        webDriver.findElement(By.xpath("/html/body/div[1]/a[3]")).click();
        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals("http://127.0.0.1:8080/login.html", cur_url);
    }
}
