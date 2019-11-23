package ch.g1raffi.message.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Session;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class MessageProducer implements Runnable {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ConnectionFactory connectionFactory;

    private final Logger log = Logger.getLogger(MessageProducer.class.toString());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    void onStart(@Observes StartupEvent event) {
        scheduler.scheduleWithFixedDelay(this, 0L, 5L, TimeUnit.SECONDS);
    }

    void onStop(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            double random = Math.random();
            log.info(">> Sending message: " + random);
            context.createProducer().send(context.createQueue("Messages"), objectMapper.writeValueAsString(random));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
