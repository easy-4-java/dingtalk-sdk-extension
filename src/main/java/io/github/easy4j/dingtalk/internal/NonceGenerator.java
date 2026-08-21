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

/**
 * Utility class for generating random alphanumeric strings.
 * <p>Used to produce nonce strings required by DingTalk JSAPI signatures.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see JsapiSignatureGenerator#sign(String, String, long, String)
 */
public class NonceGenerator {

  /** Character set for random string generation (a-z, A-Z, 0-9). */
  private static final String RANDOM_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  /** Shared random number generator. */
  private static final java.util.Random RANDOM = new java.util.Random();

  /**
   * Generates a random 16-character alphanumeric string.
   *
   * @return a 16-character random string
   */
  public static String getRandomStr() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 16; i++) {
      sb.append(RANDOM_STR.charAt(RANDOM.nextInt(RANDOM_STR.length())));
    }
    return sb.toString();
  }

}
