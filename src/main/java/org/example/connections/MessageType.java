package org.example.connections;

public enum MessageType {
    FIELD, // Прием матрицы игрового поля
    SHOT, // Отправка/приём выстрела
    DEFEAT, // Отправка/приём поражения одного из игроков
    ACCEPTED, // Отправка/приём сокета сервером
    DISCONNECT, // Отключение клиента
    MY_DISCONNECT; // Отключение
}
