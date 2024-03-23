package org.example.models;

import lombok.Getter;
import lombok.Setter;
import org.example.views.Picture;

import java.io.Serializable;

@Getter @Setter
public class Frame implements Serializable {
    // Координаты фрейма х и y на поле
    private int x;
    private int y;
    private Picture picture;
    private boolean isOpen = false;  // Открыт ли данный фрейм (== стрелял ли соперник в данный фрейм)

    public boolean isOpen() {
        return isOpen;
    }

    public Frame(Picture picture, int x, int y) {
        this.picture = picture;
        this.x = x;
        this.y = y;
    }
}