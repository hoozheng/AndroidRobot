/*
 * Copyright (C) 2012 The CeHu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/***************************
 * - Python keywords - Author : mTester - Time : 2012.09.21
 ****************************/
public class ReadProperties {

	private static String[] key = null;
	private static Properties pro = new Properties();
	static {
		readFlie();
	}

	synchronized static public void readFlie() {

		int i = 0;
		if (key == null) {
			try {
				FileInputStream input = 
						new FileInputStream(System.getProperty("user.dir") + 
								File.separator + "keywords.properties");
				pro.load(input);
				input.close();
				// ��ȡ����������
				key = new String[60];
				Enumeration enu2 = pro.propertyNames();
				while (enu2.hasMoreElements()) {
					key[i] = (String) enu2.nextElement();
					i++;
				}

			} catch (Exception e) {
				System.err.println("Load \"keywords.properties\" failed!");
			}
		}
	}

	public static String[] getKeyName() {
		if (key == null)
			readFlie();
		return key;
	}

	public static String getKeyValue(String keyName) {
		String keyValue = pro.getProperty(keyName);
		return keyValue;

	}

}
