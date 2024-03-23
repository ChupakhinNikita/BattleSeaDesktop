package org.example.controllers;

import org.example.connections.Connection;
import org.example.connections.Message;
import org.example.connections.MessageType;
import org.example.connections.Server;
import org.example.models.Frame;
import org.example.models.Model;
import org.example.models.Ship;
import org.example.sound.Sound;
import org.example.views.Picture;
import org.example.views.View;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.List;

public class Controller {
    private View view;
    private Model model;
    private Connection connection;

    private String winner; // Имя игрока 2

    private String looser; // Имя игрока 2

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    public String getWinner() {
        return winner;
    }

    public String getLooser() {
        return looser;
    }

    //загружает наше пустое игровое поле
    public void loadEmptyMyField() {
        //очистка списков всех типов кораблей
        model.getShipsOneDeck().clear();
        model.getShipsTwoDeck().clear();
        model.getShipsThreeDeck().clear();
        model.getShipsFourDeck().clear();
        model.setMyField(new Frame[Picture.FIELD_SIZE][Picture.FIELD_SIZE]);
        for (int i = 0; i < Picture.FIELD_SIZE; i++) {
            for (int j = 0; j < Picture.FIELD_SIZE; j++) {
                if (i == 0 && j == 0) continue;
                else if (i == 0 && j != 0) { // буквы
                    model.addBoxInField(model.getMyField(), new Frame(Picture.valueOf("SYM" + j), Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                } else if (i != 0 && j == 0) { // цифры
                    model.addBoxInField(model.getMyField(), new Frame(Picture.valueOf("NUM" + i), Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                } else { // в остальных случаях значение картинки с пустой клеткой
                    model.addBoxInField(model.getMyField(), new Frame(Picture.EMPTY, Picture.IMAGE_SIZE * i, Picture.IMAGE_SIZE * j));
                }
            }
        }
    }

    // Добавляет корабль
    public void addShip(Ship ship) {
        List<Frame> boxesOfShip = ship.getBoxesOfShip();
        for (Frame frameShip : boxesOfShip) {
            if (checkAround(frameShip, boxesOfShip)) {
                boxesOfShip.clear();
                return;
            }
        }
        if (boxesOfShip.size() != 0) model.addShip(ship);
    }

    // Удаляет корабль
    public Ship removeShip(int x, int y) {
        List<Ship> allShips = model.getAllShips(); // получаем список всех добавленных кораблей
        for (Ship ship : allShips) {
            for (Frame frame : ship.getBoxesOfShip()) {
                if (x == frame.getX() && y == frame.getY()) { // перебираем корабли, затем их боксы,
                    // если координаты бокса совпадают с заданными - удаляем корабль
                    model.removeShip(ship);
                    return ship;
                }
            }
        }
        return null;
    }

    // Метод проверки на пересечение с другими кораблями
    private boolean checkAround(Frame frame, List<Frame> boxesOfShip) {
        int myX = frame.getX();
        int myY = frame.getY();
        for (int i = myX - Picture.IMAGE_SIZE; i <= myX + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = myY - Picture.IMAGE_SIZE; j <= myY + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Frame frameFromMatrix = model.getBox(model.getMyField(), i, j);
                if (frameFromMatrix != null && (frameFromMatrix.getPicture() == Picture.DECK1 || frameFromMatrix.getPicture() == Picture.DECK2) && !boxesOfShip.contains(frameFromMatrix)) {
                    View.callInformationWindow("Сюда нельзя добавлять корабль - пересечение с другим");
                    boxesOfShip.clear();
                    return true;
                }
            }
        }
        return false;
    }

    // метод открывающий пустые клетки вокруг подбитого корабля на поле противника
    public void openBoxesAround(Frame frameShot) {
        Ship ship = model.getShipOfEnemy(frameShot); // по боксу в который выстрелили получаем корабль
        if (ship != null) {
            // если число палуб == числу подбитых (открытых) боксов корабля - то открываем все пустые клетки вокруг
            if (ship.getCountDeck() == getCountOpenBoxOfShip(ship)) model.openAllBoxesAroundShip(ship);
                // иначе если подбита только одна палуба - ничего не делаем
            else if (getCountOpenBoxOfShip(ship) == 1) return;
                // в остальных случаях открываем пустые клетки вокруг подбитых палуб
            else {
                for (Frame frame : ship.getBoxesOfShip()) {
                    if (frame.isOpen())
                        model.openBoxAroundBoxOfShipEnemy(frame.getX(), frame.getY(), ship.isHorizontalPlacement());
                }
            }
        }
    }

    // метод возвращает количество подбитых палуб корабля
    public int getCountOpenBoxOfShip(Ship ship) {
        int count = 0;
        for (Frame frame : ship.getBoxesOfShip()) {
            if (frame.isOpen()) count++;
        }
        return count;
    }

    // проверка на окончание игры
    private boolean checkEndGame() {
        List<Frame> allBoxesOfShip = model.getAllBoxesOfShips(); // получаем список всех своих кораблей
        for (Frame frame : allBoxesOfShip) {
            // проверяем если хотя бы один корабль имеет значение картины SHIP то игра не окончена
            if (frame.getPicture() == Picture.DECK1 || frame.getPicture() == Picture.DECK2 ) return false;
        }
        return true;
    }

    // проверка на полный комплект добавленный кораблей перед стартом игры
    public boolean checkFullSetShips() {
        // просто проверяем количество корабля в соответствующих списках по каждому типу кораблей
        if (model.getShipsOneDeck().size() == 4 &&
                model.getShipsTwoDeck().size() == 3 &&
                model.getShipsThreeDeck().size() == 2 &&
                model.getShipsFourDeck().size() == 1) return true;
        else return false;
    }


    /* Методы для кеннекта к серверу и обмена выстрелами между игроками */

    // Создает игровую комнату
    public void createGameRoom(int port, LocalTime time) throws IOException {
        Server server = new Server(port, time); // создаем объект класса сервер и запускаем поток исполнения
        server.start();

    }


    // Метод подключения клиента к серверу
    public void connectToRoom(int port) throws IOException, ClassNotFoundException {
        connection = new Connection(new Socket("localhost", port));
        Message message = connection.get(); // Принимаем от сервера сообщение

        // Если тип сообщения ACCEPTED, то отправляем на сервер наше поле с кораблями и список всех кораблей
        if (message.getMessageType() == MessageType.ACCEPTED) {
            connection.send(new Message(MessageType.FIELD, model.getMyField(), model.getAllShips()));
            Message messageField = connection.get(); // ждем ответа от сервера с полем и списком кораблей соперника
            if (messageField.getMessageType() == MessageType.FIELD) {
                model.setEnemyField(messageField.getGameField());  //сохраняем в модель поле и список кораблей противника
                model.setAllShipsOfEnemy(messageField.getListOfAllShips());
            }
        }
    }

    // метод отключения от сервера
    public void disconnectGameRoom() throws IOException {
        connection.send(new Message(MessageType.DISCONNECT));
    }

    // отправка сообщения на сервер с координатами выстрела
    public boolean sendMessage(int x, int y) throws IOException {
        if (x == 0 && y == 0){
            connection.send(new Message(MessageType.SHOT, x, y)); // отправляем координаты выстрела на сервер\
            return true;
        }
        Frame frame = model.getBox(model.getEnemyField(), x, y);
        if (!frame.isOpen()) {
            frame.setOpen(true); // открываем бокс выстрела
            openBoxesAround(frame); // открываем соседние пустые клетки (боксы)
            connection.send(new Message(MessageType.SHOT, x, y)); // отправляем координаты выстрела на сервер
            return true;
        } else return false;
    }

    // метод принимающий сообщения от сервера
    public boolean receiveMessage() throws IOException, ClassNotFoundException {
        Message message = connection.get(); // принимаем сообщение

        if (message.getMessageType() == MessageType.SHOT) {
            int x = message.getX();
            int y = message.getY();
            Frame frame = model.getBox(model.getMyField(), x, y); // получаем бокс с нашего поля по координатам

            if (x == 0 && y == 0){
                return true;
            }
            else if (frame.getPicture() == Picture.EMPTY) {
                frame.setPicture(Picture.POINT);
                Sound.playSound("C:\\Users\\Никита\\Desktop\\BattleSea\\wav\\misSound.wav").join();
                model.setExtraTurn(false);
            } else if (frame.getPicture() == Picture.DECK1) {
                frame.setPicture(Picture.DESTROY_DECK1);
                Sound.playSound("C:\\Users\\Никита\\Desktop\\BattleSea\\wav\\shotSound.wav").join();
                model.setExtraTurn(true);
            } else if (frame.getPicture() == Picture.DECK2) {
                frame.setPicture(Picture.DESTROY_DECK2);
                Sound.playSound("C:\\Users\\Никита\\Desktop\\BattleSea\\wav\\shotSound.wav").join();
                model.setExtraTurn(true);
            }
            model.addBoxInField(model.getMyField(), frame); // устанавливаем измененный бокс в матрицу нашего поля

            if (checkEndGame()) { // проверка на конец игры

                if ( model.getName() != null)
                {
                    JOptionPane.showMessageDialog(
                            null, model.getName() + ", Вы проиграли! Все Ваши корабли уничтожены.",
                            "Внимание!", JOptionPane.ERROR_MESSAGE
                    );
                    //View.callInformationWindow( model.getName() + ", Вы проиграли! Все Ваши корабли уничтожены.");
                    this.looser = model.getName();
                    //this.nameSecondEnemy = model.getNameSecondEnemy();
                }
                connection.send(new Message(MessageType.DEFEAT, false, this.looser));
                return false;
            }

            return true;
            // если тип сообщения DISCONNECT - отключаемся
        } else if (message.getMessageType() == MessageType.DISCONNECT) {
            connection.send(new Message(MessageType.MY_DISCONNECT));

            JOptionPane.showMessageDialog(
                    null, model.getName() + ", Ваш соперник покинул игру. Вы одержали техническую победу!",
                    "Внимание!", JOptionPane.INFORMATION_MESSAGE
            );
            View.callInformationWindow("Ваш соперник покинул игру. Вы одержали техническую победу!");
            return false;
            // если тип сообщения DEFEAT - отключаемся
        } else if (message.getMessageType() == MessageType.DEFEAT) {


            if ( model.getName() != null)
            {
                JOptionPane.showMessageDialog(
                        null, model.getName() + ", все корабли противника уничтожены. Вы одержали победу!",
                        "Внимание!", JOptionPane.INFORMATION_MESSAGE
                );
                //View.callInformationWindow( model.getName() + ", все корабли противника уничтожены. Вы одержали победу!");
                this.winner = model.getName();
            }
            connection.send(new Message(MessageType.MY_DISCONNECT, true, this.winner ));
            return false;
        } else return false;
    }
}
