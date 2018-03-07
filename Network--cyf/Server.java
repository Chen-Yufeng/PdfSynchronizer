package com.ifchan.p2p.Netword;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Usage: setFile() and run().
 */

public class Server {
    public static final int PORT = 2018;
    private ServiceCallbacks mServiceCallbacks;
    private boolean isRunning = false;
    private ServerSocket serverSocket;
    private File file;
    private long fileLength = 0;
    private ArrayList<Long> transLength = new ArrayList<>();
    private ArrayList<Socket> sockets = new ArrayList<>();

    interface ServiceCallbacks {
        void executeEverySendingLoop(int clientIndex, long transLength);
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        mServiceCallbacks = serviceCallbacks;
    }

    public boolean setFile(File file) {
        if (file.exists()) {
            this.file = file;
        } else {
            return false;
        }
        return true;
    }

    public void start() {
        isRunning = true;
        new ConnectDetectThread().execute();
    }

    private void run() throws IOException {
        serverSocket = new ServerSocket(PORT);

        while (isRunning) {
            Socket socket = serverSocket.accept();
            sockets.add(socket);
            sendFile(socket);
        }
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

    //android need to run in other thread!
    public boolean sendFile(Socket clientSocket) throws IOException {
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
        int length = 0;
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

}
