package org.ousi.ousi;


import java.io.*;

public class FileManager {

    static public void networkDOTBytes(Network network) {

    }

    static public void networkCSVBytes(Network network) {

    }

    static byte[] networkBinaryBytes(Network network) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(network);
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Network loadNetworkDOT(InputStream inputStream) {
        return null;
    }

    static public Network loadNetworkCSV(InputStream inputStream) {
        return null;
    }

    static Network loadNetworkBinary(InputStream inputStream) {
        Network network = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            network = (Network) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return network;
    }
}
