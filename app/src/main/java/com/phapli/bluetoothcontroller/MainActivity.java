package com.phapli.bluetoothcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    // This Activity demo a bluetooth connection with support of a library {@link https://github.com/akexorcist/Android-BluetoothSPPLibrary}
    BluetoothSPP bt;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bt = new BluetoothSPP(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                sendMessage();
            }
        });

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(this, R.string.bluetooth_not_available, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendMessage() {
        bt.send("Message", true);
        // or
        bt.send(new byte[] { 0x30, 0x38, 0x12}, false);
    }

    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            Toast.makeText(this, R.string.bluetooth_enabled, Toast.LENGTH_LONG).show();
            startBluetoothService();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.bluetooth_open_setting)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, BluetoothState.REQUEST_ENABLE_BT);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            // Create the AlertDialog object and return it
            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    private void startBluetoothService() {
        //For connection with android device
        bt.startService(BluetoothState.DEVICE_ANDROID);

        //For connection with any microcontroller which communication with bluetooth serial port profile module
        //bt.startService(BluetoothState.DEVICE_OTHER);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, "OnDataReceivedListener ->\ndata " + data + "\nmessage " + message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(MainActivity.this, "BluetoothConnectionListener -> onDeviceConnected" + "\nname: " + name + "\taddress "+ address, Toast.LENGTH_SHORT).show();
                fab.setVisibility(View.VISIBLE);
            }

            public void onDeviceDisconnected() {
                Toast.makeText(MainActivity.this, "BluetoothConnectionListener -> onDeviceDisconnected", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(MainActivity.this, "BluetoothConnectionListener -> onDeviceConnectionFailed", Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Toast.makeText(MainActivity.this, "BluetoothStateListener -> STATE_CONNECTED", Toast.LENGTH_SHORT).show();
                    fab.setVisibility(View.VISIBLE);
                } else if (state == BluetoothState.STATE_CONNECTING){
                    Toast.makeText(MainActivity.this, "BluetoothStateListener -> STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                } else if (state == BluetoothState.STATE_LISTEN) {
                    Toast.makeText(MainActivity.this, "BluetoothStateListener -> STATE_LISTEN", Toast.LENGTH_SHORT).show();
                }else if (state == BluetoothState.STATE_NONE) {
                    Toast.makeText(MainActivity.this, "BluetoothStateListener -> STATE_NONE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Intent to choose device activity
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        bt.stopService();
        fab.setVisibility(View.INVISIBLE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                startBluetoothService();
            } else {
                // Do something if user doesn't choose any device (Pressed back)
                Toast.makeText(this, R.string.bluetooth_not_connect_device, Toast.LENGTH_LONG).show();
            }
        }
    }
}
