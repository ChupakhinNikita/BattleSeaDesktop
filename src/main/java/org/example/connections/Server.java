package org.example.connections;

import org.example.models.Frame;
import org.example.models.Ship;
import org.example.views.View;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private ServerSocket serverSocket; // Прослушивание подключений
    private Frame[][] fieldPlayer1; // Матрица игрового поля игрока 1
    private Frame[][] fieldPlayer2; // Матрица игрового поля игрока 2
    private List<Ship> allShipsPlayer1; // Список кораблей игрока 1
    private List<Ship> allShipsPlayer2; // Список кораблей игрока 2
    private volatile boolean allPlayersConnected = false; // Флаг подключения игроков
    private List<Connection> listConnection = new ArrayList<>(); // Список объектов Connection, представляющих соединения с игроками

    private static String nameWinner; // Имя игрока 1

    private static String namelosser; // Имя игрока 2

    private static LocalTime timeStart; // Имя игрока 1

    private static LocalTime timeEnd; // Имя игрока 2

    private static String port; // Имя игрока 2


    public static String getNamelosser() {
        return namelosser;
    }

    public static String getNameWinner() {
        return nameWinner;
    }

    public Server(int port, LocalTime time) throws IOException {
        this.port = String.valueOf(port);
        serverSocket = new ServerSocket(port);
        timeStart = time;
    }

    // Запускает метод startServer при запуске потока сервера.
    @Override
    public void run() {
        startServer();
    }

    // Метод запуска сервера
    private void startServer() {
        try {
            while (!allPlayersConnected) {
                Socket socket = serverSocket.accept(); // Принимаем подключение игрока

                // Игрок 1
                if (listConnection.size() == 0) {
                    Connection connection = new Connection(socket);
                    listConnection.add(connection); // Добавляем connection в список
                    connection.send(new Message(MessageType.ACCEPTED)); // Отправляем клиенту сообщение о принятии

                    Message message = connection.get();  // Получаем от клиента матрицу и список кораблей
                    if (message.getMessageType() == MessageType.FIELD) {
                        // Устанавливаем поле и корабли в соответсвующие поля

                        fieldPlayer1 = message.getGameField();
                        allShipsPlayer1 = message.getListOfAllShips();
                    }

                    // Запускаем нить основного цикла общения клиента и сервера
                    new ThreadConnection(connection).start();
                }

                // Игрок 2
                else if (listConnection.size() == 1) {
                    Connection connection = new Connection(socket);
                    listConnection.add(connection);
                    connection.send(new Message(MessageType.ACCEPTED));
                    Message message = connection.get();
                    if (message.getMessageType() == MessageType.FIELD) {
                        fieldPlayer2 = message.getGameField();
                        allShipsPlayer2 = message.getListOfAllShips();

                        connection.send(new Message(MessageType.FIELD, fieldPlayer1, allShipsPlayer1));
                        listConnection.get(0).send(new Message(MessageType.FIELD, fieldPlayer2, allShipsPlayer2));
                    }
                    new ThreadConnection(connection).start();
                    allPlayersConnected = true;
                }
            }

            serverSocket.close();
        } catch (Exception e) {
            View.callInformationWindow("Возникла ошибка при запуске сервера игровой комнаты.");
        }
    }


    // Внутренний класс, представляющий поток обработки соединения с одним игроком.
    private class ThreadConnection extends Thread {
        private Connection connection;
        private volatile boolean stopCicle = false; //  Флаг прерывания основного цикла общения с игроком

        private static String nameWin;

        private static String nameLos;

        public String getNameWin() {
            return nameWin;
        }

        public void setNameWin(String nameWin) {
            this.nameWin = nameWin;
        }

        public void setNameLos(String nameLos) {
            this.nameLos = nameLos;
        }

        public String getNameLos() {
            return nameLos;
        }

        public ThreadConnection(Connection connection) {
            this.connection = connection;
        }

        // Основной цикл общения с клиентом
        private void mainCicle(Connection connection) {
            boolean isTwo = false;
            try {
                while (!stopCicle) {
                    Message message = connection.get(); // Принимаем сообщение от клиента

                    // Если дисконнект, то перенаправляем сообщение клиенту противника и останавливаем цикл
                    if (message.getMessageType() == MessageType.DISCONNECT || message.getMessageType() == MessageType.DEFEAT) {
                        if (message.getLosName() != null)
                            namelosser = message.getLosName();
                        sendMessageEnemy(message);
                        stopCicle = true;
                    } else if (message.getMessageType() == MessageType.MY_DISCONNECT) { // Если тип сообщения MY_DISCONNECT, то просто останавливаем цикл
                        if (message.getWinName() != null)
                            nameWinner = message.getWinName();
                        stopCicle = true;
                    // В любом другом случае перенаправляем сообщение противнику
                    }
                    // В любом другом случае перенаправляем сообщение противнику
                    else sendMessageEnemy(message);
                }

                if (nameWinner != null && namelosser != null) {
                    System.out.println("Порт игры: " + port);
                    System.out.println("Игрок 1: " + nameWinner);
                    System.out.println("Игрок 2: " + namelosser);
                    System.out.println("Победитель: " + nameWinner);
                    LocalDate date = LocalDate.now();
                    System.out.println("Дата: " + date);
                    System.out.println("Время начала партии: " + timeStart);
                    DateTimeFormatter formatter
                            = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
                    timeEnd = LocalTime.parse(LocalTime.now().format(formatter));
                    System.out.println("Время конца партии: " + timeEnd);
                    long minutesDifference = ChronoUnit.MINUTES.between(timeStart, timeEnd);
                    System.out.println("Длительность партии (в минутах): " + minutesDifference);

/*                    Log log = new Log();
                    log.setFirst_player_name(nameWinner);
                    log.setSecond_player_name(namelosser);
                    log.setWinner_name(nameWinner);
                    log.setPort_game(port);
                    log.setData(date.toString());
                    log.setTime_start_game(timeStart.toString());
                    log.setTime_end_game(timeEnd.toString());
                    log.setDuration_game("" + minutesDifference);*/

                    Class.forName("org.postgresql.Driver");
                    java.sql.Connection myConn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BattleSea",
                            "postgres", "123456");

                    // Проверка пользователя в базе данных
                    try (java.sql.Connection connect = myConn) {
                        String insertLogQuery = "INSERT INTO public.\"logs\" (first_player_name, second_player_name, winner_name, port_game, data, time_start_game, time_end_game, duration_game) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = connect.prepareStatement(insertLogQuery);
                        statement.setString(1, nameWinner);
                        statement.setString(2, namelosser);
                        statement.setString(3, nameWinner);
                        statement.setString(4, port);
                        statement.setString(5, date.toString());
                        statement.setString(6, timeStart.toString());
                        statement.setString(7, timeEnd.toString());
                        statement.setString(8, Long.toString(minutesDifference));
                        statement.executeUpdate();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                connection.close();
            } catch (Exception e) {
                View.callInformationWindow("Ошибка при обмене выстрелами. Связь потеряна");
            }
        }

        // Отправляет сообщение противнику
        private void sendMessageEnemy(Message message) throws IOException {
            for (Connection con : listConnection) if (!connection.equals(con)) con.send(message);
        }

        @Override
        public void run() {
            mainCicle(connection);
        }
    }
}
