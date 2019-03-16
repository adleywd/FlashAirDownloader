package br.com.adley.flashairdownloader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.adley.flashairdownloader.Models.FileModel;

public class MainActivity extends AppCompatActivity {

    private Button mBtnGet;
    private String mGetUrlNumberItems = "http://flashair/command.cgi?op=101&DIR=/DCIM";
    private String mGetUrlListItems = "http://flashair/command.cgi?op=100&DIR=/DCIM";
    private String[] mTypes;
    private List<FileModel> mFileList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTypes = new String[3];
        mTypes[0] = ".png";
        mTypes[1] = ".jpeg";
        mTypes[2] = ".jpg";
        mBtnGet = findViewById(R.id.btnGet);
        mBtnGet.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemsResult();
            }
        });
    }

    public void GetItemsResult() {
        //Clear the list
        mFileList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = FlashAirRequest.getString(mGetUrlListItems);
                    String[] allLines = result.split("([\n])");
                    for (int i = 1; i < allLines.length; i++) {
                        String[] values = allLines[i].split(",");
                        FileModel file = new FileModel();
                        file.setDirectory(values[0]);
                        file.setFilename(values[1]);
                        file.setSize(values[2]);
                        file.setAttribute(values[3]);
                        file.setDate(values[4]);
                        file.setTime(values[5]);
                        for (String mType : mTypes) {
                            if (file.getFilename().endsWith(mType)) {
                                mFileList.add(file);
                                break;
                            }
                        }
                    }
                    // run on ui thread to update UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView numberItems = findViewById(R.id.numberItems);
                            numberItems.setText(String.format("Photos Found: %s", mFileList.size()));
                        }
                    });
                    DownloadImages(0);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    public void DownloadImages(final int index) {
        final FileModel file = mFileList.get(index);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlDownload = "http://flashair" + file.getDirectory() + "/" + file.getFilename();
                String result = FlashAirRequest.getBase64Image(urlDownload);
                mFileList.get(index).setBase64(result);
                if (index < mFileList.size()-1) {
                    DownloadImages(index + 1); // Recursive method
                } else {
                    //GetImagesBase64(0);
                    fakeSync();
                }

            }
        }).start();
    }

    public void fakeSync(){
        System.out.println("-----------------------------SYNC----------------------------");
    }

    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
