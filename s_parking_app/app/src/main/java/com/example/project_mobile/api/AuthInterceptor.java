package com.example.project_mobile.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenProvider tokenProvider;

    public AuthInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Không chèn token với các API công khai
        if (isPublicApi(path)) {
            return chain.proceed(originalRequest);
        }

        String token = getTokenSafely();

        // Kiểm tra null và isEmpty() một cách an toàn
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest); // Không có token, gửi request như bình thường
        }

        // Tạo request mới có thêm Authorization header
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }

    /**
     * Kiểm tra xem API có phải là public không (không cần token)
     */
    private boolean isPublicApi(String path) {
        return path.startsWith("/auth/login") ||
                path.startsWith("/auth/send-otp") ||
                path.startsWith("/auth/verify-otp") ||
                path.startsWith("/auth/update-info") ||
                path.startsWith("/auth/reset-password") ||
                path.startsWith("/parking-lots/available") ||
                path.startsWith("/parking-area/all") ||
                path.startsWith("/img/all");
    }

    /**
     * Lấy token một cách an toàn
     */
    private String getTokenSafely() {
        try {
            String token = tokenProvider.getToken();
            return token != null ? token : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Interface để lấy token.
     */
    public interface TokenProvider {
        String getToken() throws Exception;
    }
}