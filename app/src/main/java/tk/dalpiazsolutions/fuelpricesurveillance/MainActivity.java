package tk.dalpiazsolutions.fuelpricesurveillance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonStartService;
    TextView txtPrice;
    MainController mainController;
    MailController mailController;
    private ListView priceView;
    private ArrayAdapter priceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }

        mainController = new MainController(this);
        mailController = new MailController(this);

        txtPrice = findViewById(R.id.textPrice);
        buttonStartService = findViewById(R.id.buttonStartService);
        priceView = findViewById(R.id.priceView);

        buttonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainController.startMonitoring();
            }
        });

        float price = mainController.getPrice();

        if(price == -1)
        {
            txtPrice.setText(getString(R.string.error));
        }

        else if(price == -2)
        {
            txtPrice.setText(getString(R.string.noprice));
        }

        else if(price == -3)
        {
            txtPrice.setText(getString(R.string.nopricetwo));
        }

        else
        {
            txtPrice.setText(getString(R.string.super95) + ": " + Float.toString(price) + "€");
        }
    }

    public void setArrayAdapter(ArrayAdapter arrayAdapter)
    {
        priceAdapter = arrayAdapter;
        priceView.setAdapter(priceAdapter);
    }
}
