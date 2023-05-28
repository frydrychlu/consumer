package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.domain.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class BasketController {

    @PostMapping("/basket")
    public ResponseEntity<Integer> processBasket(@RequestBody List<String> products) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        int toPay = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        for (String productName : products) {
            HttpGet request = new HttpGet(String.format("http://localhost:8080/products/%s", productName));
            HttpResponse response = httpClient.execute(request);

            InputStream inputStream = response.getEntity().getContent();
            Product product = objectMapper.readValue(inputStream, Product.class);
            toPay += product.getPrice();
        }

        return ResponseEntity.ok(toPay);
    }
}
