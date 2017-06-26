package android.cloud.microsoft.com.serviceapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

//this implements the actual transfer of data
class Worker implements Runnable
{
    ServerSocket serverSocket=null;
    Socket clientSocket=null;
    BufferedReader readMetaData=null;
    BufferedInputStream br=null;
    PrintWriter out=null;
    BufferedOutputStream bufferOut;

    @Override
    public void run() {


        try {


            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {

                //This provides the path of Music Directory as given in my phone. emulated/0/music/
                final File musicFilePath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC);


                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(9998));

                while (true) {
                    Log.i("Log", "Waiting for Client connection...");
                    clientSocket = serverSocket.accept();
                    Log.i("Log", "Connected to Client...");
                    Log.i("Log", "Receiving Meta Data");


                    readMetaData = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    br = new BufferedInputStream(clientSocket.getInputStream());


                    String rawData = null;
                    while ((rawData = readMetaData.readLine()) != null) {

                        JSONObject metaData = new JSONObject(rawData);
                        Log.i("MetaDataContent", rawData);

                        String mp3Name = metaData.getString("mp3Name");
                        long mp3Size = Long.parseLong(metaData.getString("mp3Size"));


                        out.println("Server:Meta Data receieved");

                        Log.i("Log", "Receiving File");

                        File mp3File = new File(musicFilePath, mp3Name);


                        bufferOut = new BufferedOutputStream(new FileOutputStream(mp3File));


                        byte[] buffer = new byte[1024 * 128];

                        // Looping till we reach the end of file i.e value returned is -1 which is when socket gets closed
                        int count = 0;
                        int recv = 0;

                        while (recv < mp3Size && (count = br.read(buffer)) > 0) {

                            bufferOut.write(buffer, 0, count);

                            recv += count;
                            Log.i("Receiving", "Data" + recv + "\n");


                        }
                        out.println("Server: File Received");

                        Log.i("Log", "Received Successfully");


                    }

                    br.close();
                    out.close();
                    readMetaData.close();
                    bufferOut.close();

                }
            }
            else Log.i("Status","SD card not available");
        }

            catch(Exception e){
                e.printStackTrace();
                Log.e("ErrorLog", "" + e.getMessage());
            }

            finally{
                // deallocating resources
                try {
                    clientSocket.close();
                    serverSocket.close();
                    readMetaData = null;
                    br = null;
                    out = null;
                    bufferOut = null;

                } catch (Exception e) {
                    String error = e.getMessage();
                    if (error != null)
                        Log.i("Try in Try", error);
         }
            }

    }

    public void clearResources()
    {

        readMetaData=null;
        br=null;
        out=null;
        bufferOut=null;

        try {
            if(clientSocket!=null&&clientSocket.isConnected())
            clientSocket.close();
            else
            {
                clientSocket=null;
            }

            serverSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String error=e.toString();
            if(error!=null)
            Log.i("Try in Try2",error);
        }
    }

}



public class MyService extends Service {

    Thread mythread;
    Worker runnable;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        runnable = new Worker();
        mythread=new Thread(runnable);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {
            mythread.start();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Service already Started", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            String error=e.getMessage();
            if(error!=null)
            Log.i("onStartCommand",error);
        }


        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        runnable.clearResources();
        mythread=null;

    }
}