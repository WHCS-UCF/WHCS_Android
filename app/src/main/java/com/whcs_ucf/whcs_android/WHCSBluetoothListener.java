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
            ListenerThread = new Thread(SingletonWHCSBluetoothListener);
            ListenerThread.start();
        }
        return SingletonWHCSBluetoothListener;
    }

    public static WHCSBluetoothListener GetSingletonBluetoothListener(CommandIssuer responseHandler) {
        if(SingletonWHCSBluetoothListener == null) {
            SingletonWHCSBluetoothListener = new WHCSBluetoothListener(responseHandler);
            new Thread(SingletonWHCSBluetoothListener).start();
        }
        return SingletonWHCSBluetoothListener;
    }

    public static WHCSBluetoothListener GetSingletonBluetoothListener(BluetoothDevice btDevice, CommandIssuer responseHandler) throws IOException {
        if(SingletonWHCSBluetoothListener == null) {
            SingletonWHCSBluetoothListener = new WHCSBluetoothListener(responseHandler);
            SingletonWHCSBluetoothListener.setupBluetoothSocketFromDevice(btDevice);
            try {
                SingletonWHCSBluetoothListener.socket.connect();
            }
            catch(IOException e) {
                SingletonWHCSBluetoothListener = null;
                throw e;
            }
            new Thread(SingletonWHCSBluetoothListener).start();
        }
        return SingletonWHCSBluetoothListener;
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
        this.shouldStop = true;
        this.closeSocket();
        SingletonWHCSBluetoothListener = null;
    }

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