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


package com.sangupta.jerry.security;

import java.security.Principal;

import org.junit.Assert;

import org.junit.Test;

/**
 * Unit tests for {@link SecurityContext}.
 *
 * @author sangupta
 *
 */
public class TestSecurityContext {

	@Test
	public void testSecurityContext() {
		Assert.assertNull(SecurityContext.getPrincipal());
		Assert.assertTrue(SecurityContext.isAnonymousUser());

		final Principal anonymous = new Principal() {

			@Override
			public String getName() {
				return "anonymous";
			}

		};
		SecurityContext.setupAnonymousUserAccount(anonymous);
		Assert.assertEquals(anonymous, SecurityContext.getPrincipal());
		Assert.assertTrue(SecurityContext.isAnonymousUser());
		SecurityContext.setContext(anonymous);
		Assert.assertTrue(SecurityContext.isAnonymousUser());

		final Principal user = new Principal() {

			@Override
			public String getName() {
				return "anonymous";
			}

		};
		SecurityContext.setContext(user);
		Assert.assertEquals(user, SecurityContext.getPrincipal());

		Assert.assertFalse(SecurityContext.isAnonymousUser());

		SecurityContext.clearPrincipal();
		Assert.assertTrue(SecurityContext.isAnonymousUser());
	}

}
