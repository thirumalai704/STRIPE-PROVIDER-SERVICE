package com.hulkhiretech.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonUtil {

	private final ObjectMapper objectMapper;

	// convert json string to java object provided type return null or error

	public <T> T convertJsonToObject(String json, Class<T> clazz) {
		if (json == null) {
			log.debug("convertJsonToObject called with null json");
			return null;
		}
		if (clazz == null) {
			log.debug("convertJsonToObject called with null clazz");
			return null;
		}
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			log.error("Failed to convert JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
			return null;
		}
	}
	// convert java object to json String return null or error

	public String convertObjectToJson(Object object) {
		if (object == null) {
			log.warn("JSON conversion skipped because object is null");
			return null;
		}

		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			log.error("Failed to convert object to JSON. Object type: {}", object.getClass().getSimpleName(), e);
			return null;
		}
	}
}
