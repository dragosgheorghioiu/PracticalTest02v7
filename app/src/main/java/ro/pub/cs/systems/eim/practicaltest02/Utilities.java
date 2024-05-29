package ro.pub.cs.systems.eim.practicaltest02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Utilities {
    public static BufferedReader getReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrintWriter getWriter(Socket socket) {
        try {
            return new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
