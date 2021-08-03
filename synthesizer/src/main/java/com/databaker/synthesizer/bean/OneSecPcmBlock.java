package com.databaker.synthesizer.bean;

/**
 * Create by hsj55
 * 2019/12/15
 */
public class OneSecPcmBlock {

    private byte[] blockbytes;

    public OneSecPcmBlock(byte[] bytes) {
        this.blockbytes = bytes;
    }

    public byte[] getBlockbytes() {
        return blockbytes;
    }

    public void setBlockbytes(byte[] bytes) {
        this.blockbytes = bytes;
    }
}
