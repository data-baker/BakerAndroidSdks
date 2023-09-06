package com.baker.engrave.lib;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PcmFileUtil {

    private PcmFileUtil() {
    }

    private static class InnerClass {
        private static final PcmFileUtil INSTANCE = new PcmFileUtil();
    }

    public static PcmFileUtil getInstance() {
        return InnerClass.INSTANCE;
    }

    private FileOutputStream fout;

    public String getConvertResultFile(Context mContext, String fileName) {
        return mContext.getFilesDir().getAbsolutePath()+File.separator + fileName;
    }

    public void init(Context mContext, String fileName) {
        try {
            if (fout != null) {
                fout.close();
                fout = null;
            }
            String path = mContext.getFilesDir().getAbsolutePath()+File.separator + fileName;
            File file = new File(path);
            if (file.exists()) {
                boolean isDelete = file.delete();
                if (!isDelete) return;
            }
            if (file.getParentFile() != null && (!file.getParentFile().exists())) {
                boolean isMkdirs = file.getParentFile().mkdirs();
                if (!isMkdirs) return;
            }
            boolean isCreated = file.createNewFile();
            if (!isCreated) return;
            fout = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        try {
            if (fout != null) {
                fout.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (fout != null) {
            try {
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
