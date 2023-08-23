package com.baker.engrave.lib.callback;

public interface PlayListener {
    void playStart();

    void playEnd();

    void playError(Exception e);
}