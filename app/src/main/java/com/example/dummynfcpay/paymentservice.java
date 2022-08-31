package com.example.dummynfcpay;

import android.annotation.SuppressLint;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class paymentservice extends HostApduService {

    public static final byte[] select_ppse_array = {0, -92, 4, 0, 14, 50, 80, 65, 89, 46, 83, 89, 83, 46, 68, 68, 70, 48, 49, 0};
    public static final byte[] select_aid_array = {0, -92, 4, 0, 7, -96, 0, 0, 0, 3, 16, 16, 0};
    public static final byte[] read_rec_array = {0, -78, 1, 12, 0};
    public static final byte[] gpo_array = {Byte.MIN_VALUE, -88, 0, 0, 4, -125, 2, Byte.MIN_VALUE, 0, 0};
    public static final byte[] gen_ac_array = {};

    public static String select_aid_response = "6F64840E325041592E5359532E4444463031A552BF0C4661224F07A0000000041010500A5649534120444542495487010142034750555F5502555361204F07A00000009808405008555320444542495487010242034750555F550255539F38069F1D089F1A029000";

    private int state = 0;// state variable to ensure the proper flow of commands

    @Override
    public void onCreate() {
        super.onCreate();


        state = 0;
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @SuppressLint("WrongConstant")
    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        byte[] response = new byte[0];

        // LOG --------------
        String select_aid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            select_aid = byteArrayToHex(select_aid_array);
            Log.println(Log.INFO, "SELECT AID APDU", select_aid);
        }
        Log.println(Log.INFO, "RECEIVED APDU", byteArrayToHex(bytes));
        // LOG ----------------



        if (Arrays.equals(bytes, select_aid_array))
        {
            state = 1;
            response = select_aid_response.getBytes(StandardCharsets.UTF_8);
        }
        else if (state == 1 && Arrays.equals(bytes,gpo_array))
        {
            state = 2;
            response = ("770A820259809404010101019000").getBytes(StandardCharsets.UTF_8);
        }
        else if (state == 2 && Arrays.equals(bytes, read_rec_array))
        {
            state = 3;
            response = ("7020840E315041592E5359532E4444463031A5088801005F2D02656E9F69039F6A049000").getBytes(StandardCharsets.UTF_8);
        }
        else if (state == 3 && Arrays.equals(bytes, gen_ac_array))
        {
            state = 4;
            //response =
        }
        else
        {
            response = new byte[]{111, 0};
        }

        return response;
    }

    @Override
    public void onDeactivated(int i) {

    }
}
