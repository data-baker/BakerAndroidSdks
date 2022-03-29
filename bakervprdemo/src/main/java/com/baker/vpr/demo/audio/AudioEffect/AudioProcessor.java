package com.baker.vpr.demo.audio.AudioEffect;

public class AudioProcessor {
    static {
        System.loadLibrary("audio-processor");
    }

    private final int mBufferSize;//缓冲区大小
    private final byte[] mOutBuffer;//输出缓冲区
    private final float[] mFloatInput;//临时缓冲区
    private final float[] mFloatOutput;

    public AudioProcessor(int bufferSize) {
        mBufferSize = bufferSize;
        mOutBuffer = new byte[mBufferSize];
        //两个字节对应一个 float
        mFloatInput = new float[mBufferSize / 2];
        mFloatOutput = new float[mBufferSize / 2];
    }

    /*
      ratio 1~2
     */
    public synchronized byte[] process(float ratio, byte[] input, int sampleRate) {
        process(ratio, input, mOutBuffer, mBufferSize, sampleRate, mFloatInput, mFloatOutput);
        return mOutBuffer;
    }

    private static native void process(float ratio, byte[] in, byte[] out,
                                       int size, int sampleRate, float[] floatInput, float[] floatOutput);
}
