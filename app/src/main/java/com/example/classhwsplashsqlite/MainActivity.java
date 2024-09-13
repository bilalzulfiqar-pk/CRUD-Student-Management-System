package com.example.classhwsplashsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTextName, editTextRollNo;
    Button buttonAdd, buttonRemove, buttonUpdate;
    ListView listViewStudents;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextRollNo = findViewById(R.id.editTextRollNo);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonRemove = findViewById(R.id.buttonRemove);
        listViewStudents = findViewById(R.id.listViewStudents);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // Create or open the database
        database = openOrCreateDatabase("StudentDB", MODE_PRIVATE, null);
        // Create table if not exists
        database.execSQL("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, roll_no INTEGER)");

        // Add student button click listener
        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String rollNoString = editTextRollNo.getText().toString().trim();
            if (!name.isEmpty() && !rollNoString.isEmpty()) {
                int rollNo = Integer.parseInt(rollNoString);
                addStudent(name, rollNo);
                displayStudents();
                editTextName.setText("");
                editTextRollNo.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter name and roll number", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove student button click listener
        buttonRemove.setOnClickListener(v -> {
            String rollNoString = editTextRollNo.getText().toString().trim();
            if (!rollNoString.isEmpty()) {
                int rollNo = Integer.parseInt(rollNoString);
                removeStudent(rollNo);
                displayStudents();
                editTextName.setText("");
                editTextRollNo.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter roll number", Toast.LENGTH_SHORT).show();
            }
        });

        // Update student button click listener
        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String rollNoString = editTextRollNo.getText().toString().trim();
            if (!name.isEmpty() && !rollNoString.isEmpty()) {
                int rollNo = Integer.parseInt(rollNoString);
                updateStudent(name, rollNo);
                displayStudents();
                editTextName.setText("");
                editTextRollNo.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter name and roll number", Toast.LENGTH_SHORT).show();
            }
        });

        listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Split the selected item into name and roll number
            String[] parts = selectedItem.split(" - ");
            // Set the name and roll number to the EditText fields
            editTextName.setText(parts[0]);
            editTextRollNo.setText(parts[1]);
        });

        // Display existing students
        displayStudents();
    }

    private void addStudent(String name, int rollNo) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("roll_no", rollNo);
        database.insert("students", null, values);
        Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
    }

    private void removeStudent(int rollNo) {
        int rowsAffected = database.delete("students", "roll_no=?", new String[]{String.valueOf(rollNo)});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Student removed successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No student found with the given roll number", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayStudents() {
        Cursor cursor = database.rawQuery("SELECT * FROM students", null);
        ArrayList<String> studentList = new ArrayList<>();
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex("name");
            int rollNoIndex = cursor.getColumnIndex("roll_no");
            if (nameIndex != -1 && rollNoIndex != -1) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    int rollNo = cursor.getInt(rollNoIndex);
                    studentList.add(name + " - " + rollNo);
                }
            } else {
                // Handle the situation where column indices are not found
                Toast.makeText(this, "Error: Column indices not found", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            // Handle the situation where the cursor is null
            Toast.makeText(this, "Error: Cursor is null", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        listViewStudents.setAdapter(adapter);
    }

    private void updateStudent(String name, int rollNo) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        int rowsAffected = database.update("students", values, "roll_no=?", new String[]{String.valueOf(rollNo)});
        if (rowsAffected > 0) {
            Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No student found with the given roll number", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the activity is destroyed
        if (database != null) {
            database.close();
        }
    }
}
