package eu.venthe.interview.nbp_web_proxy.shared_kernel;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class ClockService {
    public ZonedDateTime getZonedNow() {
        return ZonedDateTime.now();
    }
}
