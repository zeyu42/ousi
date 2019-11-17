package org.ousi.ousi;

import java.io.*;

public class Settings {
    private boolean useDegreeThreshold = false;
    private int degreeThreshold = 100;
    private File directory = new File("settings/");
    private File file = new File("settings/config.dat");

    public void writeSettings() {
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            file.createNewFile();
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file, false));
            dataOutputStream.writeBoolean(useDegreeThreshold);
            dataOutputStream.writeInt(degreeThreshold);
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Settings() {
        if (file.exists()) {
            // Try loading settings from this file
            try {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
                boolean useDegreeThreshold = dataInputStream.readBoolean();
                int degreeThreshold = dataInputStream.readInt();
                dataInputStream.close();
                this.useDegreeThreshold = useDegreeThreshold;
                this.degreeThreshold = degreeThreshold;
            } catch (Exception e) {
                // Can't load this file or this is not a valid config file
                // Do the same thing as below
                writeSettings();
            }
        } else {
            // Create this file with default settings
            writeSettings();
        }
    }

    public boolean getUseDegreeThreshold() {
        return useDegreeThreshold;
    }

    public int getDegreeThreshold() {
        return degreeThreshold;
    }

    public void setUseDegreeThreshold(boolean useDegreeThreshold) {
        this.useDegreeThreshold = useDegreeThreshold;
    }

    public void setDegreeThreshold(int degreeThreshold) {
        this.degreeThreshold = degreeThreshold;
    }
}
