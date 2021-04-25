import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MessageConsumer {
    private static final DAO purchaseDao = new DAO();
    public static final String TOPIC = "supermarket";
    private static KafkaConsumer<String, String> consumer;

    public static void main(String[] argv) throws Exception {

        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "54.152.226.111:9092");
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        consumer = new KafkaConsumer<String, String>(prop);
        consumer.subscribe(Arrays.asList(TOPIC));

        System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

        while(true){
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for(ConsumerRecord record : records){
                String message = record.value().toString();
                processMessage(message);
            }
        }

    }


    protected static void processMessage(String message){

        JSONObject obj = (JSONObject) JSONValue.parse(message);

        String store_id = (String) obj.get("store_id");
        String customer_id = (String) obj.get("customer_id");
        String date = (String) obj.get("date");
        // USE UUID to generate purchase_id
        String purchase_id = (String) obj.get("purchase_id");
        String items = (String) obj.get("items");

        purchaseDao.createPurchase(purchase_id, store_id, customer_id, date, items);

    }
}
