package com.example.texttile.data.model;

import java.io.Serializable;

public class JalaWithFileModel implements Serializable {

    private String jala_name;
    private String file_uri;
    private boolean isReady = false;
    private boolean isFirebaseURI;

    public JalaWithFileModel() {
    }

    public JalaWithFileModel(String jala_name, String file_uri, boolean isReady, boolean isFirebaseURI) {
        this.jala_name = jala_name;
        this.file_uri = file_uri;
        this.isReady = isReady;
        this.isFirebaseURI = isFirebaseURI;
    }

    public JalaWithFileModel(String jala_name, boolean isReady) {
        this.jala_name = jala_name;
        this.isReady = isReady;
    }

    public String getJala_name() {
        return jala_name;
    }

    public void setJala_name(String jala_name) {
        this.jala_name = jala_name;
    }

    public String getFile_uri() {
        return file_uri;
    }

    public void setFile_uri(String file_uri) {
        this.file_uri = file_uri;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isFirebaseURI() {
        return isFirebaseURI;
    }

    public void setFirebaseURI(boolean firebaseURI) {
        isFirebaseURI = firebaseURI;
    }

    @Override
    public String toString() {
        return "JalaWithFileModel{" +
                "jala_name='" + jala_name + '\'' +
                ", file_uri='" + file_uri + '\'' +
                ", isReady=" + isReady +
                ", isFirebaseURI=" + isFirebaseURI +
                '}';
    }
}
