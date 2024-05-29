package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ServerThread serverThread;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner getFieldSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getFieldSpinner.setAdapter(adapter);

        EditText serverPortEditText = findViewById(R.id.serverPortEditText);
        EditText clientPortEditText = findViewById(R.id.clientPortEditText);
        EditText clientAddressEditText = findViewById(R.id.clientAddressEditText);
        Button connectButton = findViewById(R.id.connect);
        Button getTimerButton = findViewById(R.id.gettimerbutton);
        EditText hourEditText = findViewById(R.id.hourEditText);
        EditText minuteEditText = findViewById(R.id.minuteEditText);

        connectButton.setOnClickListener(view -> {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            serverThread.start();
        });

        getTimerButton.setOnClickListener(view -> {
            String clientPort = clientPortEditText.getText().toString();
            String clientAddress = clientAddressEditText.getText().toString();
            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();
            if (clientPort == null || clientPort.isEmpty() || clientAddress == null || clientAddress.isEmpty() || hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client port, client address or city is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            TextView result = findViewById(R.id.result);
            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), hour, minute, getFieldSpinner.getSelectedItem().toString(), result);
            clientThread.start();
        });
    }
}