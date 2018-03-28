package com.ifchan.pdftest3.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.ifchan.pdftest3.Utils.ObjectToBytesUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String TAG = "@ifchan";
    public static final int PORT = 2018;
    private ConnectCallback mConnectedCallback;
    private boolean hasConnected = false;
    private boolean isReceiving = false;
    private Socket mSocket;
    private long mFileLength = 0;
    private long mTransLength = 0;

    public interface ConnectCallback {
        /**
         * Called and tells whether the connection is successful.
         *
         * @param hasConnected
         */
        void connectResult(boolean hasConnected);

        /**
         * Called and tells whether the file has been fully received.
         *
         * @param isFileReceivedSuccessful
         */
        void receiveFileCompleted(boolean isFileReceivedSuccessful);

        /**
         * Execute when annotation received.
         *
         * @param annotation Annotation from server.
         */
        void onReceiveAnnotation(Object annotation);
    }

    /**
     * Set callbacks.
     *
     * @param connectedCallback
     */
    public void setConnectedCallback(ConnectCallback connectedCallback) {
        mConnectedCallback = connectedCallback;
    }

    /**
     * The Client will try to connect the server machine, the result of connection can be get by
     * callback(if has been set).
     *
     * @param ipAddress IP of server machine.
     */
    public void connect(String ipAddress) {
        new ConnectThread().execute(ipAddress);
    }

    /**
     * The Client will try to receive the special file, the result of if can be get by
     * callback(if has been set).
     *
     * @param destination you can use Environment.getExternalStorageDirectory().getPath() to get
     *                    a directory path (not a file path, file name will add automatically). Be
     *                    sure that it ends with a '/'.
     */
    public void startReceive(String destination) {
        new ReceiveFileThread().execute(destination);
    }

    /**
     * Have not tried, I guess it have 0 error.
     *
     * @return The length of file in bytes.
     */
    public long getFileLength() {
        return mFileLength;
    }

    /**
     * Have not tried either, I guess it have 0 error.
     *
     * @return The length of file received in bytes.
     */
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

    /**
     * Close the socket.
     */
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
            if (mConnectedCallback != null) {
                mConnectedCallback.connectResult(hasConnected);
            }
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
            if (mConnectedCallback != null) {
                mConnectedCallback.receiveFileCompleted(mTransLength == mFileLength);
            }
        }
    }

    private void receiveAnnotation() {
        if (hasConnected) {
            try {
                DataInputStream dataInputStream = new DataInputStream(mSocket.getInputStream());
                while (isReceiving && hasConnected) {
                    int length = dataInputStream.readInt();
                    if (length > 0) {
                        byte[] bytes = new byte[length];
                        dataInputStream.readFully(bytes, 0, bytes.length);
                        mConnectedCallback.onReceiveAnnotation(ObjectToBytesUtil
                                .deserialize(bytes));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "startReceiveAnnotation: Have not connected!");
        }
    }

    private class ReceivingAnnotationThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            receiveAnnotation();
            return null;
        }
    }

    public void startReceiveAnnotation() {
        isReceiving = true;
    }
}
