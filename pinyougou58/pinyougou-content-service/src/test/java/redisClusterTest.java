import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring/spring-redis-cluster.xml")
@RunWith(SpringRunner.class)
public class redisClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void fun1(){
        redisTemplate.boundValueOps("aaaa").set("helloWorld");
        System.out.println(redisTemplate.boundValueOps("aaaa").get());
    }
}
