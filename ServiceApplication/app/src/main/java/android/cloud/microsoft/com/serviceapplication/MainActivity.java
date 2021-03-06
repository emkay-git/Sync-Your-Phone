package android.cloud.microsoft.com.serviceapplication;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements  AppState, Observer {

    // This app has 2 access point to start the socket connection and file transfer
    //
    //
    // 1) Use the Wifi on/off button of phone.
    // If activity is closed a)Wifi is on - starts service, no binding can share files.
    //                       b)Wifi is off - stops service, can't share file
    //
    // If activity is open a)Wifi is on - starts service, activity binds to service and displays
    //                     displays text in text box "Connected to Phone"
    //                     b)Wifi is off - stops service, activity unbinds to service and displays
    //                     text in the text box "Connect to Wifi first"
    //
    // 2) Use Start service/Stop service button i.e. activity is opened.
    // If wifi is off - message displayed is "Connect to internet first"
    //
    // If wifi is on - Depeneding on button - "Connected to Phone" "Disconnected" is dipslayed



    MyService serviceObject;
    boolean isBound = false;
    private boolean wifiOff = true;

    // onCreate method starts when activity starts. It peforms following task
    // 1) Registers with the Observable pattern so that it can listen to wifi state change.
    // 2) It gets the text view and set it to "Connect to Wifi first".
    // 3) It then checks if service is already running, if it does it indicates wifi is on
    // hence binds activity to the service which then reset the text in the text box.

    RecyclerView recyclerView;
    CustomAdapter adapter;
    //listContentArr is passed to the adapter,
    private ArrayList<FilePOJO> listContentArr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ObservableObject.getInstance().addObserver(this);
        CheckConnectivity cc = new CheckConnectivity(getApplication());

        TextView text = (TextView) findViewById(R.id.appState);

        text.setText("Connect to wifi first");
        if(cc.isNetworkAvailable()) {
            text.setText("Start service to share files");
            wifiOff = false;
        }
            if(MyService.isRunning)
        {

            wifiOff = false;
            Log.i("NullCheck","ActivityStarts");
            Intent intent = new Intent(this, MyService.class);
            if(!(isBound))
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }




        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        //LinearLayoutManager uses linear layout within recycler view.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //passing current instance of Activity to CustomAdapter and also instantiating CustomAdapter object adapter
        adapter=new CustomAdapter(this,this);
        //Method call for populating the view
        populateRecyclerViewValues("root");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // Unbind the activity if already not when activity is going to be closed.
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // Unbind from the service
//        if (isBound) {
//            unbindService(mConnection);
//
//        }
//    }

    // Method to start the service using button Start Service button press.
    // It starts the socket. The message displayed in text box is obtained initially via method call
    // in onServiceConnected that in turns call isConnectedState;
    // after that onwards, isConnectedState is called itself, whenever state changes.
    public void startService(View view) {

            if(!wifiOff) {
                Intent intent = new Intent(this, MyService.class);

                if (!(isBound))
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


                startService(intent);

            }

    }


    // Method to stop the service using Stop Service button press.
    // It closes the socket which then prints "Disconnected" in the text box.
    public void stopService(View view) {
        if(!wifiOff) {
            if (MyService.isRunning) {

                if (isBound) {
                    Log.i("NullCheck", "Unbinding");
                    unbindService(mConnection);
                    isBound = false;

                }
                stopService(new Intent(getBaseContext(), MyService.class));
            }
        }
    }



    //callback; when service is connected onServiceConnected happens;
    // whenever activity binds to the service following operations are done
    // 1) Get the object of the MyService class and using it set the object of this class in the
    // my service class.
    // 2) Get the status of the socket connection and display it in the text box. It is obtained via
    // getStatus() method of the service class.

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("NullCheck", "ServiceConnection");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.ServiceBinder binder = (MyService.ServiceBinder) service;
            serviceObject = binder.getService();
            isBound = true;

            serviceObject.setAppState(MainActivity.this);
//            serviceObject.display("Disconnected");
            String appStatus = serviceObject.getStatus();
            isConnectedState(appStatus);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            Log.i("NullCheck","ServiceDisconnected");
           isBound = false;

        }

    };

    // this method (the method i took so much trouble.. :P you fucked man) reports the
    // status of the socket connection;
    // Following may be the output
    // When Wifi off - "Connect to Wifi first"
    // When wifi on and Service started - "Connected to Phone"
    // When wifi on and Service stopped - "Disconnected"
    // When receiving data - "Phone is receiving Data"
    // When wifi on, service stopped and activity opened - "Start Service to Share files"

    @Override
    public void isConnectedState(final String message) {
       final TextView text = (TextView) findViewById(R.id.appState);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.i("MainDisplay", "OK");

            if(wifiOff)
                text.setText("Connect to internet first");
            else
                text.setText(message);

            }
        });

    }

    // it's an observable design pattern; used to get notified about the changes in wifi connection;
    // it's to take care of the case when wifi is on/off with activity opened;
    // when wifi is on while activity is opened service started already in the broadcast receiver
    // but binding is performed here to notice the changes in the text box
    // when wifi is off while activity is opened service stopped already in broadcast receiver
    // but unbinding if any is performed here.

    @Override
    public void update(Observable observable, Object data) {
       if(String.valueOf(data).compareTo("false")==0)
       {
           wifiOff = true;
           if(isBound)
               unbindService(mConnection);
                isBound = false;
            isConnectedState("any");
       }
        else {
           wifiOff = false;
           if(!isBound) {
               Intent intent = new Intent(this,MyService.class);
               bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
               isBound = true;
           }

       }
    }


    // Each Linear layout in Recyler view will be filled up by the FilePOJO object.
    // So it creates the array of all the files in the folder specified by fileName variable
    // the arraylist created is passed to the adapter then.
    // Method takes fileName which can be root or other sub folder in root.
    public void populateRecyclerViewValues(String fileName) {


        listContentArr = new ArrayList<>();

        FileList l = new FileList(fileName);
        String [][] data = l.getFileList();



        for(int i = 0 ;i< data.length;i++) {

            FilePOJO pojoObject = new FilePOJO();

            pojoObject.setFileName(data[i][0]);
            pojoObject.setDetail(data[i][1]);
            pojoObject.setFileImage(data[i][2]);

            listContentArr.add(pojoObject);
        }
        //We set the array to the adapter
        adapter.setListContent(listContentArr);
        //We in turn set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
    }


    //Method gets called whenever back button is pressed on the hardware. If I am at the root
    // folder then it goes back in the background. If not then it goes to the previous folder.
    @Override
    public void onBackPressed() {
        if(adapter.goBack())
            moveTaskToBack(true);
    }


    //whenever a file is clicked it should open. This method performs that.
    //pdf, mp3, text, image are taken care of. Need to make smarter way to guess mime types
    // and play that.
    public void playFile(String clickedFile,String type)
    {
        File file = new File(clickedFile);
        Intent target = new Intent(Intent.ACTION_VIEW);
        if(type.compareTo("pdf")==0)
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
        else if(type.compareTo("mp3")==0)
            target.setDataAndType(Uri.fromFile(file),"audio/*");
        else if(type.compareTo("txt")==0)
            target.setDataAndType(Uri.fromFile(file),"text/plain");
        else
            target.setDataAndType(Uri.fromFile(file),"image/*");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }


    //whenever download is completed from the system layout should refresh itself, to perform that
    // refreshPage is used, which is called when transfer is done by Worker class to method in the Service class,
    //which finally calls this method.
    public void refreshPage()
    {
        populateRecyclerViewValues("root");
    }


}
