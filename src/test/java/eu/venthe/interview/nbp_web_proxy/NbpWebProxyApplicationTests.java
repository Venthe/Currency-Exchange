package eu.venthe.interview.nbp_web_proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NbpWebProxyApplicationTests {

	@LocalServerPort
	Integer port;

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnUpForHealthEndpoint() {
		String baseUrl = "http://localhost:" + port + "/actuator/health";
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody()).contains("\"status\":\"UP\"");
	}

}
