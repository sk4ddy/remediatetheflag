/*
 *  
 * REMEDIATE THE FLAG
 * Copyright 2018 - Andrea Scaduto 
 * remediatetheflag@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.remediatetheflag.global.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class RandomGenerator {

	private static final Random RANDOM = new SecureRandom();
	private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789"; 
	
	public static String getNextSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	public static String getNextString(Integer length){
		char[] buf = new char[length];
		for (int idx = 0; idx < buf.length; ++idx) 
			buf[idx] = symbols.charAt(RANDOM.nextInt(symbols.length()));
		return new String(buf);
	}

}
