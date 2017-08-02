package android.cloud.microsoft.com.serviceapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;




public class MyService extends Service {
    //tells if service is running or not.
    public static boolean isRunning=false;

    Thread mythread;
    Worker runnable;
    AppState appState;

    IBinder bindObject = new ServiceBinder();

    public String getStatus()
    {
        return runnable.getStatus();
    }

    //setter for MainActivity object
    public void setAppState(MainActivity obj)
    {
        runnable.checkServiceObj();
        appState = obj;
        Log.i("NullCheck","setAppState");
        if(appState!=null)
        {
            Log.i("NullCheck","Not null");
        }
    }

    @Override
    public void onCreate()
    {
        isRunning = true;
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        Log.i("NullCheck","onCreate");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("NullCheck","onStartCommand");

        try {
            if(runnable == null && mythread == null) {
                runnable = new Worker(this);
                mythread = new Thread(runnable);
            }
            else
            {
                Log.i("NullCheck","Assigned");
            }


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
    public IBinder onBind(Intent arg0) {
        Log.i("NullCheck","onBind");
        return bindObject;
    }


    @Override
    public void onDestroy() {

        Log.i("NullCheck","onDestroy");
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        runnable.setTerminateFlag();
        runnable.clearResources();

        mythread=null;
        isRunning = false;
    }

    public void display(String msg)
    {
        Log.i("NullCheck","It works");

        appState.isConnectedState(msg);

    }

    public class ServiceBinder extends Binder {
        MyService getService()
        {return MyService.this;}

    }

    public void refreshWindow()
    {
        appState.refreshPage();
    }


}
