package br.com.adley.flashairdownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class FlashAirRequest {
    public static String getString(String command) {
        String result = "";
        try {
            URL url = new URL(command);
            URLConnection urlCon = url.openConnection();
            urlCon.connect();
            InputStream inputStream = urlCon.getInputStream();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = bufreader.readLine()) != null) {
                if (!stringBuilder.toString().equals("")) stringBuilder.append("\n");
                stringBuilder.append(str);
            }
            result = stringBuilder.toString();
        } catch (MalformedURLException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        return result;
    }


    public static String getBase64Image(String  url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }

    public static Bitmap getBitmap(String command) {
        Bitmap resultBitmap = null;
        try {
            URL url = new URL(command);
            URLConnection urlCon = url.openConnection();
            urlCon.connect();
            InputStream inputStream = urlCon.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(byteChunk)) != -1) {
                byteArrayOutputStream.write(byteChunk, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inPurgeable = true;
            resultBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bfOptions);
            byteArrayOutputStream.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        return resultBitmap;
    }


}
