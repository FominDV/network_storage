package ru.fomin.network;

import ru.fomin.KeyCommands;
import ru.fomin.core.Commands;
import ru.fomin.core.HandlerCommands;

import java.io.*;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final Socket socket;
    private  final Commands commands=new HandlerCommands();
    private DataOutputStream out;
    private DataInputStream in;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = in.readUTF();
                try {
                    commands.handleRequest(command, this);
                } catch (IOException e) {
                    out.writeUTF(KeyCommands.ERROR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUTF() throws IOException {
        return in.readUTF();
    }

    public long readLong() throws IOException {
        return in.readLong();
    }

    public void writeUTF(String message) throws IOException {
        out.writeUTF(message);
    }

    public void writeLong(Long number) throws IOException {
        out.writeLong(number);
    }

    public int read(byte[] buffer) throws IOException {
        return in.read(buffer);
    }

    public void write(byte[] buffer, int len) throws IOException {
        out.write(buffer,0,len);
    }

    public void flush() throws IOException {
        out.flush();
    }
}
