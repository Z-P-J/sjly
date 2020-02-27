package com.zpj.shouji.market.utils;

public final class Base64 {
    private static final int BASELENGTH = 255;
    private static final int EIGHTBIT = 8;
    private static final int FOURBYTE = 4;
    private static final int LOOKUPLENGTH = 64;
    private static final byte PAD = 61;
    private static final int SIGN = -128;
    private static final int SIXBIT = 6;
    private static final int SIXTEENBIT = 16;
    private static final int TWENTYFOURBITGROUP = 24;
    private static byte[] base64Alphabet = new byte[255];
    private static byte[] lookUpBase64Alphabet = new byte[64];

    static {
        int i = 0;
        for (int i2 = 0; i2 < 255; i2++) {
            base64Alphabet[i2] = -1;
        }
        for (int i3 = 90; i3 >= 65; i3--) {
            base64Alphabet[i3] = (byte) (i3 - 65);
        }
        for (int i4 = 122; i4 >= 97; i4--) {
            base64Alphabet[i4] = (byte) ((i4 - 97) + 26);
        }
        for (int i5 = 57; i5 >= 48; i5--) {
            base64Alphabet[i5] = (byte) ((i5 - 48) + 52);
        }
        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;
        for (int i6 = 0; i6 <= 25; i6++) {
            lookUpBase64Alphabet[i6] = (byte) (i6 + 65);
        }
        int i7 = 26;
        int i8 = 0;
        while (i7 <= 51) {
            lookUpBase64Alphabet[i7] = (byte) (i8 + 97);
            i7++;
            i8++;
        }
        int i9 = 52;
        while (i9 <= 61) {
            lookUpBase64Alphabet[i9] = (byte) (i + 48);
            i9++;
            i++;
        }
        lookUpBase64Alphabet[62] = 43;
        lookUpBase64Alphabet[63] = 47;
    }

    public Base64() {
    }

    public static byte[] decode(byte[] bArr) {
        if (bArr.length == 0) {
            return new byte[0];
        }
        int length = bArr.length / 4;
        int length2 = bArr.length;
        while (bArr[length2 - 1] == 61) {
            length2--;
            if (length2 == 0) {
                return new byte[0];
            }
        }
        byte[] bArr2 = new byte[(length2 - length)];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 4;
            byte b = bArr[i3 + 2];
            byte b2 = bArr[i3 + 3];
            byte b3 = base64Alphabet[bArr[i3]];
            byte b4 = base64Alphabet[bArr[i3 + 1]];
            if (b != 61 && b2 != 61) {
                byte b5 = base64Alphabet[b];
                byte b6 = base64Alphabet[b2];
                bArr2[i] = (byte) ((b3 << 2) | (b4 >> 4));
                bArr2[i + 1] = (byte) (((b4 & 15) << 4) | ((b5 >> 2) & 15));
                bArr2[i + 2] = (byte) ((b5 << 6) | b6);
            } else if (b == 61) {
                bArr2[i] = (byte) ((b4 >> 4) | (b3 << 2));
            } else if (b2 == 61) {
                byte b7 = base64Alphabet[b];
                bArr2[i] = (byte) ((b3 << 2) | (b4 >> 4));
                bArr2[i + 1] = (byte) (((b4 & 15) << 4) | ((b7 >> 2) & 15));
            }
            i += 3;
        }
        return bArr2;
    }

    public static byte[] encode(byte[] bArr) {
        int length = bArr.length * 8;
        int i = length % 24;
        int i2 = length / 24;
        byte[] bArr2 = i != 0 ? new byte[((i2 + 1) * 4)] : new byte[(i2 * 4)];
        int i3 = 0;
        while (i3 < i2) {
            int i4 = i3 * 3;
            byte b = bArr[i4];
            byte b2 = bArr[i4 + 1];
            byte b3 = bArr[i4 + 2];
            byte b4 = (byte) (b2 & 15);
            byte b5 = (byte) (b & 3);
            int i5 = i3 * 4;
            byte b6 = (b & Byte.MIN_VALUE) == 0 ? (byte) (b >> 2) : (byte) ((b >> 2) ^ 192);
            byte b7 = (b2 & Byte.MIN_VALUE) == 0 ? (byte) (b2 >> 4) : (byte) ((b2 >> 4) ^ 240);
            int i6 = (b3 & Byte.MIN_VALUE) == 0 ? b3 >> 6 : (b3 >> 6) ^ 252;
            bArr2[i5] = lookUpBase64Alphabet[b6];
            bArr2[i5 + 1] = lookUpBase64Alphabet[b7 | (b5 << 4)];
            bArr2[i5 + 2] = lookUpBase64Alphabet[((byte) i6) | (b4 << 2)];
            bArr2[i5 + 3] = lookUpBase64Alphabet[b3 & 63];
            i3++;
        }
        int i7 = i3 * 3;
        int i8 = i3 * 4;
        if (i == 8) {
            byte b8 = bArr[i7];
            byte b9 = (byte) (b8 & 3);
            bArr2[i8] = lookUpBase64Alphabet[(b8 & Byte.MIN_VALUE) == 0 ? (byte) (b8 >> 2) : (byte) ((b8 >> 2) ^ 192)];
            bArr2[i8 + 1] = lookUpBase64Alphabet[b9 << 4];
            bArr2[i8 + 2] = 61;
            bArr2[i8 + 3] = 61;
        } else if (i == 16) {
            byte b10 = bArr[i7];
            byte b11 = bArr[i7 + 1];
            byte b12 = (byte) (b11 & 15);
            byte b13 = (byte) (b10 & 3);
            byte b14 = (b10 & Byte.MIN_VALUE) == 0 ? (byte) (b10 >> 2) : (byte) ((b10 >> 2) ^ 192);
            byte b15 = (b11 & Byte.MIN_VALUE) == 0 ? (byte) (b11 >> 4) : (byte) ((b11 >> 4) ^ 240);
            bArr2[i8] = lookUpBase64Alphabet[b14];
            bArr2[i8 + 1] = lookUpBase64Alphabet[b15 | (b13 << 4)];
            bArr2[i8 + 2] = lookUpBase64Alphabet[b12 << 2];
            bArr2[i8 + 3] = 61;
        }
        return bArr2;
    }

    public static boolean isBase64(byte b) {
        return b == 61 || base64Alphabet[b] != -1;
    }

}

