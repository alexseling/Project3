package edu.msu.replicant.project3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

/**
 * This class will send a view as a bitmap to a remote server.
 */
public class ViewSender {
    /**
     * The URL for the server
     */
    private static final String URL = "https://facweb.cse.msu.edu/cbowen/cse476x/submitview.php";
    
    /**
     * Server magic number
     */
    private static final String MAGIC = "Vecre6e5uthE";
    
    /**
     * This is the function that sends the view. Note that it immediately makes
     * a bitmap image file from the view, but the send takes place in a thread.
     * @param activity An activity. This will be the target for a Toast when the send is complete.
     * @param view A view to send.
     * @param group A group name to associated with the view.
     */
    public void sendView(Activity activity, View view, String group) {
       
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        
        int width = view.getWidth();
        int height = view.getHeight();

        Bitmap bm = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();
        
        sendBitmap(activity, bm, group);
    }
    
    /**
     * Increment ID value to use
     */
    private int id = 1;
    
    private final static String BOUNDARY = "ServerCommFormBoundaryePkpFF7WxyAqx29L";
    private final static String LINEEND = "\r\n";
    private final static String TWOHYPHENS = "--";
    private final static int MAXBUFFERSIZE = 1024*1024;
    
    private void sendBitmap(final Activity activity, final Bitmap bm, final String group) {
        final String filename = "screengrab" + id++ + ".jpg";
        
        /*
         * Save the screen capture to a file
         */
        try {
            FileOutputStream fOut = activity.openFileOutput(filename, Context.MODE_PRIVATE);

            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            return;
        }
        
        FileInputStream istr;
        try {
            istr = activity.openFileInput(filename);
        } catch (Exception e) {
            return;
        }
        
        final FileInputStream fileInputStream = istr;
        
        // Run this processing in a thread
        new Thread(new Runnable() {

            @Override
            public void run() {

                
                /*
                 * Upload the file to the server
                 */
                try
                {
                    URL url = new URL(URL);

                    // Create a new HTTPS connection
                    // and get a stream
                    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                    
                    // Enable POST method
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);

                    // Allow Inputs & Outputs
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

                    DataOutputStream outputStream = new DataOutputStream( connection.getOutputStream() );
                    
                    /*
                     * Group name
                     */
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + LINEEND);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"group\"\r\n");
                    outputStream.writeBytes(LINEEND);
                    
                    outputStream.writeBytes(group);
                    outputStream.writeBytes(LINEEND);
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + LINEEND);
                    
                    /*
                     * Magic number
                     */
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + LINEEND);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"magic\"\r\n");
                    outputStream.writeBytes(LINEEND);
                    
                    outputStream.writeBytes(MAGIC);
                    outputStream.writeBytes(LINEEND);
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + LINEEND);
                    
                    /*
                     * Image
                     */
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + LINEEND);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"x.jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n");
                    outputStream.writeBytes(LINEEND);

                    int bytesAvailable = fileInputStream.available();
                    int bufferSize = Math.min(bytesAvailable, MAXBUFFERSIZE);
                    byte [] buffer = new byte[bufferSize];

                    // Read file
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0)
                    {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, MAXBUFFERSIZE);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(LINEEND);
                    outputStream.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + LINEEND);
                    outputStream.flush();

                    // Responses from the server (code and message)
                    if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        // We have failed
                        toasted(activity, false);
                        return;
                    }
                    connection.getResponseMessage();
                    
//                    InputStream stream = connection.getInputStream();
//                    String response = inputStreamToString(stream);
//                    Log.i("476", response);
                    
                    fileInputStream.close();
                    outputStream.close();
                    
                    activity.deleteFile(filename);
                }
                catch (Exception ex)
                {
                    toasted(activity, false);
                }
                
                toasted(activity, true);
            }
            
        }).start();
        

    }
    
    /**
     * Notify the foreground activity that a view has been submitted
     * @param activity Activity to notify
     * @param success True if successful
     */
    private void toasted(final Activity activity, final boolean success) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(success) {
                    Toast.makeText(activity, "View successfully submitted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Submission of view failed!", Toast.LENGTH_LONG).show();
                }
            }
            
        });
    }
    
    public static String inputStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();
        return sb.toString();
    }

}