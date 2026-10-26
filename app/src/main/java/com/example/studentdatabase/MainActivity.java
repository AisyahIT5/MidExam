
package com.example.studentdatabase;

import android.os.Bundle;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etName, etId;
    Spinner spMajor;
    TextView tvResult;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        spMajor = findViewById(R.id.spMajor);
        tvResult = findViewById(R.id.tvResult);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnView = findViewById(R.id.btnView);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new DBHelper(this);

        String[] majors = {
                "Informatics",
                "Information Systems"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, majors);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spMajor.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String major = spMajor.getSelectedItem().toString();

            if (id.isEmpty() || name.isEmpty()) {
                showMessage("Name and ID are required");
                return;
            }

            boolean success =
                    dbHelper.insertStudent(id, name, major);

            if (success) {
                showMessage("Student added successfully");
                clearForm();
                viewStudents();
            } else {
                showMessage("Student ID already exists");
            }
        });

        btnView.setOnClickListener(v -> viewStudents());

        btnUpdate.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String major = spMajor.getSelectedItem().toString();

            if (id.isEmpty() || name.isEmpty()) {
                showMessage("Enter Student ID and Name");
                return;
            }

            boolean success =
                    dbHelper.updateStudent(id, name, major);

            showMessage(success
                    ? "Student updated"
                    : "Student ID not found");

            if (success) viewStudents();
        });

        btnDelete.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();

            if (id.isEmpty()) {
                showMessage("Enter Student ID");
                return;
            }

            boolean success = dbHelper.deleteStudent(id);

            showMessage(success
                    ? "Student deleted"
                    : "Student ID not found");

            if (success) {
                clearForm();
                viewStudents();
            }
        });
    }

    private void viewStudents() {
        Cursor cursor = dbHelper.getAllStudents();
        StringBuilder result = new StringBuilder();

        try {
            while (cursor.moveToNext()) {
                result.append("Name: ")
                        .append(cursor.getString(1))
                        .append("\nID: ")
                        .append(cursor.getString(0))
                        .append("\nMajor: ")
                        .append(cursor.getString(2))
                        .append("\n\n");
            }
        } finally {
            cursor.close();
        }

        tvResult.setText(result.length() == 0
                ? "No student records found"
                : result.toString());
    }

    private void clearForm() {
        etName.setText("");
        etId.setText("");
        spMajor.setSelection(0);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
