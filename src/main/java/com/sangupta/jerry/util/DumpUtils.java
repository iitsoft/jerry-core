/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012-2016, Sandeep Gupta
 *
 * http://sangupta.com/projects/jerry-core
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.sangupta.jerry.util;

import java.lang.reflect.Method;

/**
 * Utility classes to dump given objects as string including
 * all child objects.
 *
 * @author sangupta
 *
 */
public abstract class DumpUtils {

	/**
	 * Returns the string representation of the given object by invoking the
	 * <code>toString</code> method, if available, else returns a string
	 * representation as generated by {@link DumpUtils#forceDumpAsString(Object)}
	 * method.
	 *
	 * <b>It is not recommended to use this method in PRODUCTION environment, as
	 * reflection on object might slow execution down.</b>
	 *
	 * @param object the object to be dumped as string
	 *
	 * @return {@link String} representation of the object
	 */
	public static final String dumpAsString(Object object) {
		StringBuilder result = new StringBuilder();

		if (object == null) {
			result.append(StringUtils.BLANK_STRING);
		} else {
			result.append("(Param Class: ");
			result.append(object.getClass().getName());
			result.append("; Param Value: ");

			// check if object implements toString method, then use that
			// otherwise
			// dump using the string
			boolean foundMethod = false;
			Method[] methods = object.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals("toString")) {
					// the class declares a toString method, return invocation
					// of this.
					result.append(object.toString());
					foundMethod = true;
					break;
				}
			}
			if (!foundMethod) {
				// no declaring method was found - use reflection to dump the
				// object's content
				result.append(forceDumpAsString(object));
			}
		}

		result.append(" )");

		return result.toString();
	}

	/**
	 * Build an object dump as string by calling each and every no-argument
	 * getter on the object, and creating a list of all such attributes, which
	 * is then suffixed to the object's class name. The method can be used to
	 * generate <code>toString</code> methods for value-objects while
	 * development.
	 *
	 * <b>It is NOT recommended to use this method excessively in PRODUCTION
	 * environment, as it might slow down the execution.</b>
	 *
	 * @param object the object to be dumped
	 *
	 * @return the string representation
	 */
	public static final String forceDumpAsString(Object object) {
		if (object == null) {
			return StringUtils.BLANK_STRING;
		}

		StringBuilder result = new StringBuilder();

		Method[] methods = object.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
				String propertyNameWithoutFirstCharacter = method.getName().substring(4);
				String propertyNamefirstCharacter = method.getName().substring(3, 4).toLowerCase();
				String propertyName = propertyNamefirstCharacter + propertyNameWithoutFirstCharacter;

				Object propertyValue;

				try {
					propertyValue = method.invoke(object);
				} catch (Exception e) {
					propertyValue = "(EXCEPTION invoking getter: " + e.getMessage() + ")";
				}

				result.append("{");
				result.append(propertyName);
				result.append(": ");
				result.append(dumpAsString(propertyValue));
				result.append("}");
			}
		}

		return result.toString();
	}
}
