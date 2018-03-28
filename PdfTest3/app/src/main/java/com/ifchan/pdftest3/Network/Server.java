package com.ifchan.pdftest3.Network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Usage: setFile() and run().
 */

public class Server {
    public static final String TAG = "@ifchan";
    public static final int PORT = 2018;
    private ServiceCallbacks mServiceCallbacks;
    private boolean isRunning = false;
    private ServerSocket serverSocket;
    private File file;
    private long fileLength = 0;
    private ArrayList<Long> transLength = new ArrayList<>();
    private ArrayList<Socket> sockets = new ArrayList<>();

    interface ServiceCallbacks {
        /**
         * It can execute every while loop used to send file, and the buff size is 1024 bytes.
         * So it will execute quickly.
         *
         * @param clientIndex The index of client, start from 0.
         * @param transLength The length of file has been sent in bytes.
         */
        void executeEverySendingLoop(int clientIndex, long transLength);
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        mServiceCallbacks = serviceCallbacks;
    }

    public boolean setFile(File file) {
        if (file.exists()) {
            this.file = file;
            fileLength = file.length();
        } else {
            return false;
        }
        return true;
    }

    /**
     * Start detect connection, keep connection with clients. File sending will start after
     * connect successfully.
     */
    public void start() {
        isRunning = true;
        new ConnectDetectThread().execute();
    }


    public void stop() {
        isRunning = false;
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ConnectDetectThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void run() throws IOException {
        isRunning = true;
        serverSocket = new ServerSocket(PORT);

        while (isRunning) {
            Socket socket = serverSocket.accept();
            sockets.add(socket);
            new SendFileThread(socket);
        }
    }

    class SendFileThread extends Thread {
        private Socket socket;

        public SendFileThread(Socket socket) {
            this.socket = socket;
            start();
        }

        @Override

        public void run() {
            try {
                sendFile(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //android need to run in other thread!
    private boolean sendFile(Socket clientSocket) throws IOException {
        if (file == null) {
            return false;
        }
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());
        dataOutputStream.flush();

        //send file
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] sendBytes = new byte[1024];
        int clientIndex = transLength.size();
        int length;
        long memory = 0;
        while ((length = fileInputStream.read(sendBytes, 0, sendBytes.length)) > 0) {
            if (transLength.size() == clientIndex) {
                transLength.add((long) length);
            } else {
                transLength.set(clientIndex, transLength.get(clientIndex) + length);
            }
            dataOutputStream.write(sendBytes, 0, length);
            dataOutputStream.flush();
            if (mServiceCallbacks != null) {
                mServiceCallbacks.executeEverySendingLoop(clientIndex, transLength.get
                        (clientIndex));
            }
            if ((transLength.get(clientIndex) > memory + 1024 * 1024) || (transLength.get
                    (clientIndex) == fileLength)) {
                memory = transLength.get(clientIndex);
            }
        }

        if (dataOutputStream != null) {
            dataOutputStream.close();
        }
        if (fileInputStream != null) {
            fileInputStream.close();
        }

        return true;
    }

    /**
     *
     * @param annotation Please change import to PSPDFKIT annotation.
     */
    public void sendAnnotation(Object annotation) {
        if (isRunning) {
            for (Socket socket : sockets) {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket
                            .getOutputStream());
                    byte[] bytes = ObjectToBytesUtil.serialize(annotation);
                    dataOutputStream.writeInt(bytes.length);
                    dataOutputStream.write(bytes);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "sendAnnotation: Not running!");
        }
    }
}
