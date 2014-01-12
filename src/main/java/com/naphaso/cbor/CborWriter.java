package com.naphaso.cbor;

import com.naphaso.cbor.io.Output;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by wolong on 1/12/14.
 */
public class CborWriter {
    private Output output;

    public CborWriter(Output output) {
        this.output = output;
    }

    private void writeTypeAndValue(int majorType, int value) throws IOException {
        majorType <<= 5;
        if (value < 24) {
            output.write(majorType | value);
        } else if (value <= 255) {
            output.write(majorType | 24);
            output.write(value);
        } else if (value <= 65535) {
            output.write(majorType | 25);
            output.write(value >> 8);
            output.write(value & 0xFF);
        } else {
            output.write(majorType | 26);
            output.write((value >> 24) & 0xFF);
            output.write((value >> 16) & 0xFF);
            output.write((value >> 8) & 0xFF);
            output.write(value & 0xFF);
        }
    }

    protected void writeTypeAndValue(int majorType, long value) throws IOException {
        majorType <<= 5;
        if (value < 24L) {
            output.write(majorType | (int)value);
        } else if (value <= 255L) {
            output.write(majorType | 24);
            output.write((int)value);
        } else if (value <= 65535L) {
            output.write(majorType | 25);
            output.write((int)value >> 8);
            output.write((int)value & 0xFF);
        } else if (value <= 4294967295L) {
            output.write(majorType | 26);
            output.write((int)(value >> 24) & 0xFF);
            output.write((int)(value >> 16) & 0xFF);
            output.write((int)(value >> 8) & 0xFF);
            output.write((int) value & 0xFF);
        } else {
            output.write(majorType | 27);
            output.write((int)(value >> 56) & 0xFF);
            output.write((int)(value >> 48) & 0xFF);
            output.write((int)(value >> 40) & 0xFF);
            output.write((int)(value >> 32) & 0xFF);
            output.write((int)(value >> 24) & 0xFF);
            output.write((int)(value >> 16) & 0xFF);
            output.write((int)(value >> 8) & 0xFF);
            output.write((int)value & 0xFF);
        }
    }

    protected void writeTypeAndValue(int majorType, BigInteger value) throws IOException {
        majorType <<= 5;
        if (value.compareTo(BigInteger.valueOf(24)) == -1) {
            output.write(majorType | value.intValue());
        } else if (value.compareTo(BigInteger.valueOf(256)) == -1) {
            output.write(majorType | 24);
            output.write(value.intValue());
        } else if (value.compareTo(BigInteger.valueOf(65536L)) == -1) {
            output.write(majorType | 25);
            int twoByteValue = value.intValue();
            output.write(twoByteValue >> 8);
            output.write(twoByteValue & 0xFF);
        } else if (value.compareTo(BigInteger.valueOf(4294967296L)) == -1) {
            output.write(majorType | 26);
            long fourByteValue = value.longValue();
            output.write((int) ((fourByteValue >> 24) & 0xFF));
            output.write((int) ((fourByteValue >> 16) & 0xFF));
            output.write((int) ((fourByteValue >> 8) & 0xFF));
            output.write((int) (fourByteValue & 0xFF));
        } else if (value.compareTo(new BigInteger("18446744073709551616")) == -1) {
            output.write(majorType | 27);
            BigInteger mask = BigInteger.valueOf(0xFF);
            output.write(value.shiftRight(56).and(mask).intValue());
            output.write(value.shiftRight(48).and(mask).intValue());
            output.write(value.shiftRight(40).and(mask).intValue());
            output.write(value.shiftRight(32).and(mask).intValue());
            output.write(value.shiftRight(24).and(mask).intValue());
            output.write(value.shiftRight(16).and(mask).intValue());
            output.write(value.shiftRight(8).and(mask).intValue());
            output.write(value.and(mask).intValue());
        } else {
            /*
            if (majorType == MAJOR_TYPE_NEGATIVE_INTEGER) {
                writeTypeAndValue(MAJOR_TYPE_TAG, 3);
            } else {
                writeTypeAndValue(MAJOR_TYPE_TAG, 4);
            }*/
            //encoder.encode(new ByteString(length.toByteArray()));
        }
    }

    // int

    public void writeInt(int value) throws IOException {
        if(value < 0) {
            writeTypeAndValue(1, -value);
        } else {
            writeTypeAndValue(0, value);
        }
    }

    public void writeInt(long value) throws IOException {
        if(value < 0) {
            writeTypeAndValue(1, -value);
        } else {
            writeTypeAndValue(0, value);
        }
    }

    public void writeInt(BigInteger value) throws IOException {
        if(value.signum() == -1) {
            writeTypeAndValue(1, value.negate());
        } else {
            writeTypeAndValue(0, value);
        }
    }

    // bytes

    public void writeBytes(byte[] bytes) throws IOException {
        writeTypeAndValue(2, bytes.length);
        output.write(bytes, 0, bytes.length);
    }

    // string

    public void writeString(String str) throws IOException {
        byte[] bytes = str.getBytes();
        writeTypeAndValue(3, bytes.length);
        output.write(bytes, 0, bytes.length);
    }

    // array

    public void writeArray(int size) throws IOException {
        writeTypeAndValue(4, size);
    }

    public void writeArray(long size) throws IOException {
        writeTypeAndValue(4, size);
    }

    public void writeArray(BigInteger size) throws IOException {
        writeTypeAndValue(4, size);
    }

    // map

    public void writeMap(int size) throws IOException {
        writeTypeAndValue(5, size);
    }

    public void writeMap(long size) throws IOException {
        writeTypeAndValue(5, size);
    }

    public void writeMap(BigInteger size) throws IOException {
        writeTypeAndValue(5, size);
    }

    // tag

    public void writeTag(int tag) throws IOException {
        writeTypeAndValue(6, tag);
    }

    public void writeTag(long tag) throws IOException {
        writeTypeAndValue(6, tag);
    }

    public void writeTag(BigInteger tag) throws IOException {
        writeTypeAndValue(6, tag);
    }

    // special

    public void writeSpecial(int code) throws IOException {
        writeTypeAndValue(6, code);
    }

    public void writeSpecial(long code) throws IOException {
        writeTypeAndValue(6, code);
    }

    public void writeSpecial(BigInteger code) throws IOException {
        writeTypeAndValue(6, code);
    }
}