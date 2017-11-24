package com.example.max.chatwithnotifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.BitSet;

/**
 * Created by Max on 24.11.2017.
 */

public class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

    ImageView img;

    public ImageDownloader(ImageView img)
    {
        this.img=img;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlToDisplay=strings[0];
        Bitmap image=null;
        try
        {
            InputStream in=new URL(urlToDisplay).openStream();
            image= BitmapFactory.decodeStream(in);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        img.setImageBitmap(bitmap);
    }
}
