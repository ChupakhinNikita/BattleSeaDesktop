package org.example.connections;

import lombok.Getter;
import lombok.Setter;
import org.example.battlesea.models.Frame;
import org.example.battlesea.models.Ship;

import java.io.Serializable;
import java.util.List;

// Класс Message предназначен для передачи сообщений между клиентом и сервером через сокеты.
@Getter @Setter
public class Message implements Serializable {
    private int x;
    private int y;
    private MessageType messageType;
    private Frame[][]  gameField;
    private List<Ship> listOfAllShips;
    private String winName;
    private String losName;

    public Message(MessageType messageType, Frame[][] gameField, List<Ship> allShipsOfEnemy) {
        this.messageType = messageType;
        this.gameField = gameField;
        this.listOfAllShips = allShipsOfEnemy;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message(MessageType messageType, boolean isWin, String name) {
        this.messageType = messageType;
        if(isWin) this.winName = name;
        else this.losName = name;
    }

    public Message(MessageType messageType, int x, int y) {
        this.x = x;
        this.y = y;
        this.messageType = messageType;
    }
}

/*    public Message(MessageType messageType, String name) {
        if (messageType == MessageType.WINNER)
            this.winName = name;
        else if (messageType == MessageType.LOSSER)
            this.losName = name;
    }*/
