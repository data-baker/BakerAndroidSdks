package com.baker.vpr.demo.audio;


import android.media.AudioFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConvertHelper {

    private static final int SAMPLE_RATE_INHZ = 44100;

    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    private static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private FileInputStream mInputStream;

    private FileOutputStream mOutStream;

    private final byte[] mBuffer = new byte[2048];


    public void pcmToWav(File inPcmFilePath, File outWavFilePath) {
        if (inPcmFilePath == null || outWavFilePath == null) return;

        try {
            mInputStream = new FileInputStream(inPcmFilePath);
            mOutStream = new FileOutputStream(outWavFilePath);

            //采样字节byte率
            long byteRate = SAMPLE_RATE_INHZ * CHANNEL_CONFIG * ENCODING_FORMAT / 8;

            long totalPcmSize = mInputStream.getChannel().size();
            //总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalWavSize = totalPcmSize + 36;
            WavHeader.WavHeader(mOutStream, totalPcmSize, totalWavSize, Config.SAMPLE_RATE_INHZ,
                    Config.CHANNEL_CONFIG, byteRate);

            int length = 0;
            while ((length = mInputStream.read(mBuffer)) > 0) {
                mOutStream.write(mBuffer, 0, length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (mInputStream!=null){
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mOutStream!=null){
                try {
                    mOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
