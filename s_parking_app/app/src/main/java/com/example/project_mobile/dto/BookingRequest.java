package com.example.project_mobile.dto;

public class BookingRequest {
    private long id;           // Dùng để truyền id booking khi gọi /booking/get
    private Long idParking;    // Dùng khi tạo booking hoặc trường hợp khác
    private String username;   // Dùng khi cần username

    // Constructor mặc định
    public BookingRequest() {}

    // Constructor cho tạo booking: idParking + username
    public BookingRequest(Long idParking, String username) {
        this.idParking = idParking;
        this.username = username;
    }

    // Constructor cho getBookingById: chỉ id booking
    public BookingRequest(long id) {
        this.id = id;
    }

    // Getter & Setter cho tất cả trường
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getIdParking() {
        return idParking;
    }

    public void setIdParking(Long idParking) {
        this.idParking = idParking;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
