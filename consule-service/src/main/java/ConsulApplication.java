import com.zbw.config.TestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.zbw"})
@EnableConfigurationProperties
public class ConsulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsulApplication.class, args);
    }

}
