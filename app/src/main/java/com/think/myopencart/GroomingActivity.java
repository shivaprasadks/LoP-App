package com.think.myopencart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GroomingActivity extends AppCompatActivity {
    private RadioGroup radio_normalGroup;
    private RadioButton radio_normalButton;
    private Button btn_normal;
    private String pID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grooming);

        addListenerOnButton();

    }

    public void addListenerOnButton() {

        radio_normalGroup = (RadioGroup) findViewById(R.id.radio_normal_wash);
        btn_normal = (Button) findViewById(R.id.btn_normal_wash);
        btn_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radio_normalGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radio_normalButton = (RadioButton) findViewById(selectedId);
                Toast.makeText(GroomingActivity.this,
                        radio_normalButton.getText() + "Added to cart", Toast.LENGTH_SHORT).show();
                pID= (String) radio_normalButton.getHint();
             //   new ProductViewActivity.addtoCartAsync().execute("http://lifeofpet.com/shop/index.php?route=feed/checkout_api/addToCart&pID=" + pID + "&cID=" + cID + "&quantity=" + quantity);
            }

        });

    }
}
