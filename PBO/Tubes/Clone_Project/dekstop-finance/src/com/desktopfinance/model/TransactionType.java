package com.desktopfinance.model;

public enum TransactionType {
    INCOME("income"),
    EXPENSE("expense");

    private final String databaseValue;

    TransactionType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    // Opsional: metode untuk mendapatkan enum dari nilai string database
    public static TransactionType fromDatabaseValue(String databaseValue) {
        for (TransactionType type : values()) {
            if (type.databaseValue.equalsIgnoreCase(databaseValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + databaseValue);
    }
}