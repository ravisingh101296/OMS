package com.example.order_management.util;

import com.example.order_management.dto.CreateOrderRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class RequestHashUtil {

    private RequestHashUtil(){}


    public static String hash(CreateOrderRequest request) {
        Map<UUID, Integer> quantitiesByProduct = new TreeMap<>();

        for (var item : request.items()) {
            quantitiesByProduct.merge(
                    item.id(),
                    item.quantity(),
                    Math::addExact
            );
        }

        String canonicalRequest = quantitiesByProduct.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining("|"));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    canonicalRequest.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

}
