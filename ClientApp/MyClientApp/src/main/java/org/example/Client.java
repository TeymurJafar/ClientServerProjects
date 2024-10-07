package org.example;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {

    public static void run(){
        final String HOST_ADDRESS = "localhost";
        final int PORT = 6789;

        try(
                Socket socket = new Socket(HOST_ADDRESS, PORT);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                Scanner scanner = new Scanner(System.in)){

            System.out.println("Enter filename ...");
            String filePath = scanner.nextLine().replace("\\","/");

            File file =   new File(filePath);
            if(!file.exists()){
                System.out.println("File does not exist");
                return;
            }
            String fileName = file.getName();
            dataOutputStream.writeUTF(fileName);
            try(BufferedInputStream bufferedInputStream= new BufferedInputStream(Files.newInputStream(file.toPath()))){

                byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = bufferedInputStream.read(buffer)) != -1){
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
