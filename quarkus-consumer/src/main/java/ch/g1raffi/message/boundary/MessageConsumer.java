package ch.g1raffi.message.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@ApplicationScoped
public class MessageConsumer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    ObjectMapper objectMapper;

    private final Logger log = Logger.getLogger(MessageConsumer.class.toString());
    private final ExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    void onStartup(@Observes StartupEvent event) {
        scheduler.submit(this);
    }

    void onShutdown(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createTopic("TestTopic"));
            while (true) {
                Message message = consumer.receive();
                if (message == null) return;
                log.info(">> Message received: " + message.getBody(String.class));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}