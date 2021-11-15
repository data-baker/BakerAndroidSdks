package com.baker.vpr.demo.comm;

/**
 * @author xujian
 * @date 2021/11/14
 */
public interface RecordStreamListener {
    public void recordOfByte(byte[] audiodata, int i, int length);
}
