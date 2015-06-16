package com.whcs_ucf.whcs_android;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

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

    private BluetoothSocket socket;
    private ResponseHandler responseHandler;
    WHCSResponse.WHCSResponseParser responseParser = new WHCSResponse.WHCSResponseParser();

    private WHCSBluetoothListener() {
        this.responseParser = new WHCSResponse.WHCSResponseParser();
    }

    public WHCSBluetoothListener(BluetoothSocket socket, ResponseHandler responseHandler) {
        this();
        this.socket = socket;
        this.responseHandler = responseHandler;
    }

    public WHCSBluetoothListener(CommandIssuer responseHandler) {
        this();
        this.responseHandler = responseHandler;
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
                while(!this.responseParser.hasCompletelyParsed()) {
                    bytesRead = inStream.read(buffer);
                    if (bytesRead != -1) {
                        this.responseParser.addData(buffer, bytesRead);
                    }
                }
                this.handoffResponseToIssuer();

                socket.getInputStream();
            }
        } catch (IOException e) {
            Log.d("BLUETOOTH_COMMS", e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    private void handoffResponseToIssuer() {
        this.responseHandler.handleResponse(this.responseParser.getResponseFromCompletedParser());
    }

    public void performDebugResponseRead() {
        int bytesRead = 4;
        byte bytes[] = new byte[] {0x00, WHCSOpCodes.GET_STATUS_OF_BASE_STATION, 0x00, 0x02};
        this.responseParser.addData(bytes, bytesRead);
        if(!this.responseParser.hasCompletelyParsed()) {
            throw new Error("The byte array should be representing a completed WHCS packet.");
        }
        this.handoffResponseToIssuer();
        this.responseParser.reset();
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void sendOutCommand(WHCSCommand command) {
        if(DebugFlags.DEBUG_BLUETOOTH_COMM_PIPELINE) {
            performDebugResponseRead();
            return;
        }
    }
}