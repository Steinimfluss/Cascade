package me.tom.cascade;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class MojangUUIDAdapter implements JsonDeserializer<UUID> {
	@Override
	public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		String raw = json.getAsString();
	    String dashed = raw.replaceFirst(
	        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
	        "$1-$2-$3-$4-$5"
	    );
	    return UUID.fromString(dashed);
	}
}