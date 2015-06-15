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
 */
public class WHCSBluetoothListener implements Runnable {

    private BluetoothSocket socket;
    private CommandIssuer commandIssuer;
    WHCSResponse.WHCSResponseParser responseParser = new WHCSResponse.WHCSResponseParser();

    private WHCSBluetoothListener() {
        this.responseParser = new WHCSResponse.WHCSResponseParser();
    }

    public WHCSBluetoothListener(BluetoothSocket socket, CommandIssuer issuer) {
        this();
        this.socket = socket;
        this.commandIssuer = issuer;
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
        this.commandIssuer.handleResponse(this.responseParser.getResponseFromCompletedParser());
    }

    public void setCommandIssuer(CommandIssuer issuer) {
        this.commandIssuer = issuer;
    }
}