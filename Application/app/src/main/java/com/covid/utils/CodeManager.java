package com.covid.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.SecureRandom;
import java.util.Calendar;

//Class
//Called to generate a random string with prefix NZ, 3 random chars, and the timestamp in milliseconds (c.13 digits long)
// "NZ-XXX-0000000000000"
//This class should be created and run once on install.
//Can be modified to generate more codes to intensify privacy preservation
public class CodeManager {

    // Generates a 13 character code based off of time in milliseconds
    public static long generateCode(){
        //Get time from calendar in milliseconds
        return Calendar.getInstance().getTimeInMillis();
    }

    // Converts a long number to a byte array
    public static byte[] longToByteArray(final long i) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(i);
        return buffer.array();
    }

    // Converts a byte array to a long number
    public static long getLongFromByteArray(byte[] array) {
        ByteBuffer b = ByteBuffer.wrap(array);
        return b.getLong();
    }
}
