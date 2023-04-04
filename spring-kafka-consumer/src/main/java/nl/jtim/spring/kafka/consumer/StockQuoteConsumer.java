package nl.jtim.spring.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class StockQuoteConsumer {

    public final static String STOCK_QUOTES_TOPIC_NAME = "stock-quotes";

    private final RestTemplate restTemplate;

    public StockQuoteConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = STOCK_QUOTES_TOPIC_NAME)
    public void on(nl.jtim.spring.kafka.avro.stock.quote.StockQuote stockQuote, @Header(KafkaHeaders.RECEIVED_PARTITION) String partition) {
        log.info("Consumed from partition: {} value: {}", partition, stockQuote);

        ResponseEntity<Greeting> reponse = restTemplate.getForEntity("http://localhost:7999/api/greet", Greeting.class);
    }
}
