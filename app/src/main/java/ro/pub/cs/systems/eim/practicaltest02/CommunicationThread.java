package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        // check if the socket is null
        if (socket == null) {
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            String clientAddress = bufferedReader.readLine();
            String hour = bufferedReader.readLine();
            String minute = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();
            if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e("[app]", "[COMMUNICATION THREAD] Error receiving parameters from client (hour / minute / information type)!");
                return;
            }

            if (informationType.equals("set")) {
                if (clientAddress == null || clientAddress.isEmpty()) {
                    Log.e("[app]", "[COMMUNICATION THREAD] Error receiving parameters from client (client address)!");
                    return;
                }
                if (serverThread != null && serverThread.getData(clientAddress) != null && serverThread.getData(clientAddress).state.equals("active")) {
                    Log.i("[app]", "[COMMUNICATION THREAD] Timer information is already set! You need to reset it first!");
                    return;
                }
                serverThread.setData(clientAddress, hour, minute, "inactive");
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println("set");
                printWriter.flush();
            } else if (informationType.equals("reset")) {
                serverThread.setState(clientAddress, "none");
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println("reset");
                printWriter.flush();
            } else if (informationType.equals("poll")) {
                TimerInformation timerInformation = serverThread.getData(clientAddress);
                if (timerInformation == null) {
                    Log.e("[app]", "[COMMUNICATION THREAD] Error receiving information from client (timer information is null)!");
                    String result = "none";
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(result);
                    printWriter.flush();
                    return;
                }
                if (timerInformation.state.equals("none")) {
                    Log.i("[app]", "[COMMUNICATION THREAD] Timer information is not set!");
                    String result = "none";
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(result);
                    printWriter.flush();
                    return;
                }
                if (timerInformation.state.equals("active")) {
                    String result = "active";
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(result);
                    printWriter.flush();
                    return;
                }

                Socket socketAPI = new Socket("utcnist.colorado.edu", 13);
                if (socketAPI == null) {
                    Log.e("[app]", "[COMMUNICATION THREAD] Could not create socket!");
                    return;
                }
                BufferedReader socketBufferedReader = Utilities.getReader(socketAPI);

                socketBufferedReader.readLine();
                String allDetails = socketBufferedReader.readLine();
                String[] tokens = allDetails.split(" ");
                String currentTime = tokens[2];

                String[] timeTokens = currentTime.split(":");
                String currentHour = timeTokens[0];
                String currentMinute = timeTokens[1];

                Log.d("[app]", "[COMMUNICATION THREAD] Current minute is: " + currentMinute);
                Log.d("[app]", "[COMMUNICATION THREAD] Current hour is: " + currentHour);

                int currentHourInt = Integer.parseInt(currentHour);
                int currentMinuteInt = Integer.parseInt(currentMinute);

                int hourInt = Integer.parseInt(timerInformation.hour);
                int minuteInt = Integer.parseInt(timerInformation.minute);

                if (currentHourInt > hourInt || (currentHourInt == hourInt && currentMinuteInt > minuteInt)) {
                    timerInformation.state = "active";
                    String result = "active";
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(result);
                    printWriter.flush();
                } else {
                    String result = "inactive";
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(result);
                    printWriter.flush();
                }


            } else {
                Log.e("[app]", "[COMMUNICATION THREAD] Error receiving parameters from client (information type)!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}