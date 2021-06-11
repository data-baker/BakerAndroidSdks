package com.baker.sdk.longtime.asr.bean;

/**
 * @author hsj55
 * 2020/9/25
 */
public class PcmBlock {
    private byte[] blockbytes;

    private int req_idx;

    public PcmBlock(byte[] blockbytes, int req_idx) {
        this.blockbytes = blockbytes;
        this.req_idx = req_idx;
    }

    public byte[] getBlockbytes() {
        return blockbytes;
    }

    public void setBlockbytes(byte[] blockbytes) {
        this.blockbytes = blockbytes;
    }

    public int getReq_idx() {
        return req_idx;
    }

    public void setReq_idx(int req_idx) {
        this.req_idx = req_idx;
    }
}
