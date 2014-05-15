/**
 * RONTECH CONFIDENTIAL
 * __________________
 *
 *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of SuZhou Rontech Co.,Ltd. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to SuZhou Rontech Co.,Ltd.
 * and its suppliers and may be covered by China and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from SuZhou Rontech Co.,Ltd..
 */
package com.meifannet.framework.utils;

import java.lang.reflect.Constructor;

/**
 * Utility class using reflection to operate class/function/variables.
 * 
 */
public class ROperations {
	/**
	 * return a new instance with reflection.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokePrivateConstructor(String type, T obj) {
		try {
			Class<?> clazz = Class.forName(type);
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			// set accessibility
			constructor.setAccessible(true);
			return (T) constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
