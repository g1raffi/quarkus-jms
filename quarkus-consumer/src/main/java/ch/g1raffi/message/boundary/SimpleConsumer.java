package ch.g1raffi.message.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "TestTopic"
        ),
        @ActivationConfigProperty(
                propertyName =  "destinationType",
                propertyValue = "javax.jms.Topic"
        )
})
public class SimpleConsumer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    ObjectMapper objectMapper;

    private final Logger log = Logger.getLogger(SimpleConsumer.class.toString());
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
            JMSConsumer consumer = context.createConsumer(context.createTopic("SimpleTopic?consumer.retroactive=true"));
            while (true) {
                Message message = consumer.receive();
                if (message == null) return;
                log.info(">> Simple received: " + message.getBody(String.class));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}