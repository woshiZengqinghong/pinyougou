import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
        Map map = new HashMap();
        Object a = map.get("a"); //空指针异常
        System.out.println(a);
    }
}
