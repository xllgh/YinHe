package com.yinhe.hardware;

interface Hardware{
 int setAudioOutput( int port, int mode);

 int getAudioOutput(int port);

 int setBluerayHbr(int status);

 int setEnterSmartSuspend(int status);



}