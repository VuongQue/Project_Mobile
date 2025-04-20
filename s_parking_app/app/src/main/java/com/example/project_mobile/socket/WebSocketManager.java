package com.example.project_mobile.socket;

import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

public class WebSocketManager<T> {

    private StompClient stompClient;
    private Gson gson = new Gson();
    private Disposable stompDisposable;
    private Disposable lifecycleDisposable;

    private Class<T> type; // dùng để deserialize JSON
    private String topic;

    public WebSocketManager(Class<T> type, String topic) {
        this.type = type;
        this.topic = topic;
    }

    public void connect(OnMessageCallback<T> callback) {
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
