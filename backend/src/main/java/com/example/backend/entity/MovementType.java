package com.example.backend.entity;

// Тип руху товару на складі
public enum MovementType {
    INCOME,        // надходження (прихід від постачальника)
    ORDER,         // списання під замовлення
    ORDER_CANCEL,  // повернення на склад після скасування замовлення
    ADJUSTMENT     // коригування після інвентаризації або редагування товару
}
