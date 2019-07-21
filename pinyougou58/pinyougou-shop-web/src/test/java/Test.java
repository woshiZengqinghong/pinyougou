import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456");
        System.out.println(encode);
        String encode1 = encoder.encode("123456");
        System.out.println(encode1);
    }
}
