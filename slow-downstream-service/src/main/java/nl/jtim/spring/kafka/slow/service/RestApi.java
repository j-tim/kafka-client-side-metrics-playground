package nl.jtim.spring.kafka.slow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Thread.*;

@RestController
@Slf4j
public class RestApi {

    @GetMapping("/api/greet")
    public Greeting greeting() {
        Greeting greeting = new Greeting("Hello World!");

        try {
            sleep(50);
            log.info("Greeting: {}", greeting);
        } catch (InterruptedException e) {
            log.info("Whoops");
        }

        return greeting;
    }
}
