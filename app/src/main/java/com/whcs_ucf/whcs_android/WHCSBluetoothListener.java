package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jimmy on 6/15/2015.
 * The WHCSBluetoothListener is multipurpose. Despite the name of "listener" it acts as the method
 * for sending outgoing communications over Bluetooth as well. This is because it implements the
 * CommandSender Interface. The CommandSender interface allows any class that has commands to be
 * sent out to utilize the BluetoothListeners BluetoothSocket for sending out commands.
 *
 * The WHCSBluetoothListener has a thread that continuously checks for incoming data on a
 * BluetoothSocket. It is constantly checking to see if the data it has received constitutes a valid
 * WHCSResponse packet. It does this using its WHCSResponseParser object.
 *
 * The WHCSBlueToothListener works closely with a ResponeHandler. Whenever the ResponseParser has
 * found that it has received a complete response the BluetoothListener constructs the response and
 * hands it off to the ResponseHandler. In the architecture of WHCS, the CommandIssuer class provides
 * implements the interface for ResponseHandler.
 */
public class WHCSBluetoothListener implements Runnable, CommandSender {

    private BluetoothDevice mostRecentDevice;
    private BluetoothSocket socket;
    private ResponseHandler responseHandler;
    WHCSResponse.WHCSResponseParser responseParser = new WHCSResponse.WHCSResponseParser();
    //Used to tell the listener when to stop. It always checks if it needs to stop.
    private boolean shouldStop;
    private static WHCSBluetoothListener SingletonWHCSBluetoothListener;
    private static Thread ListenerThread;
    private ConnectThread connectThread;

    private WHCSBluetoothListener() {
        this.responseParser = new WHCSResponse.WHCSResponseParser();
        this.shouldStop = false;
    }

    private WHCSBluetoothListener(BluetoothSocket socket, ResponseHandler responseHandler) {
        this();
        this.socket = socket;
        this.responseHandler = responseHandler;
    }

    private WHCSBluetoothListener(CommandIssuer responseHandler) {
        this();
        this.responseHandler = responseHandler;
    }

    public static WHCSBluetoothListener GetSingletonCommandIssuer() {
        if(SingletonWHCSBluetoothListener == null) {
            SingletonWHCSBluetoothListener = new WHCSBluetoothListener();
        }
        return SingletonWHCSBluetoothListener;
    }

    public static WHCSBluetoothListener GetSingletonBluetoothListener(CommandIssuer responseHandler) {
        if(SingletonWHCSBluetoothListener == null) {
            SingletonWHCSBluetoothListener = new WHCSBluetoothListener(responseHandler);
        }
        return SingletonWHCSBluetoothListener;
    }

    /*
    The start method of the WHCSBluetoothListener is what creates the thread that monitors a BluetoothSocket.
    The start method has the task of getting the socket from a BluetoothDevice object and then connecting
    to that device. The connect call for a BluetoothSocket is blocking. In order to prevent other things from
    blocking the start function calls the asynchConnect method offered by WHCSBluetoothListener. This function
    was designed to create a ConnectThread that could asynchronously connect to the socket and then call a
    callback function when the socket was succesfully connected to. If the socket timesout or has some sort of
    an error, the callback has a method for handling that.
     */
    public void start(BluetoothDevice btDevice,final ConnectionMadeCallback cb) throws IOException{
        if(SingletonWHCSBluetoothListener.socket == null || !SingletonWHCSBluetoothListener.socket.isConnected()) {
            if(ListenerThread != null && ListenerThread.isAlive()) {
                SingletonWHCSBluetoothListener.stop();
            }
            SingletonWHCSBluetoothListener.setupBluetoothSocketFromDevice(btDevice);

            SingletonWHCSBluetoothListener.asynchConnect(new ConnectionMadeCallback() {
                @Override
                public void onSuccessfulConnection() {
                    ListenerThread = new Thread(SingletonWHCSBluetoothListener);
                    ListenerThread.start();
                    cb.onSuccessfulConnection();
                }

                @Override
                public void onTimeoutConnection() {
                    cb.onTimeoutConnection();
                }
            });
        }
    }

    public void run() {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            InputStream inStream = socket.getInputStream();
            int bytesRead = -1;
            this.responseParser.reset();
            while (true) {
                this.responseParser.reset();
                if(shouldStop) { return; }
                while(!this.responseParser.hasCompletelyParsed()) {
                    if(shouldStop) { return; }
                    bytesRead = inStream.read(buffer);
                    if (bytesRead != -1) {
                        this.responseParser.addData(buffer, bytesRead, this.responseHandler);
                        Log.d("WHCS-UCF", "BluetoothListener received: "+ Utils.HexStringFromByteArray(buffer, bytesRead));
                    }
                }

                socket.getInputStream();
            }
        } catch (IOException e) {
            Log.d("BLUETOOTH_COMMS", e.getMessage());
        }
    }

    public void stop() {
        if(connectThread != null && connectThread.isAlive()) {
            connectThread.setShouldStop();
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectThread = null;
        }
        this.shouldStop = true;
        if(this.socket != null) {
            this.closeSocket();
        }
        SingletonWHCSBluetoothListener = null;
    }

    /*
    Creates an instance of the ConnectThread class in order to try to connect to a bluetoothsocket.
    It is possible that while this is taking place that a caller may want to connect to a different socket.
    To do this the caller should first perform the shouldStop() call on the existing ConnectThread and then
    another ConnectThread can be created. If the shouldStop() function is called on a connect thread it will
    not call any of its callback functions because its execution path should be halted. There is no way to
    stop the ConnectThread while it is blocking on the connect() call, so preventing it from operating any
    further is the accpetable work around.
     */
    private void asynchConnect(final ConnectionMadeCallback cb) {
        connectThread = new ConnectThread(cb);
        connectThread.start();
    }

    private class ConnectThread extends Thread {
        private boolean shouldStop;
        private ConnectionMadeCallback cb;

        ConnectThread(ConnectionMadeCallback cb) {
            this.cb = cb;
            this.shouldStop = false;
        }

        @Override
        public void run() {
            try {
                Log.d("WHCS-UCF", "connecting to "+ SingletonWHCSBluetoothListener.socket.getRemoteDevice().toString());
                SingletonWHCSBluetoothListener.socket.connect();
                Log.d("WHCS-UCF", "connected");
                if(!this.shouldStop) {
                    this.cb.onSuccessfulConnection();
                } else if(SingletonWHCSBluetoothListener.socket != null) {
                    SingletonWHCSBluetoothListener.socket.close();
                }
            }
            catch(IOException e) {
                e.printStackTrace();
                Log.d("WHCS-UCF", e.toString());
                if(!this.shouldStop) {
                    this.cb.onTimeoutConnection();
                }
            }
        }

        public void setShouldStop() {
            this.shouldStop = true;
        }
    };

    public boolean isConnected() {
        return socket.isConnected();
    }


    public void performDebugResponseRead() {
        int bytesRead = 4;
        byte bytes[] = new byte[] {0x00, WHCSOpCodes.GET_STATUS_OF_BASE_STATION, 0x00, 0x02};
        this.responseParser.addData(bytes, bytesRead, responseHandler);
        if(!this.responseParser.hasCompletelyParsed()) {
            throw new Error("The byte array should be representing a completed WHCS packet.");
        }
        this.responseParser.reset();
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    private void setupBluetoothSocketFromDevice(BluetoothDevice device) throws IOException {
        this.mostRecentDevice = device;
        if(this.socket != null) {
            this.closeSocket();
        }
        this.socket = device.createInsecureRfcommSocketToServiceRecord(WHCSActivity.WHCS_BLUETOOTH_UUID);
    }

    public void closeSocket() {
        if(this.socket.isConnected()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("WHCS-UCF", "Could not close BluetoothListener Socket.");
            }
        }
    }

    public BluetoothDevice getBluetoothDevice() {
        return mostRecentDevice;
    }

    public void setBluetoothDevice(BluetoothDevice device) {
        mostRecentDevice = device;
    }

    public void refreshConnection() {
        if(this.socket.isConnected()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.setupBluetoothSocketFromDevice(mostRecentDevice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendOutCommand(WHCSCommand command) {
        if(DebugFlags.DEBUG_BLUETOOTH_COMM_PIPELINE) {
            performDebugResponseRead();
            return;
        }
        else {
            try {
                this.socket.getOutputStream().write(0x1B);
                Log.d("WHCS-UCF", "byte sending: " + Utils.HexStringFromByteArray(command.toByteArray()));
                this.socket.getOutputStream().write(command.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("WHCS-UCF", "Exception: " + e.getStackTrace().toString());
                throw new Error("Couldn't get the outputstream in BluetoothListener for sending out command");
            }
        }
    }
}