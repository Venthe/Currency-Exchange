package eu.venthe.interview.nbp_web_proxy;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "logging.level.eu.venthe=trace"
})
public abstract class AbstractBaseIntegrationTest {
}
