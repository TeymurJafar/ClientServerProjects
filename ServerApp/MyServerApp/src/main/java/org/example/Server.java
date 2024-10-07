package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void run() throws IOException {
        final int PORT = 6789;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for clients...");

            while (true) {
                Socket client = serverSocket.accept();
                String ip = client.getInetAddress().getHostAddress();
                int port = client.getPort();
                System.out.println("New Client connected with IP: " + ip + " and PORT: " + port);
                new Thread (new ClientRunnable(client)).start();
            }
        }
    }

    public static class ClientRunnable implements Runnable {
        private final Socket clientSocket;

        public ClientRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            String RECEIVED_DIR = "received_files";

            try (InputStream inputStream = clientSocket.getInputStream();
                 DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream))) {

                File receivedDir = new File(RECEIVED_DIR);
                if (!receivedDir.exists()) {
                    receivedDir.mkdir();
                }

                //get filename from client
                String fileName = dataInputStream.readUTF();

                // Create file in server
                File outputFile = new File(receivedDir, fileName);
                // check file for duplicate
                if (outputFile.exists()) { // if exists rename file with current time in millis
                    fileName = renameFile(fileName);
                    outputFile = new File(receivedDir, fileName);
                }
                // GET FILE CONTENT
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("File received successfully.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private static String renameFile(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String newFileName;
        String extension;

        if(dotIndex != -1) {
            newFileName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }
        else {
            newFileName = fileName;
            extension = "";
        }

        return newFileName + "_" + System.currentTimeMillis() + extension;
    }
}
