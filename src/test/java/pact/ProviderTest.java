package pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
public class ProviderTest {

    @Pact(consumer = "Consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Product product = new Product();
        product.setName("Pepsi");
        product.setPrice(6);
        return builder
                .given("Product data")
                .uponReceiving("A request to /product/Pepsi")
                .path("/product/Pepsi")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .stringValue("name", "Pepsi")
                        .integerType("price", 6)
                )
                .toPact();
    }


    @PactTestFor(providerName = "Provider", port = "1234") // Use a random unused port
    @Test
    void testOnConsumerSide(MockServer mockServer) throws IOException, URISyntaxException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(new URI(mockServer.getUrl() + "/product/Pepsi"));
        org.apache.http.HttpResponse response = httpClient.execute(request);

        InputStream inputStream = response.getEntity().getContent();
        Product product = objectMapper.readValue(inputStream, Product.class);

        assertEquals(response.getStatusLine().getStatusCode(), 200);
    }
}
