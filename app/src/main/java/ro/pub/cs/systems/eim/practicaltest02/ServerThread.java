package ro.pub.cs.systems.eim.practicaltest02;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

class TimerInformation {
    public String hour;
    public String minute;
    public String state;

    public TimerInformation(String hour, String minute, String state) {
        this.hour = hour;
        this.minute = minute;
        this.state = state;
    }
}

public class ServerThread extends Thread {

    private int serverPort;
    private ServerSocket serverSocket;
    private HashMap<String, TimerInformation> data = new HashMap<String, TimerInformation>();

    public ServerThread(int i) {
        this.serverPort = i;
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setData(String clientAddress, String hour, String minute, String state) {
        this.data.put(clientAddress, new TimerInformation(hour, minute, state));
    }

    public synchronized void setState(String clientAddress, String state) {
        Objects.requireNonNull(this.data.get(clientAddress)).state = state;
    }

    public synchronized TimerInformation getData(String clientAddress) {
        return this.data.get(clientAddress);
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                if (socket == null) {
                    continue;
                }
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}