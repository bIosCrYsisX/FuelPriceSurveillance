package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonStartService;
    TextView txtPrice;
    MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainController = new MainController(this);

        txtPrice = findViewById(R.id.textPrice);
        buttonStartService = findViewById(R.id.buttonStartService);

        buttonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FuelService.class);
                startService(intent);
                Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mainController.getPrice() == -1)
        {
            txtPrice.setText(getString(R.string.error));
        }

        else
        {
            txtPrice.setText(Float.toString(mainController.getPrice()) + "€");
        }
    }
}
