package com.example.project_mobile.utils;

import android.os.Build;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();

        // Kiểm tra nếu chuỗi là null hoặc rỗng
        if (dateString == null || dateString.isEmpty()) {
            return null;  // Hoặc bạn có thể thay thế bằng LocalDateTime.now() nếu cần giá trị mặc định
        }

        try {
            // Sử dụng DateTimeFormatter để chuyển chuỗi thành LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(dateString, formatter);
        } catch (Exception e) {
            throw new JsonParseException("Không thể parse chuỗi thành LocalDateTime: " + dateString, e);
        }
    }
}
