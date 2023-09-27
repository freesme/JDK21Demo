import java.util.Date;

import static java.util.FormatProcessor.FMT;

/**
 * <a href="https://openjdk.org/jeps/430">JEP 430</a><br/>
 * 总览<br/>
 * 使用字符串模板增强Java编程语言。字符串模板通过将文本与嵌入表达式和模板处理器耦合来产生专门的结果，
 * 从而补充了Java现有的字符串文字和文本块。这是一个预览语言特性和API<br/>
 * <br/>目标
 * <br/>简化Java程序的编写，使其易于表达包含在运行时计算的值的字符串。
 * <br/>增强混合文本和表达式的表达式的可读性，无论文本是适合于单个源行(如字符串字面值)还是跨越多个源行(如文本块)。
 * <br/>通过支持模板及其嵌入表达式的值的验证和转换，提高从用户提供的值组成字符串并将其传递给其他系统(例如，为数据库构建查询)的Java程序的安全性。
 * <br/>通过允许Java库定义字符串模板中使用的格式化语法来保持灵活性。
 * <br/>简化接受非java语言(如SQL、XML和JSON)编写的字符串的api的使用。
 * <br/>允许创建从文字文本和嵌入表达式计算的非字符串值，而无需通过中间字符串表示进行转换。
 * <br/>
 * <br/>模板表达式是Java编程语言中一种新的表达式。模板表达式可以执行字符串插值，但也可以通过一种帮助开发人员安全高效地组合字符串的方式进行编程。
 * 此外，模板表达式并不局限于组合字符串——它们可以根据特定于领域的规则将结构化文本转换为任何类型的对象。
 */

public class StringTemplateDemo {
    public static void main(String[] args) {

        strDescription();
        // STR处理器
        strProcessor();
        // 多行文本
        System.out.println(strMultipleLines());
        // 带格式说明的插值
        System.out.println(fmtMultipleLines());

    }

    private static void strProcessor() {
        // 嵌入式表达式可以是字符串
        String firstName = "Bill";
        String lastName = "Duck";
        String fullName = STR. "\{ firstName } \{ lastName }" ;
        System.out.println(fullName);
        // "Bill Duck"
        String sortName = STR. "\{ lastName }, \{ firstName }" ;
        System.out.println(sortName);
        // "Duck, Bill"

        // 嵌入式表达式可以执行算术
        int x = 10, y = 20;
        String str1 = STR. "\{ x } + \{ y } = \{ x + y }" ;
        System.out.println(str1);
        // "10 + 20 = 30"

        // 嵌入式表达式可以调用方法和访问字段
        String str2 = STR. "You have a \{ getOfferType() } waiting for you!" ;
        System.out.println(str2);
        //"You have a gift waiting for you!"
        RequestExample req = new RequestExample(new Date(), "12:00", "8.8.8.8");
        String str3 = STR. "Access at \{ req.date } \{ req.time } from \{ req.ipAddress }" ;
        System.out.println(str3);
        // "Access at Wed Sep 27 15:49:37 CST 2023 12:00 from 8.8.8.8"

        boolean t = true;
        // 可以在嵌入表达式中使用双引号字符，而不用将它们转义为\"。
        // 这意味着嵌入表达式可以出现在模板表达式中，就像它出现在模板表达式外部一样，从而简化了从连接(+)到模板表达式的转换
        String temp = STR. "YES Or NO: \{ t ? "YES" : "NO" } " ;
        System.out.println(temp);
    }

    /**
     * 模板处理器(STR);
     * 点字符(U+002E)，见于其他类型的表达式;和
     * 一个模板("My name is \{name}")，它包含一个内嵌表达式(\{name})。
     * 当在运行时计算模板表达式时，它的模板处理器将模板中的文本与嵌入表达式的值结合起来以产生结果。模板处理程序的结果，
     * 以及对模板表达式求值的结果，通常是一个String —— (尽管并非总是如此)
     */
    private static void strDescription() {
        // 从语法上讲，模板表达式类似于带前缀的字符串字面值
        String name = "Joan";
        // 这是一个模板表达式
        String info = STR. "My name is \{ name }" ;
        System.out.println(info.equals("My name is Joan"));     // true
    }

    /**
     * 多行文本处理
     */
    private static String strMultipleLines() {
        String title = "My Web Page";
        String text = "Hello, world";
        return STR. """
        <html>
          <head>
            <title>\{ title }</title>
          </head>
          <body>
            <p>\{ text }</p>
          </body>
        </html>
        """ ;
    }


    /**
     * FMT类似于STR，因为它执行插值，但它也解释出现在嵌入表达式左侧的格式说明符。
     * 格式说明符与java.util.Formatter中定义的格式说明符相同。下面是区域表示例，按模板中的格式说明符整理:
     */
    public static String fmtMultipleLines() {
        record Rectangle(String name, double width, double height) {
            double area() {
                return width * height;
            }
        }
        Rectangle[] zone = new Rectangle[]{new Rectangle("Alfa", 17.8, 31.4), new Rectangle("Bravo", 9.6, 12.4), new Rectangle("Charlie", 7.1, 11.23),};
        return FMT. """
        Description     Width    Height     Area
        %-12s\{ zone[0].name }  %7.2f\{ zone[0].width }  %7.2f\{ zone[0].height }     %7.2f\{ zone[0].area() }
        %-12s\{ zone[1].name }  %7.2f\{ zone[1].width }  %7.2f\{ zone[1].height }     %7.2f\{ zone[1].area() }
        %-12s\{ zone[2].name }  %7.2f\{ zone[2].width }  %7.2f\{ zone[2].height }     %7.2f\{ zone[2].area() }
        \{ " ".repeat(28) } Total %7.2f\{ zone[0].area() + zone[1].area() + zone[2].area() }
        """ ;
    }

    public static String getOfferType() {
        return "gift";
    }

    record RequestExample(Date date, String time, String ipAddress) {
    }
}