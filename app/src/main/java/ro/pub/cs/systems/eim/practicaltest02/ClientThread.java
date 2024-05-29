package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.transform.stream.StreamSource;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String hour;
    private String minute;
    private String informationType;
    private TextView result;

    public ClientThread(String address, int port, String hour, String minute, String informationType, TextView result) {
        this.address = address;
        this.port = port;
        this.hour = hour;
        this.minute = minute;
        this.informationType = informationType;
        this.result = result;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);
            if (socket == null) {
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                return;
            }
            printWriter.println(address);
            printWriter.flush();
            printWriter.println(hour);
            printWriter.flush();
            printWriter.println(minute);
            printWriter.flush();
            printWriter.println(informationType);
            printWriter.flush();
            String resultInformation;
            while ((resultInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeatherInformation = resultInformation;
                Log.i("[app]", "[CLIENT THREAD] Got information from server: " + finalizedWeatherInformation);
                result.post(() -> result.setText(finalizedWeatherInformation));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}