package com.ifchan.p2p.Netword;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


/**
 * Created by daily on 3/15/18.
 */

public class NetworkDetectorClient {
    public static final String TAG = "Cyf_Network";
    public static final String DETECT_IP = "224.0.2.123";
    public static final int DETECT_PORT = 6666;
    public static final int RECEIVED_BUFFER_LENGTH = 100;
    private static final int TIMEOUT = 5000;
    private MulticastSocket multicastSocket;
    private InetAddress multicastIP;
    private DatagramPacket datagramPacket;
    private ArrayList<ServerInfo> serverInfos = new ArrayList<>();
    private Callbacks mCallbacks;
    private boolean findFlag = false;

    public interface Callbacks {
        /**
         * The method will execute after <tt>stopFindServers()</tt> or time is out. Timeout is 5
         * seconds.
         *
         * @param serverArrayList An <tt>ArrayList</tt> contains the servers found.
         */
        void onServersFindingCompleteListener(ArrayList<ServerInfo> serverArrayList);
    }

    public NetworkDetectorClient(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    private void findServers() throws IOException {
        serverInfos.clear();
        multicastSocket = new MulticastSocket(DETECT_PORT);
        multicastIP = InetAddress.getByName(DETECT_IP);
        multicastSocket.joinGroup(multicastIP);
        multicastSocket.setSoTimeout(TIMEOUT);
        byte[] buffer = new byte[RECEIVED_BUFFER_LENGTH];
        datagramPacket = new DatagramPacket(buffer, RECEIVED_BUFFER_LENGTH);
        try {
            while (findFlag) {
                multicastSocket.receive(datagramPacket);
                //what is charsetName?
                String roomName = new String(datagramPacket.getData(), 0, datagramPacket
                        .getLength(),
                        "8859_1");
                if (!exist(serverInfos, roomName)) {
                    serverInfos.add(new ServerInfo(datagramPacket.getAddress().getHostAddress(),
                            roomName));
                }
            }
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "findServers: Time is out!");
            findFlag = false;
        }
        mCallbacks.onServersFindingCompleteListener(serverInfos);
    }

    private boolean exist(ArrayList<ServerInfo> serverInfos, String roomName) {
        for (ServerInfo serverInfo : serverInfos) {
            if (serverInfo.getRoomName().equals(roomName)) {
                return true;
            }
        }
        return false;
    }

    private class findTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            findFlag = true;
            try {
                findServers();
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: findServers() error!");
                e.printStackTrace();
            }
            return null;
        }
    }

    public void startFindServers() {
        if (!findFlag) {
            new findTask().execute();
        }
    }

    public void stopFindServers() {
        findFlag = false;
    }
}

