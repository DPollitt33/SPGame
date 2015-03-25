package seniorproject.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import JavaFiles.BluetoothService;
import JavaFiles.Constants;

/**
 * ----------READ ME----------
 * As of now this only allows for a two way communication.  4 way communication seems to be a bit more complicated and is a work in progress.
 * One problem with this app is once another activity is open the connection seems to be lost.  I need to solve this problem first.
 * Meanwhile I set it up so that as long as your within this activity you should be able to successfully pass Strings between two devices.
 * All you need to do is call the 'sendMessage()' method and pass in the desired String within the 'send String' set action listener method.
 * This way you can at least test features of our game as I begin to work on improving the bluetooth capabilities.
 * ----------READ ME----------
 */
public class MainActivity extends ActionBarActivity {

    Context context;
    private static final String LOG = "Matt";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBluetoothAdapter = null;
    private StringBuffer mOutStringBuffer;
    private BluetoothService mBluetoothService = null;
    private String mConnectedDeviceName = null;

    TextView mainTextView;
    TextView sentMessage;
    Button host;
    Button join;
    Button sendString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mainTextView.setText("Bluetooth is not supported");
        }
        //If bluetooth is not on request it to be enabled.
        /*if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //REQUEST_ENABLE_BT is a field.
        }*/
        //Otherwise keep connecting
        //  if (mBluetoothService == null){
        setOnClickListeners();
        // Intent intent = new Intent(MainActivity.this, activity_test.class);
        // startActivity(intent);
        //  }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { //Seems to only be called from the list adapter
        Log.d(LOG, "onActivityResult");

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so start the new activity
                    //Intent intent = new Intent(MainActivity.this, activity_test.class);
                    //startActivity(intent);
                    //Temporarily reveal a button for test purposes only
                    sendString.setVisibility(View.VISIBLE);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(LOG, "BT not enabled");
                    //Toast.makeText(context, R.string.bt_not_enabled_leaving,
                    //       Toast.LENGTH_SHORT).show();
                    //context.finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        Log.d(LOG, "connectDevice");
        Log.d(LOG, data.toString());

        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBluetoothService.connect(device, secure);
    }

    public void setOnClickListeners() {
        Log.d("MATT", "setOnClickListeners()");
        host.setOnClickListener(new View.OnClickListener() { //The variable name is misleading, make the current device discoverable.
            public void onClick(View v) {
                makeDiscoverable();
            }
        });
        join.setOnClickListener(new View.OnClickListener(){ //Call an intent to bring up a listView of paired devices.
            public void onClick(View v) { //Open list of devices
                Intent serverIntent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });
        sendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Matt", "TEST");
                sendMessage("Hello World!");
            }
        });
    }

    private void makeDiscoverable() {
        Log.d(LOG, "ensureDiscoverable()");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void init() {
        mainTextView = (TextView) findViewById(R.id.mainTextView);
        sentMessage = (TextView) findViewById(R.id.sentMessage);
        host = (Button) findViewById(R.id.host);
        join = (Button) findViewById(R.id.join);
        sendString = (Button) findViewById(R.id.sendString);
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothService = new BluetoothService(context, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        context = this;
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String handler = "Handler1";
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.d(handler, "STATE_CONNECTED");

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d(handler, "STATE_CONNECTING");
                            break;
                        case BluetoothService.STATE_LISTEN:
                            Log.d(handler, "STATE_LISTEN");
                            break;
                        case BluetoothService.STATE_NONE:
                            Log.d(handler, "STATE_NONE");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    Log.d(handler, "MESSAGE_WRITE");
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(context, "Sent: " + writeMessage, Toast.LENGTH_SHORT);
                    break;
                case Constants.MESSAGE_READ:
                    Log.d(handler, "MESSAGE_READ");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    sentMessage.setText(readMessage); // Temporary
                    Toast.makeText(context, "Received: " + readMessage, Toast.LENGTH_SHORT);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:

                    Log.d(handler, "MESSAGE_DEVICE_NAME");
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != context) {
                        Toast.makeText(context, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(MainActivity.this, activity_test.class);
                        //startActivity(intent);
                        sendString.setVisibility(View.VISIBLE);
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d(handler, "MESSAGE_TOAST");
                    if (null != context) {
                        Toast.makeText(context, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

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









    //DEMO PURPOSES ONLY!
    private void sendMessage(String message) {
        Log.d(LOG, "sendMessage(" + message + ")");

        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
}
