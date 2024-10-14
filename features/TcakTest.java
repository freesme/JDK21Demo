public class TcakTest {
    public static void main(String[] args) {
        String str = "你好 %s";

        String lisi = str.formatted("lisi");
        System.out.println(lisi);
    }
}
