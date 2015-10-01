package com.zensar.navigateme.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zensar.navigateme.R;
import com.zensar.navigateme.dao.DatabaseManager;
import com.zensar.navigateme.dto.Location;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportActivity extends AppCompatActivity {
    DatabaseManager databaseManager;
    Button buttonEmail;
    ArrayList<Location> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        databaseManager = new DatabaseManager(this);
        buttonEmail = (Button) findViewById(R.id.button);
        locationArrayList = databaseManager.getLocations();
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToCSV(locationArrayList);
            }
        });
    }

    private void writeToCSV(ArrayList<Location> locationArrayList) {
        try {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "AnalysisData.csv";
            String filePath = baseDir + File.separator + fileName;
            File f = new File(filePath);
            CSVWriter writer;
            if (f.exists() && !f.isDirectory()) {
//                FileWriter mFileWriter = new FileWriter(filePath, true);
//                writer = new CSVWriter(mFileWriter);
                writer = new CSVWriter(new FileWriter(filePath));
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            for (int i = 0; i < locationArrayList.size(); i++) {
                Location location = locationArrayList.get(i);
                String[] data = {"Address : " + location.getLocationName(), "Latitude : " + location.getLatitude(), "Longitude : " + location.getLongitude(), "Speed : " + location.getSpeed() + " km/hr", "X : " + location.getX(), "Y : " + location.getY(), "Z : " + location.getZ(), "Date/Time : " + location.getDateTime()};
                writer.writeNext(data);
            }
            writer.close();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "GeoTracker CSV");
            intent.putExtra(Intent.EXTRA_TEXT, "GeoTracker CSV");
            /*File root = Environment.getExternalStorageDirectory();
            File file = new File(root, xmlFilename);
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Uri uri = Uri.parse("file://" + file);*/
            Uri uri = Uri.parse("file://" + filePath);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
