package com.back_end_TN.project_tn.enums;

public enum Gender {
    NAM(1),
    NU(0),
    KHAC(-1);
    private int value;
    Gender(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    // Optional: Phương thức để lấy giá trị từ số nguyên
    public static Active fromValue(int value) {
        for (Active status : Active.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
