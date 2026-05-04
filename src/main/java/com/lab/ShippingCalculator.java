package com.lab;

public class ShippingCalculator {

    public double calculate(double weight, String type) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        if (type.equals("EXPRESS")) return weight * 5000 + 20000;

        // BƯỚC TẠO LỖI 4: Cố tình sửa hệ số nhân từ 3000 thành 4000
        // Việc này sẽ làm hỏng kết quả của bài Test, khiến Pipeline báo đỏ
        if (type.equals("STANDARD")) return weight * 4000;

        throw new IllegalArgumentException("Unknown type: " + type);
    }
}