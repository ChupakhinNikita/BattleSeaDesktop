package org.example.views;

import org.example.controllers.Controller;
import org.example.models.Frame;
import org.example.models.Model;
import org.example.models.Ship;
import org.example.views.panels.ChoosePanel;
import org.example.views.panels.EnemyField;
import org.example.views.panels.MyField;
import org.example.views.panels.PanelButtons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class View extends JFrame {


    private Controller controller;
    private Model model;
    private MyField myField; // Game Field
    private EnemyField enemyField; // Game Field enemy
    private ChoosePanel choosePanel; // Панель выбора настроек при добавлении корабля
    private PanelButtons panelButtons; // Панель кнопок


    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*----------------------------Уставнка свойств окна--------------------------------------*/
        setTitle("Морской бой");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(Picture.getImage("icon"));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    //инициализация графического интерфейса
    public void init() {

        if (enemyField != null) {
            remove(enemyField);
            remove(myField);
            remove(panelButtons);
        }
        controller.loadEmptyMyField();
        add(choosePanel = new ChoosePanel(this), BorderLayout.EAST);
        add(myField = new MyField(this), BorderLayout.WEST);
        add(panelButtons = new PanelButtons(this), BorderLayout.SOUTH);
        myField.setChoosePanel(choosePanel);
        pack();
        revalidate();
        setVisible(true);

        // Установка открытия окна в центре экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
    }

    //метод для вызова информационного диалогового окна с заданныйм текстом
    public static void callInformationWindow(String message) {
        JOptionPane.showMessageDialog(
                null, message,
                "Внимание!", JOptionPane.WARNING_MESSAGE
        );
    }

    public static void callInformationWin(String message) {
        JOptionPane.showMessageDialog(
                null, message,
                "Внимание!", JOptionPane.INFORMATION_MESSAGE
        );
    }

    //метод для загрузки нашего пустого игровоо поля
    public void loadEmptyMyField() {
        controller.loadEmptyMyField();
        myField.repaint();
        choosePanel.setNameOneDeck(4);
        choosePanel.setNameTwoDeck(3);
        choosePanel.setNameThreeDeck(2);
        choosePanel.setNameFourDeck(1);
    }

    //добавление корабля
    public void addShip(Ship ship) {
        controller.addShip(ship);
    }

    //удаление корабля с нашего поля по координатам
    public Ship removeShip(int x, int y) {
        return controller.removeShip(x, y);
    }

    //метод, который изменяет имя у радиоБаттонов при удалении/добавлении кораблей по параметру число палуб
    public void changeCountShipOnChoosePanel(int countDeck) {
        switch (countDeck) {
            case 1: {
                //параметр - число кораблей которое осталось добавить (максимальное число кораблей данного типа -
                //число кораблей уже добавленных в соответствующий список в model
                choosePanel.setNameOneDeck(4 - model.getShipsOneDeck().size());
                break;
            }
            case 2: {
                choosePanel.setNameTwoDeck(3 - model.getShipsTwoDeck().size());
                break;
            }
            case 3: {
                choosePanel.setNameThreeDeck(2 - model.getShipsThreeDeck().size());
                break;
            }
            case 4: {
                choosePanel.setNameFourDeck(1 - model.getShipsFourDeck().size());
                break;
            }
        }
        choosePanel.revalidate();
    }

    // Перерисовывает наше игровое поле
    public void repaintMyField(Graphics g) {
        Frame[][] matrix = model.getMyField(); //получаем матрицу нашего поля
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Frame frame = matrix[i][j]; //присваиваем боксу значение элемента матрицы
                if (frame == null) continue;
                //подгружаем картинку на панель нашего игрового поля
                g.drawImage(Picture.getImage(frame.getPicture().name()), frame.getX(), frame.getY(), myField);
            }
        }
    }

    // Перерисовывает игровое поле соперника
    public void repaintEnemyField(Graphics g) {
        Frame[][] matrix = model.getEnemyField(); //получаем матрицу поля соперника
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Frame frame = matrix[i][j];
                if (frame == null) continue;
                //если значение картинки = пустой клетки или клетки с кораблем то...
                if ((frame.getPicture() == Picture.EMPTY || frame.getPicture() == Picture.DECK1 || frame.getPicture() == Picture.DECK2)) {
                    //если бокс открыт и картинка = пустая клетка, то отрисовываем эту клетку картинкой "пустая клетка с точкой"
                    if (frame.isOpen() && frame.getPicture() == Picture.EMPTY) {
                        g.drawImage(Picture.getImage(Picture.POINT.name()), frame.getX(), frame.getY(), enemyField);
                    }
                    //иначе если бокс открыт и картинка = клетка с кораблем, то отрисовываем эту клетку картинкой "клетка с зачеркнутым кораблем"
                    else if ((frame.isOpen() && frame.getPicture() == Picture.DECK1)) {
                        g.drawImage(Picture.getImage(Picture.DESTROY_DECK1.name()), frame.getX(), frame.getY(), enemyField);
                    } else if ((frame.isOpen() && frame.getPicture() == Picture.DECK2)) {
                        g.drawImage(Picture.getImage(Picture.DESTROY_DECK2.name()), frame.getX(), frame.getY(), enemyField);
                    }
                    //в остальных случаях отрисовываем клетку картинкой "закрытая клетка"
                    else g.drawImage(Picture.getImage(Picture.EMPTY.name()), frame.getX(), frame.getY(), enemyField);
                }
                //иначе отрисовываем той картинкой которая хранится в матрице - для клеток нумерации столбцов и строк
                else g.drawImage(Picture.getImage(frame.getPicture().name()), frame.getX(), frame.getY(), enemyField);
            }
        }
    }

    public void viewInfoWin(String text){

        JLabel label = new JLabel();
        label.setText("Игра окончена");

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setBounds(500, 300, 300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(label, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Старт игры
    public void startGame() {
        // Проверка на полный комплект кораблей
        if (controller.checkFullSetShips()) {
            String[] options = {"Создать", "Подключиться"};
            JPanel panel = new JPanel();
            JTextField field = new JTextField(25);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(new JLabel("Создайте комнату, введя 4-ех значный номер комнаты."));
            panel.add(new JLabel("Или подключитесь к уже созданной:"));
            panel.add(field);

            panel.add(new JLabel("Введите ваше имя:"));
            JTextField playerNameField = new JTextField(25);
            panel.add(playerNameField);

            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Создание комнаты:",
                    JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            try {
                if (selectedOption == 0) { // Если отжали кнопку "создать комнату"

                    int port = Integer.parseInt(field.getText().trim());
                    String playerName = playerNameField.getText().trim();
                    model.setName(playerName);

                    setTitle("Морской бой, игрок - " + playerName);

                    model.setName(playerName);
                    //controller.setNameFirstEnemy(playerName);

                    DateTimeFormatter formatter
                            = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

                    controller.createGameRoom(port, LocalTime.parse(LocalTime.now().format(formatter))); // Создается игровая комната - запускается сервер с сокетными соединениями
                    panelButtons.setTextInfo("Ожидание соперника");
                    panelButtons.revalidate();
                    View.callInformationWindow("Ожидаем соперника: после того как соперник подключиться к комнате, появится уведомление. Затем начнется игра. Ваш ход первый.");
                    controller.connectToRoom(port); //коннект клиента к серверу
                    View.callInformationWindow("Второй игрок подключился! Можно начинать сражение.");
                    refreshGuiAfterConnect(); //обновление интерфейса клиента после подключения второго игрока
                    panelButtons.setTextInfo("СЕЙЧАС ВАШ ХОД");
                    panelButtons.getExitButton().setEnabled(true); //активация кнопки Выхода
                    enemyField.addListener(); //добавляем слушателя к объекту панели игрового поля соперника
                } else if (selectedOption == 1) { //если отжата кнопка "подключиться к комнате"
                    int port = Integer.parseInt(field.getText().trim());
                    String playerName = playerNameField.getText().trim();
                    model.setName(playerName);

                    setTitle("Морской бой, игрок - " + playerName);

                    model.setName(playerName);

                    //controller.setNameSecondEnemy(playerName);

                    controller.connectToRoom(port); //коннект клиента к серверу
                    View.callInformationWindow("Вы успешно подключились к комнате. Ваш соперник ходит первым.");
                    refreshGuiAfterConnect(); //обновление интерфейса клиента после подключения
                    panelButtons.setTextInfo("СЕЙЧАС ХОД СОПЕРНИКА");
                    new ReceiveThread().start(); //запуск нити, которая ожидает сообщение от сервера
                }
            } catch (Exception e) {
                View.callInformationWindow("Произошла ошибка при создании комнаты, либо некорректный номер комнаты, попробуйте еще раз.");
                e.printStackTrace();
            }
        } else View.callInformationWindow("Вы добавили не все корабли на своем поле!");
    }

    //метод отключеия клиента от сервера
    public void disconnectGameRoom() {
        try {
            controller.disconnectGameRoom();
            View.callInformationWindow("Вы отключились от комнаты. Игра окончена. Вы потерпели техническое поражение.");
            enemyField.removeListener(); //ужаляем слушателя у панели игрового поля соперника
        } catch (Exception e) {
            View.callInformationWindow("Произошла ошибка при отключении от комнаты.");
        }
    }

    //обновляет интерфейс клиента после подключения обоих игроков
    public void refreshGuiAfterConnect() {
        MouseListener[] listeners = myField.getMouseListeners();
        for (MouseListener lis : listeners) {
            myField.removeMouseListener(lis); //удаление слушателя у панели нашего игрового поля
        }
        choosePanel.setVisible(false);
        remove(choosePanel);          //удаление панели настроек добавления корабля
        add(enemyField = new EnemyField(this), BorderLayout.EAST); //добавление панели игрового поля соперника
        enemyField.repaint(); //отрисовка поля соперника
        pack();  //репак формы
        panelButtons.getStartGameButton().setVisible(false); //деактивация кнопки "Начать игру"
        revalidate();
    }

    public void sendShot(int x, int y) {
        try {
            boolean isSendShot = controller.sendMessage(x, y); //непосредственная отправка сообщения через контроллер
            //System.out.println("sendShot: " + model.getNameSecondEnemy() +"\n");
            if (x == 0 && y == 0 && isSendShot){
                enemyField.repaint(); //переотрисовка поля соперника
                panelButtons.setTextInfo("СЕЙЧАС ХОД СОПЕРНИКА - он попал");
                new ReceiveThread().start(); //запуск нити, которая ожидает сообщение от сервера
            }
            else {
                if (isSendShot) { //если сообщение отправлено, то ...
                    enemyField.repaint(); //переотрисовка поля соперника
                    enemyField.removeListener(); //удаление слушателя у панели поля соперника
                    panelButtons.setTextInfo("СЕЙЧАС ХОД СОПЕРНИКА");
                    panelButtons.getExitButton().setVisible(false); //деактивация кнопки выхода
                    new ReceiveThread().start(); //запуск нити, которая ожидает сообщение от сервера
                }
            }
        } catch (Exception e) {
            View.callInformationWindow("Произошла ошибка при отправке выстрела.");
            e.printStackTrace();
        }
    }

    //класс-поток, который ожидает сообщение от сервера
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                boolean continueGame = controller.receiveMessage(); //контроллер принял сообщение
                myField.repaint();
               // System.out.println("ReceiveThread: " + model.getNameFirstEnemy() +"\n");
                if (continueGame) { //если вернулось true, то...
                    if (!model.isExtraTurn())
                    {
                        panelButtons.setTextInfo("СЕЙЧАС ВАШ ХОД");
                        enemyField.addListener();  //добавление слушателя к полю соперника
                    } else {
                        panelButtons.setTextInfo("ХОД соперника");
                        sendShot(0,0);
                    }
                } else { //если вернлось false то игра окончена
                    panelButtons.setTextInfo("ИГРА ОКОНЧЕНА");
                    panelButtons.getExitButton().setVisible(false);
                    enemyField.removeListener();
                    panelButtons.getRestartGameButton().setVisible(true);
                }

            } catch (IOException | ClassNotFoundException e) {
                View.callInformationWindow("Произошла ошибка при приеме сообщения от сервера");
                e.printStackTrace();
            }
        }
    }
}