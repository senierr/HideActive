package com.hideactive.util;

import android.app.Application;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoConstants;

/**
 * Created by realuei on 2017/6/29.
 */

public class ZegoAppHelper {

    private static ZegoAppHelper sInstance = new ZegoAppHelper();

    private ZegoLiveRoom mZegoLiveRoom;

    private ZegoAppHelper() {
    }

    static public byte[] parseSignKeyFromString(String strSignKey) throws NumberFormatException {
        String[] keys = strSignKey.split(",");
        if (keys.length != 32) {
            throw new NumberFormatException("App Sign Key Illegal");
        }
        byte[] byteSignKey = new byte[32];
        for (int i = 0; i < 32; i++) {
            int data = Integer.valueOf(keys[i].trim().replace("0x", ""), 16);
            byteSignKey[i] = (byte) data;
        }
        return byteSignKey;
    }

    public static ZegoLiveRoom getLiveRoom() {
        if (sInstance.mZegoLiveRoom == null) {
            sInstance.mZegoLiveRoom = new ZegoLiveRoom();
        }
        return sInstance.mZegoLiveRoom;

    }

    public static void init(final Application application) {
        ZegoLiveRoom liveRoom = ZegoAppHelper.getLiveRoom();
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContext() {
            @Override
            public String getSoFullPath() {
                return null;
            }

            @Override
            public String getLogPath() {
                return null;
            }

            @Override
            public Application getAppContext() {
                return application;
            }
        });

        ZegoLiveRoom.requireHardwareEncoder(true);
        ZegoLiveRoom.requireHardwareDecoder(true);
        ZegoLiveRoom.setTestEnv(true);
        ZegoLiveRoom.setBusinessType(0);

        liveRoom.initSDK(189538440L, parseSignKeyFromString("0x84,0x04,0x0b,0xfd,0xd3,0x92,0x80,0xde," +
                "0xa3,0x51,0xd3,0x96,0x0c,0x45,0xa8,0x66,0x17,0xc4,0x43,0xb4,0xa7," +
                "0x3e,0x09,0x79,0x10,0x82,0x8e,0x5e,0xeb,0x45,0xce,0xef"));

        liveRoom.setLatencyMode(ZegoConstants.LatencyMode.Low3);
    }
}
