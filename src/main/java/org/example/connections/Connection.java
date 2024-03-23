package org.example.connections;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {
    private final Socket socket; // Для установки соединения с другой стороной.
    private final ObjectOutputStream out; // Для отправки объектов через сокет.
    private final ObjectInputStream in; // Для чтения объектов из сокета.

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    // Используется для отправки объекта Message через сокет.
    public void send(Message message) throws IOException {
        synchronized (this.out){
            out.writeObject(message);
        }
    }

    // Используется для чтения объекта Message через сокет.
    public Message get() throws IOException, ClassNotFoundException {
        synchronized (this.in){
            Message message = (Message) in.readObject();
            return message;
        }
    }

    // Используется для закрытия всех ресурсы сокета
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
