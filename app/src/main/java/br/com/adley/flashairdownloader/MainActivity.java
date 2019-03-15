package br.com.adley.flashairdownloader;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mBtnGet;
    private String mGetUrlNumberItems = "http://flashair/command.cgi?op=101&DIR=/DCIM";
    private String mGetUrlListItems = "http://flashair/command.cgi?op=100&DIR=/DCIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBtnGet = findViewById(R.id.btnGet);
        mBtnGet.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNumberItems();
                GetItemsResult();
            }
        });
    }

    public void GetNumberItems(){
        Thread getItemsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String result = FlashAirRequest.getString(mGetUrlNumberItems);
                    TextView numberItems = findViewById(R.id.numberItems);
                    numberItems.setText(String.format("Items Found: %s", result));
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        getItemsThread.start();
    }

    public void GetItemsResult(){
        Thread getItemsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String result = FlashAirRequest.getString(mGetUrlListItems);
                    TextView itemsResult = findViewById(R.id.itemsResult);
                    itemsResult.setText(result);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        getItemsThread.start();
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
