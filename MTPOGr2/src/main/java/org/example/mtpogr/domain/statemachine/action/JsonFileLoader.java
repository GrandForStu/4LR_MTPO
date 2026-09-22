package org.example.mtpogr.domain.statemachine.action;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
public class JsonFileLoader {
    private final ObjectMapper objectMapper;

    public JsonFileLoader() {
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

//    public List<Order> loadOrders(String filePath) throws IOException {
//        return loadOrders(Path.of(filePath));
//    }

    public List<Order> loadOrders(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("JSON file not found: " + filePath);
        }

        if (Files.size(filePath) == 0L) {
            return Collections.emptyList();
        }

        return objectMapper.readValue(filePath.toFile(), new TypeReference<List<Order>>() {});
    }

//    public void saveOrders(List<Order> orders, String filePath) throws IOException {
//        saveOrders(orders, Path.of(filePath));
//    }

    public void saveOrders(List<Order> orders, Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<Order> data = orders == null ? Collections.emptyList() : orders;
        objectMapper.writeValue(filePath.toFile(), data);
    }

}
