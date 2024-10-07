package org.example;

public class Main {
    public static void main(String[] args) {

        try{
            Client.run();
        }
        catch(Exception e){
           e.printStackTrace();
        }
    }
}