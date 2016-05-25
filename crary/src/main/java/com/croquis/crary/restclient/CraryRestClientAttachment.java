package com.croquis.crary.restclient;

public class CraryRestClientAttachment {
    String mName;
    byte[] mData;
    String mMimeType;
    String mFileName;

    private CraryRestClientAttachment() {
    }

    public static CraryRestClientAttachment byteArray(String name, byte[] data, String mimeType, String filename) {
        CraryRestClientAttachment attachment = new CraryRestClientAttachment();
        attachment.mName = name;
        attachment.mData = data;
        attachment.mMimeType = mimeType;
        attachment.mFileName = filename;
        return attachment;
    }
}
