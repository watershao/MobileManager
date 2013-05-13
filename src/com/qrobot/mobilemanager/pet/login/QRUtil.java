package com.qrobot.mobilemanager.pet.login;

public class QRUtil {
	public static int byteArrayToInt(byte[] bb, int offset) {
		return (int) ((((bb[offset + 3] & 0xff) << 24)
				| ((bb[offset + 2] & 0xff) << 16)
				| ((bb[offset + 1] & 0xff) << 8) | ((bb[offset + 0] & 0xff) << 0)));
	}

	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[25];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < 25; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
}
