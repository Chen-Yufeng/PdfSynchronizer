package com.ifchan.p2p.Netword;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by daily on 3/15/18.
 */

public class NetworkDetectorServer {
    public static final String TAG = "Cyf_Network";
    public static final String DETECT_IP = "224.0.2.123";
    public static final int DETECT_PORT = 6666;
    private MulticastSocket multicastSocket;
    private DatagramPacket datagramPacket;
    private boolean sendFlag = false;

    private void sendInfo(String roomName) throws IOException, InterruptedException {
//        InetAddress local = InetAddress.getLocalHost();
//        byte[] serverAddress = local.getHostAddress().getBytes("UTF-8");
        byte[] serverName = roomName.getBytes("UTF-8");
//        byte[] serverInfo = new byte[15 + serverName.length];
//        System.arraycopy(serverAddress, 0, serverInfo, 0, serverAddress.length);
//        System.arraycopy(serverName, 0, serverInfo, 15, serverName.length);
//        String localIP = local.getHostAddress().toString();
        InetAddress multicast = InetAddress.getByName(DETECT_IP);
        datagramPacket = new DatagramPacket(serverName, serverName.length, multicast, DETECT_PORT);
        multicastSocket = new MulticastSocket();
        while (sendFlag) {
            multicastSocket.send(datagramPacket);
            Thread.sleep(1000);
        }
    }

    private class sendTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                sendFlag = true;
                sendInfo(strings[0]);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + "sendInfo() error");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     *
     * @param roomName string length should no more than 100 bytes.
     */
    public void startSendInfo(String roomName) {
        new sendTask().execute(roomName);
    }

    public void stopSendInfo() {
        sendFlag = false;
    }
}

