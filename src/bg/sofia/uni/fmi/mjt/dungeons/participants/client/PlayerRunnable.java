package bg.sofia.uni.fmi.mjt.dungeons.participants.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static bg.sofia.uni.fmi.mjt.dungeons.messages.player.PlayerMessages.*;
import static bg.sofia.uni.fmi.mjt.dungeons.messages.server.ServerMessages.STOP_ALL_CLIENTS;

public class PlayerRunnable implements Runnable {

    private static final String KILLED = STOP_ALL_CLIENTS.toString();

    private Socket socket;
    private boolean running = true;

    PlayerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (running) {
                if (socket.isClosed()) {
                    System.out.println(SOCKET_CLOSED);
                    return;
                }

                StringBuilder received = new StringBuilder();
                String line = reader.readLine();
                while (!"".equals(line) && line != null) {
                    received.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }

                // if the response is the super-random string for disconnection
                // kill the client thread
                if (isKilled(received.toString())) {
                    running = false;
                    System.err.println(SERVER_CLOSED);
                    System.exit(0);
                }
                else {
                    System.out.println(received);
                }
            }
        } catch (IOException e) {
            // disconnect command sent to server and it has closed the socket
            if (e.getMessage().equals(SOCKET_CLOSED.toString())) {
                System.out.println(DISCONNECT_OK);
            }
            // some internal server error
            else if (e.getMessage().equals(CONNECTION_RESET.toString())) {
                System.err.println(SERVER_CLOSED);
            }
            // everything else
            else {
                System.err.println(e.getMessage());
            }

            System.exit(0);
        }
    }

    private boolean isKilled(String received) {
        return received.split(" ")[0].equals(KILLED);
    }

}