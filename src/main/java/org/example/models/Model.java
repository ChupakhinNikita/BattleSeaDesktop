package org.example.models;

import lombok.Getter;
import org.example.views.Picture;
import org.example.views.View;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Model {
    private Frame[][] myField = new Frame[Picture.FIELD_SIZE][Picture.FIELD_SIZE]; // матрица с боксами нашего игрового поля
    private Frame[][] enemyField = new Frame[Picture.FIELD_SIZE][Picture.FIELD_SIZE];  // матрица с боксами ирового поля соперника
    private List<Ship> shipsOneDeck = new ArrayList<>(); // список всех наших однопалубных кораблей
    private List<Ship> shipsTwoDeck = new ArrayList<>(); // список всех наших двухпалубных кораблей
    private List<Ship> shipsThreeDeck = new ArrayList<>(); // список всех наших трехпалубных кораблей
    private List<Ship> shipsFourDeck = new ArrayList<>(); // список всех наших четырехпалубных кораблей
    private List<Ship> allShipsOfEnemy = new ArrayList<>(); // список всех кораблей соперниа
    private boolean extraTurn;

    private String name; // Имя


    public void setExtraTurn(boolean extraTurn) {
        this.extraTurn = extraTurn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAllShipsOfEnemy(List<Ship> allShipsOfEnemy) {
        this.allShipsOfEnemy = allShipsOfEnemy;
    }

    //метод, который возвращает список ВСЕХ наших кораблей
    public List<Ship> getAllShips() {
        List<Ship> allBoxesOfShips = new ArrayList<>();
        allBoxesOfShips.addAll(shipsFourDeck);
        allBoxesOfShips.addAll(shipsThreeDeck);
        allBoxesOfShips.addAll(shipsTwoDeck);
        allBoxesOfShips.addAll(shipsOneDeck);
        return allBoxesOfShips;
    }

    //метод, который возвращает ВСЕ боксы из ВСЕХ наших кораблей
    public List<Frame> getAllBoxesOfShips() {
        List<Frame> allFrames = new ArrayList<>();
        List<Ship> allShips = getAllShips();
        for (Ship ship : allShips) {
            allFrames.addAll(ship.getBoxesOfShip());
        }
        return allFrames;
    }

    public void setMyField(Frame[][] myField) {
        this.myField = myField;
    }

    public void setEnemyField(Frame[][] enemyField) {
        this.enemyField = enemyField;
    }

    //метод, устанавливающий значение указанного бокс в указанную матрицу (игрового поля)
    public void addBoxInField(Frame[][] fieldFrames, Frame frame) {
        //по координатам бокса вычисляем индексы соответствующего места в матрице
        int i = frame.getX() / Picture.IMAGE_SIZE;
        int j = frame.getY() / Picture.IMAGE_SIZE;
        fieldFrames[i][j] = frame;
    }

    //метод, возвращающий бокс из уазанной матрицы (игрового поля) по координатам панели отрисовки игрового поля
    public Frame getBox(Frame[][] field, int x, int y) {
        int i = x / Picture.IMAGE_SIZE;
        int j = y / Picture.IMAGE_SIZE;
        int lenght = field.length - 1;
        //если координаты указывают на элемент индес которого больше размерности матрицы, то возвращаем null
        if (!(i > lenght || j > lenght)) {
            return field[i][j];
        }
        return null;
    }

    //метод, который устанавливает значение isOpen в true (открывает боксы после попадания в корабль) боксов, находящихся рядом с боксом корабля, определенного входными координатами
    public void openBoxAroundBoxOfShipEnemy(int x, int y, boolean isHorizontalPlacement) {
        //для горизонтально ориентированного корабля
        if (isHorizontalPlacement) {
            Frame frameUp = getBox(enemyField, x, y - Picture.IMAGE_SIZE);
            if (frameUp != null) frameUp.setOpen(true);
            Frame frameDown = getBox(enemyField, x, y + Picture.IMAGE_SIZE);
            if (frameDown != null) frameDown.setOpen(true);
        }
        //для вертиально ориентированного корабля
        else {
            Frame frameLeft = getBox(enemyField, x - Picture.IMAGE_SIZE, y);
            if (frameLeft != null) frameLeft.setOpen(true);
            Frame frameRight = getBox(enemyField, x + Picture.IMAGE_SIZE, y);
            if (frameRight != null) frameRight.setOpen(true);
        }
    }

    //открывает все пустые боксы по пириметру орабля, используется в случае, когда все палубы орабля подбиты
    public void openAllBoxesAroundShip(Ship ship) {
        //циклами открываем все боксы вокруг первого и последнего боксов корабля, т.к. больше 4 палуб быть не может, то
        //достаточно открыть все вокруг первой палубы и последней
        Frame startFrame = ship.getBoxesOfShip().get(0);
        Frame endFrame = ship.getBoxesOfShip().get(ship.getCountDeck() - 1);
        for (int i = startFrame.getX() - Picture.IMAGE_SIZE; i <= startFrame.getX() + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = startFrame.getY() - Picture.IMAGE_SIZE; j <= startFrame.getY() + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Frame frame = getBox(enemyField, i, j);
                if (frame != null) frame.setOpen(true);
            }
        }
        for (int i = endFrame.getX() - Picture.IMAGE_SIZE; i <= endFrame.getX() + Picture.IMAGE_SIZE; i += Picture.IMAGE_SIZE) {
            for (int j = endFrame.getY() - Picture.IMAGE_SIZE; j <= endFrame.getY() + Picture.IMAGE_SIZE; j += Picture.IMAGE_SIZE) {
                Frame frame = getBox(enemyField, i, j);
                if (frame != null) frame.setOpen(true);
            }
        }
    }

    // добавляет заданный корабль в соответствующий список кораблей в зависимости от количества палуб
    public void addShip(Ship ship) {
        int countDeck = ship.getCountDeck();
        switch (countDeck) {
            case 1: {
                //проверка - если в списке уже есть максимальное кол-во кораблей данного типа (кол-во палуб)
                //то вызывается соответствующее информационное окно, иначе добавляем корабль в нужный список
                if (shipsOneDeck.size() < 4) {
                    shipsOneDeck.add(ship);
                    for (Frame frame : ship.getBoxesOfShip()) {
                        addBoxInField(myField, frame);
                    }
                } else View.callInformationWindow("Перебор однопалубных. Максимально возможно - 4.");
                break;
            }
            case 2: {
                if (shipsTwoDeck.size() < 3) {
                    shipsTwoDeck.add(ship);
                    for (Frame frame : ship.getBoxesOfShip()) {
                        addBoxInField(myField, frame);
                    }
                } else View.callInformationWindow("Перебор двухпалубных. Максимально возможно - 3.");
                break;
            }
            case 3: {
                if (shipsThreeDeck.size() < 2) {
                    shipsThreeDeck.add(ship);
                    for (Frame frame : ship.getBoxesOfShip()) {
                        addBoxInField(myField, frame);
                    }
                } else View.callInformationWindow("Перебор трехпалубных. Максимально возможно - 2.");
                break;
            }
            case 4: {
                if (shipsFourDeck.size() < 1) {
                    shipsFourDeck.add(ship);
                    for (Frame frame : ship.getBoxesOfShip()) {
                        addBoxInField(myField, frame);
                    }
                } else View.callInformationWindow("Четырехпалубный уже добавлен. Максимально возможно - 1.");
                break;
            }
        }
    }


    //возвращает по боксу, в который произвели выстрел, корабль противника, если координаты боксШота равный координатам
    //бокса одного из корабля противника, иначе возвращаем null
    public Ship getShipOfEnemy(Frame frameShot) {
        for (Ship ship : allShipsOfEnemy) {
            for (Frame frame : ship.getBoxesOfShip()) {
                if (frameShot.getX() == frame.getX() && frameShot.getY() == frame.getY()) {
                    return ship;
                }
            }
        }
        return null;
    }

    //удаляет корабль из соответствующего списка - используется в процессе расстановки
    // и удалении кораблей на своем игровом поле
    public void removeShip(Ship ship) {
        //если корабль содержится в одном из списков, то перебираем все боксы списка,
        //меняем значение их картинки на EMPTY и добавляем в матрицу нашего игрового поля этот бокс
        if (shipsOneDeck.contains(ship)) {
            for (Frame frame : ship.getBoxesOfShip()) {
                frame.setPicture(Picture.EMPTY);
                addBoxInField(myField, frame);
                shipsOneDeck.remove(ship);
            }
        } else if (shipsTwoDeck.contains(ship)) {
            for (Frame frame : ship.getBoxesOfShip()) {
                frame.setPicture(Picture.EMPTY);
                addBoxInField(myField, frame);
                shipsTwoDeck.remove(ship);
            }
        } else if (shipsThreeDeck.contains(ship)) {
            for (Frame frame : ship.getBoxesOfShip()) {
                frame.setPicture(Picture.EMPTY);
                addBoxInField(myField, frame);
                shipsThreeDeck.remove(ship);
            }
        } else if (shipsFourDeck.contains(ship)) {
            for (Frame frame : ship.getBoxesOfShip()) {
                frame.setPicture(Picture.EMPTY);
                addBoxInField(myField, frame);
                shipsFourDeck.remove(ship);
            }
        }
    }
}
