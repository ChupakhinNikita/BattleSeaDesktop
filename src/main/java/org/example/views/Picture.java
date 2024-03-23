package org.example.views;

import javax.swing.*;
import java.awt.*;

public enum Picture {

    EMPTY, // Пустая
    DESTROY_DECK1, DESTROY_DECK2, // Подбитая палуба
    NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9, NUM10, //для нумерации стро игрового поля
    POINT, // Пустой отстрелянный фрейм
    DECK1, DECK2, // Палуба
    INFO, //для информации на панели выбора настроек размещения кораблей
    SYM1, SYM2, SYM3, SYM4, SYM5, SYM6, SYM7, SYM8, SYM9, SYM10; //для обозначения буквами столбцов

    public static final int FIELD_SIZE = 11;

    public static final int IMAGE_SIZE = 40;

    public static Image getImage(String nameImg) {
        String fileName = "C:\\Users\\Никита\\Desktop\\BattleSea\\img\\" + nameImg.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(fileName);
        return icon.getImage();
    }
}
