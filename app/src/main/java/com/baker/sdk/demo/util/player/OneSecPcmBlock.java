package com.baker.sdk.demo.util.player;

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
