package com.bestbuy.sdp.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.UUID;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Decoder {
	/**
	 * decodes encrypted base64 UUID string
	 *
	 * @param arg encrypted base64 UUID string
	 * @return UUID string
	 * @throws IOException
	 */
	public static String decode(String arg) throws IOException {
		byte[] bytes = new BASE64Decoder().decodeBuffer(arg);

		bytes = reverseBytes(bytes);

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		LongBuffer longBuffer = byteBuffer.asLongBuffer();

		String val = new UUID(longBuffer.get(0), longBuffer.get(1)).toString();

		return val.replaceAll("-", "");
	}


	/**
	 * encodes UUID string to encrypted base64 string
	 *
	 * @param arg UUID string
	 * @return encrypted base64 string
	 * @throws IOException
	 */
	public static String encode(String arg) throws IOException {
		UUID uuid = UUID.fromString(arg);

		byte[] bytes = new byte[16];

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		LongBuffer longBuffer = byteBuffer.asLongBuffer();
		longBuffer.put(new long[] { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() });

		return new BASE64Encoder().encode(reverseBytes(bytes));
	}

	private static byte[] reverseBytes(byte[] bytes) {
		// temp array to reverse order of bytes in three sections as follows:
		// (0123 45 67) to (3210 54 76)
		byte[] tmp = new byte[8];
		tmp[0] = bytes[3];
		tmp[1] = bytes[2];
		tmp[2] = bytes[1];
		tmp[3] = bytes[0];
		tmp[4] = bytes[5];
		tmp[5] = bytes[4];
		tmp[6] = bytes[7];
		tmp[7] = bytes[6];
		// copy back
		bytes[0] = tmp[0];
		bytes[1] = tmp[1];
		bytes[2] = tmp[2];
		bytes[3] = tmp[3];
		bytes[4] = tmp[4];
		bytes[5] = tmp[5];
		bytes[6] = tmp[6];
		bytes[7] = tmp[7];
		return bytes;
	}

	
}
