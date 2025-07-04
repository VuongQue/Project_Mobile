package com.example.project_mobile.socket;

import android.util.Log;

import com.example.project_mobile.utils.LocalDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

public class WebSocketManager<T> {

    private StompClient stompClient;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .create();

    private Disposable stompDisposable;
    private Disposable lifecycleDisposable;

    private Type type; // Sử dụng Type thay vì Class<T>
    private String topic;

    public WebSocketManager(Type type, String topic) {
        this.type = type;
        this.topic = topic;
    }

    public void connect(OnMessageCallback<T> callback) {
//        String url = "ws://10.0.2.2:8080/ws-parking/websocket";
        String url = "ws://192.168.2.4:8080/ws-parking/websocket";

        if (stompClient != null && stompClient.isConnected()) {
            Log.d("WebSocket", "Đã kết nối rồi, không reconnect.");
            return;
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        stompClient.withClientHeartbeat(10000);
        stompClient.connect();

        lifecycleDisposable = stompClient.lifecycle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LifecycleEvent>() {
                    @Override
                    public void accept(LifecycleEvent event) throws Exception {
                        switch (event.getType()) {
                            case OPENED:
                                Log.d("WebSocket", "✅ Kết nối WebSocket thành công");
                                break;
                            case ERROR:
                                Log.e("WebSocket", "❌ Lỗi WebSocket", event.getException());
                                break;
                            case CLOSED:
                                Log.w("WebSocket", "⚠️ WebSocket bị đóng, reconnect...");
                                reconnect(callback);
                                break;
                        }
                    }
                });

        stompDisposable = stompClient.topic(topic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<StompMessage>() {
                            @Override
                            public void accept(StompMessage stompMessage) throws Exception {
                                String json = stompMessage.getPayload();
                                Log.d("WebSocket Payload", json);

                                // Deserialize using Type
                                T response = gson.fromJson(json, type);
                                callback.onMessage(response);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("WebSocket", "Lỗi khi nhận message", throwable);
                            }
                        }
                );
    }

    private void reconnect(OnMessageCallback<T> callback) {
        disconnect();
        connect(callback);
    }

    public void disconnect() {
        if (stompDisposable != null && !stompDisposable.isDisposed()) {
            stompDisposable.dispose();
        }

        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed()) {
            lifecycleDisposable.dispose();
        }

        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
    }

    public interface OnMessageCallback<T> {
        void onMessage(T response);
    }
}
