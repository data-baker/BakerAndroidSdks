package com.baker.vpr.demo.audio;

import java.io.FileOutputStream;
import java.io.IOException;

public class WavHeader {
    /**
     * PCM文件转WAV文件
     *
     * @param totalPcmSize
     * @param totalWavSize
     * @param sampleRate 采样率，例如15000
     * @param channels   声道数 单声道：1或双声道：2
     * @param byteRate   采样字节byte率
     */
    public static void WavHeader(FileOutputStream os, long totalPcmSize, long totalWavSize, int sampleRate, int channels, long byteRate) throws IOException {

        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalWavSize & 0xff);//数据大小
        header[5] = (byte) ((totalWavSize >> 8) & 0xff);
        header[6] = (byte) ((totalWavSize >> 16) & 0xff);
        header[7] = (byte) ((totalWavSize >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalPcmSize & 0xff);
        header[41] = (byte) ((totalPcmSize >> 8) & 0xff);
        header[42] = (byte) ((totalPcmSize >> 16) & 0xff);
        header[43] = (byte) ((totalPcmSize >> 24) & 0xff);
        os.write(header, 0, 44);

    }
}
