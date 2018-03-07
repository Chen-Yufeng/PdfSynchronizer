package com.ifchan.p2p.Netword;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String TAG = "@vir Client";
    public static final int PORT = 2018;
    private ConnectCallback mConnectedCallback;
    private boolean hasConnected = false;
    private Socket mSocket;
    private long mFileLength = 0;
    private long mTransLength = 0;

    public interface ConnectCallback {
        void connectResult(boolean hasConnected);

        void receiveFileCompleted(boolean isFileReceivedSuccessful);
    }

    public void setConnectedCallback(ConnectCallback connectedCallback) {
        mConnectedCallback = connectedCallback;
    }

    public void connect(String ipAddress) {
        new ConnectThread().execute(ipAddress);
    }

    public void startReceive(String destination) {
        new ReceiveFileThread().execute(destination);
    }

    public long getFileLength() {
        return mFileLength;
    }

    public long getTransLength() {
        return mTransLength;
    }

    private void receiveFile(String destination) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(mSocket.getInputStream());
        String fileName = dataInputStream.readUTF();
        mFileLength = dataInputStream.readLong();
        File file = new File(destination + fileName);
        File path = new File(destination);
        if (!path.exists()) {
            path.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int read = -1;
        while ((read = dataInputStream.read(bytes)) != -1) {
            mTransLength += read;
            fileOutputStream.write(bytes, 0, read);
            fileOutputStream.flush();
        }

        //close
        dataInputStream.close();
        // TODO: 3/7/18 figure out why always true.
        if (fileOutputStream != null) {  //why always true?
            fileOutputStream.close();
        }
    }

    public void disConnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ConnectThread extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mSocket = new Socket(strings[0], PORT);
                hasConnected = true;
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Cannot connect!");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mConnectedCallback.connectResult(hasConnected);
        }
    }

    class ReceiveFileThread extends AsyncTask<String, Long, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                receiveFile(strings[0]);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + "Receive File error!");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mConnectedCallback.receiveFileCompleted(mTransLength == mFileLength);
        }
    }
}
