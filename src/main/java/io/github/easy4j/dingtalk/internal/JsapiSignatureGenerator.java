/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.dingtalk.internal;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import com.taobao.api.ApiException;

/**
 * Utility class for DingTalk JSAPI signature computation.
 * <p>Implements the SHA-1 based signature algorithm required by the
 * DingTalk JSAPI ticket verification flow.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see <a href="https://ding-doc.dingtalk.com/doc#/dev/uwa7vs">JSAPI signature documentation</a>
 */
public class JsapiSignatureGenerator {

	/**
	 * Computes a SHA-1 signature for the given JSAPI ticket, nonce, timestamp, and URL.
	 * <p>The signature plain text is formatted as:
	 * {@code jsapi_ticket=<ticket>&noncestr=<nonce>&timestamp=<ts>&url=<url>}</p>
	 *
	 * @param jsapiTicket  the JSAPI ticket
	 * @param nonceStr     the random nonce string
	 * @param timeStamp    the Unix timestamp in seconds
	 * @param url          the current page URL (without hash fragment)
	 * @return the hexadecimal SHA-1 signature string
	 * @throws ApiException if the SHA-1 algorithm or UTF-8 encoding is not available
	 */
	public static String sign(String jsapiTicket, String nonceStr, long timeStamp, String url) throws ApiException {
		String plain = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
				+ "&url=" + url;
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			sha1.reset();
			sha1.update(plain.getBytes("UTF-8"));
			return byteToHex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new ApiException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new ApiException(e.getMessage());
		}
	}

	/**
	 * Converts a byte array to a lowercase hexadecimal string.
	 *
	 * @param hash  the byte array to convert
	 * @return the hexadecimal string representation
	 */
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
