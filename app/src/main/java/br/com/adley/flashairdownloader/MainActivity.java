package br.com.adley.flashairdownloader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
                //GetNumberItems();
            }
        });
    }

    public void GetNumberItems() {
        Thread getItemsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = FlashAirRequest.getString(mGetUrlNumberItems);
                    TextView numberItems = findViewById(R.id.numberItems);
                    numberItems.setText(String.format("Items Found: %s", result));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        getItemsThread.start();
    }

    public void GetItemsResult() {
        Thread getItemsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = FlashAirRequest.getString(mGetUrlListItems);
                    //TextView itemsResult = findViewById(R.id.itemsResult);
                    //itemsResult.setText(result);
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
                    DownloadImages(0);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        getItemsThread.start();
    }

    public void DownloadImages(final int index) {
            final FileModel pic = mFileList.get(index);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlDownload = "http://flashair" + pic.getDirectory() + "/" + pic.getFilename();
                    Bitmap result = FlashAirRequest.getBitmap(urlDownload);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    pic.setBase64(base64);
                    if(mFileList.size() <= index)
                        DownloadImages(index+1);
                    else
                        ShowResults();
                /*byte[] data = null;
                int fileSize = Integer.parseInt(pic.getSize());

                try {

                    URL url = new URL(urlDownload);
                    URLConnection urlCon = url.openConnection();
                    urlCon.connect();
                    InputStream inputStream = urlCon.getInputStream();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    byte[] byteChunk = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(byteChunk)) != -1) {
                        byteArrayOutputStream.write(byteChunk, 0, bytesRead);
                        byteArrayOutputStream = new ByteArrayOutputStream();

                        byteChunk = new byte[fileSize];
                        bytesRead = 0;
                        while ((bytesRead = inputStream.read(byteChunk)) != -1) {
                            byteArrayOutputStream.write(byteChunk, 0, bytesRead);
                        }

                        data = byteArrayOutputStream.toByteArray();
                        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                        pic.setBase64(base64);
                        byteArrayOutputStream.flush();
                        byteArrayOutputStream.close();
                        //pic.getBase64();
                    }
                    System.out.println("-----------------FOI----------------------------------");
                    ShowResults();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                }
            }).start();
        }

    public void ShowResults(){
        System.out.println(mFileList.get(0).getBase64());
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
