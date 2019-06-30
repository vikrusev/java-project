package bg.sofia.uni.fmi.mjt.dungeons.participants.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.dungeons.messages.commands.Commands.*;
import static bg.sofia.uni.fmi.mjt.dungeons.messages.player.PlayerMessages.*;

public class PlayerClient {

    private PrintWriter writer = null;
    private boolean connected = false;

    private void run() {
        try (Scanner scanner = new Scanner(System.in)) {

            final int commandIndex = 0;
            final int hostIndex = 1;
            final int portIndex = 2;
            final int usernameIndex = 3;

            while (true) {
                String input = scanner.nextLine();

                String[] tokens = input.split(" ");
                String command = tokens[commandIndex];

                if (command.equals(CONNECT.toString())) {
                    String host = tokens[hostIndex];
                    int port = Integer.parseInt(tokens[portIndex]);
                    String username = tokens[usernameIndex];

                    connect(host, port, username);

                } else if (connected) {
                    if (command.equals(DISCONNECT.toString())) {
                        disconnect(command);

                        return;
                    }

                    writer.println(input);
                } else {
                    System.out.println(CONNECT_FIRST);
                }
            }
        }
    }

    private void connect(String remoteHost, int remotePort, String username) {
        try {
            // if we are already connected - do not open a new socket
            // this case can be reached if we try to connect with a username that is already in use
            if (!connected) {
                Socket socket = new Socket(remoteHost, remotePort);
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                this.connected = true;

                System.out.println(OPENED_SOCKET);
                writer.println(CONNECT + " " + username);

                PlayerRunnable playerRunnable = new PlayerRunnable(socket);
                new Thread(playerRunnable).start();
            }

            else {
                writer.println(CONNECT + " " + username);
            }
        } catch (IOException e) {
            System.out.printf(ERROR_CONNECTING.toString(), remoteHost, remotePort);
        }
    }

    private void disconnect(String disconnect) {
        writer.println(disconnect);
        this.writer.close();
        this.connected = false;
    }

    public static void main(String[] args) {
        new PlayerClient().run();
    }

}