package com.parse.starter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

/**
 * Created by Jizhizi Li on 15/09/2015.
 */

public class BlueTooth extends ActionBarActivity {

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> newArrayAdapter;
    private ArrayAdapter<String> pairedArrayAdapter;
    private ListView pairedView;
    private ListView newView;
    private Button search;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    final int REQUEST_ENABLE_BT = 1;

    private Button visible;
    private final static UUID uuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private ServerThread myServer;
    private ClientThread myConnection;
    private Bitmap sendbitmap;
    private Bitmap receivebitmap;
    private ImageView result ;
    private Button receive;
    private TextView text;
private byte[] receivebuffer = new byte[8192];
    private Button home;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);


mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //<----------------Matching with ID---------------->

        pairedView = (ListView) findViewById(R.id.pairedView);
        newView = (ListView) findViewById(R.id.newView);
        search = (Button) findViewById(R.id.search);
        visible = (Button) findViewById(R.id.visible);
        result = (ImageView)findViewById(R.id.result);
        receive = (Button)findViewById(R.id.receive);
        text = (TextView)findViewById(R.id.text);
        home = (Button)findViewById(R.id.home);
        //<---------SET VIEW TO GONE----------------->
        pairedView.setVisibility(View.GONE);
        newView.setVisibility(View.GONE);

receive.setEnabled(true);
        search.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        //<------------------------Receiving image from last intent-------------------------->
        if (getIntent().hasExtra("byteArray")) {

            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            sendbitmap = b;

            //<---------SET VIEW TO visible----------------->
            pairedView.setVisibility(View.VISIBLE);
            newView.setVisibility(View.VISIBLE);
result.setVisibility(View.GONE);
search.setVisibility(View.VISIBLE);

receive.setVisibility(View.GONE);

        }

        //<------set up bluetooth------>
//        if (mBtAdapter != null) {
//            System.out.println("there is no bluetooth");
//        }
        //<-----------------Enable bluetooth--------->
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //<-------------Search Button----------------------->
        // Initialize the button to perform device discovery

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();

            }
        });

        // <-----------------Visible Button--------------->


        visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

            }
        });

        //<----------Initialise array adapters--------------->

        pairedArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        newArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        //<----------Setup listView-------------->

        pairedView.setAdapter(pairedArrayAdapter);
        pairedView.setOnItemClickListener(mDeviceClickListener);
        newView.setAdapter(newArrayAdapter);
        newView.setOnItemClickListener(mDeviceClickListener);


        //<----------Register -------------->

        //   Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


//       //  Register for broadcasts when discovery has finished
//
//
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
//


        //<==============================================================>
        myServer = new ServerThread();
        myServer.start();

        // result.setImageBitmap(receivebitmap);

//<==============================================================>


        //   Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                pairedArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedArrayAdapter.add(noDevices);
        }


        //<-------------Receive Button----------------------->
        // Initialize the button to perform device discovery

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     System.out.println("enter result button" + receivebitmap.getHeight());

                result.setImageBitmap(receivebitmap);
System.out.println("InfoChange: press the receive");

            }
        });



        //<-------------Home Button----------------------->
        // Initialize the button to perform device discovery

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     System.out.println("enter result button" + receivebitmap.getHeight());

              myServer.cancel();
         //       myConnection.cancel();


              Intent intent = new Intent(BlueTooth.this,MainActivity.class);
                startActivity(intent);


            }
        });




    }


//<----------Method doDiscovery-------------->

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        System.out.println("doDiscovery");
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }


//<----------Method mDeviceClickListener-------------->
    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(address);


            //<-------------can i use listening thread here?---------->


            System.out.println("seems run?" + address);


            //<==============================================================>
           myConnection = new ClientThread(bluetoothDevice,sendbitmap);
            myConnection.start();
            //<==============================================================>







        }
    };


    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("something");
                if (newArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newArrayAdapter.add(noDevices);
                }
            }
        }
    };


    //<-------------------Bluetooth Server/Receive Thread---------------------------------->

    private class ServerThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;
        private InputStream mmInStream;



        public ServerThread() {
            BluetoothServerSocket temp = null;

            System.out.println("InfoChange: Waiting for connection......");
            text.setText("Waiting for connection......");


            try {
                temp = mBtAdapter.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), uuid);

            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothServerSocket = temp;
        }

        public void run() {

            System.out.println("begin connection");

            BluetoothSocket connectSocket = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            String line = "";
            // This will block while listening until a BluetoothSocket is returned
            // or an exception occurs
            while (true) {
                try {
                    connectSocket = bluetoothServerSocket.accept();
                    mBtAdapter.cancelDiscovery();
                } catch (IOException e) {
                    System.out.println("connection failed");
                    break;
                }
                // If a connection is accepted
//<------------try of send image>

                int byteNo;
                InputStream tmpIn = null;

                if (connectSocket != null) {


                    try {
                        tmpIn = connectSocket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mmInStream = tmpIn;


                    try {
                        byteNo = mmInStream.read(receivebuffer);
                        System.out.println("InfoChange: Begin to receive image......");
                       // text.setText("Begin to receive image......");

                       // Toast.makeText(BlueTooth.this,"Begin to receive image......",Toast.LENGTH_LONG);

                        if (byteNo != -1) {
                            //ensure DATAMAXSIZE Byte is read.
                            int byteNo2 = byteNo;

                           // int bufferSize = 7340;
                            int bufferSize = 7340;
                            while(byteNo2 != bufferSize){
                                bufferSize = bufferSize - byteNo2;
                                byteNo2 = mmInStream.read(receivebuffer, byteNo, bufferSize);


                                receivebitmap = BitmapFactory.decodeByteArray(receivebuffer,0,receivebuffer.length);

                                //<---------------when to set bitmap????---------->

if(receivebitmap!=null){
System.out.println("InfoChange:Receive bitmap "+receivebitmap.getHeight());







}
//
                                System.out.println("InfoChange: Receive buffer"+receivebuffer.length);


//                                if(receivebitmap.sameAs(sendbitmap)){
//
//
//                                    System.out.println("InfoChange: Size are same");
//
//                                }

                                System.out.println("InfoChange: Receive successfully! Please press receive button to receive it.");

                              //  text.setText("Receive successfully! Please press receive button to receive it.");

                                //<--------------->


                                if(byteNo2 == -1){
                                    break;
                                }
                                byteNo = byteNo+byteNo2;
                            }
                        }





                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//                                System.out.println("InfoChange: here set receive"+receivebitmap.getHeight());
                   // text.setText("heheheh");
                   // receive.setEnabled(true);

                }




                //<------------try end---------->
                    }





                }








        // Cancel the listening socket and terminate the thread
        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }




    //<---------------Bluetooth Client/sender Thread-------------------->


    private class ClientThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;
        Bitmap send;

        public ClientThread(BluetoothDevice device,Bitmap b) {

            send = b;

            System.out.println("InfoChange: Connecting a device......");
            text.setText("Connecting a device......");

            BluetoothSocket temp = null;
            bluetoothDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothSocket = temp;
        }

        public void run() {
            InputStream inStream = null;
            OutputStream outStream = null;
            // Cancel any discovery as it will slow down the connection
            mBtAdapter.cancelDiscovery();

            try {
                // This will block until it succeeds in connecting to the device
                // through the bluetoothSocket or throws an exception
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                connectException.printStackTrace();

                //<---------WHY CLOSE!!!!>
//                try {
//                    bluetoothSocket.close();
//                } catch (IOException closeException) {
//                    closeException.printStackTrace();
//                }


                //<------maybe NEED------>
            }



            //<--------------try sending photo------------->

            try {
                outStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            System.out.println("InfoChange: sendbitmap" + send.getHeight());





           send.compress(Bitmap.CompressFormat.JPEG, 10, baos);

            System.out.println("InfoChange: sendByteArray"+baos.toByteArray().length/1024);


            byte[] b =baos.toByteArray();




                try {
                System.out.println("InfoChange: Begin to send image......");
                //text.setText("Begin to send image......");
                outStream.write(b);
                outStream.flush();
                System.out.println("InfoChange: Successfully sended image!");
               // text.setText("Successfully sended image!");
            } catch (IOException e) {
                e.printStackTrace();
                    System.out.println("InfoChange: error with receiving"+e.getMessage());

            }


            //<---------finish sending photo---------->

//text.setText("Finish Sending Image!");
System.out.println("InfoChange: seems finish receiving?");




        }

        // Cancel an open connection and terminate the thread
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

//<---------who knows---------->
    @Override
    protected void onStop(){

        unregisterReceiver(mReceiver);
        super.onStop();


    }


}
