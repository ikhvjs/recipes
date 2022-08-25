package github.ikhvjs.recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {
    public static String toJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
