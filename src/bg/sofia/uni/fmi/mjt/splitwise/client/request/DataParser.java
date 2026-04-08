package bg.sofia.uni.fmi.mjt.splitwise.client.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;

public interface DataParser {
    Data parse(String[] args);
}
