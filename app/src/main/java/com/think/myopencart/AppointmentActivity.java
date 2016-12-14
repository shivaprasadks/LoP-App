package com.think.myopencart;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btn_date;
    int year_x, month_x, day_x;
    static final int DIALOG_INY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        btn_date = (Button) findViewById(R.id.btn_date);
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_INY);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Vet on Call(Illness)");
        categories.add("Vet on Call(Wellness)");
        categories.add("Vet on Call(Vaccination)");
        categories.add("Accessories");
        categories.add("Grooming");
        categories.add("Daily Walks");
        categories.add("Adoption");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_INY) {
            return new DatePickerDialog(AppointmentActivity.this, dpickerListner, year_x, month_x, day_x);
        } else {
            return null;
        }


    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yearofDate, int monthofDate, int dayofDate) {
            year_x = yearofDate;
            month_x = monthofDate + 1;
            day_x = dayofDate;
            Toast.makeText(AppointmentActivity.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_SHORT).show();
            ;
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
