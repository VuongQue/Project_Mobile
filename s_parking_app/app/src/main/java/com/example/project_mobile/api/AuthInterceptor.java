package com.example.project_mobile.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthInterceptor implements Interceptor {

    private TokenProvider tokenProvider;
    public AuthInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Không chèn token với các API công khai
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            return chain.proceed(originalRequest);
        }

        String token = tokenProvider.getToken();

        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest); // fallback không có token
        }

        // Tạo request mới có thêm Authorization header
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }

    public interface TokenProvider {
        String getToken();
    }
}

