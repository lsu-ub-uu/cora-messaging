/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.messaging.starter;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.MessagingFactory;
import se.uu.ub.cora.messaging.MessagingInitializationException;
import se.uu.ub.cora.messaging.spy.MessagingFactorySpy;
import se.uu.ub.cora.messaging.spy.log.LoggerFactorySpy;

public class MessagingStarterTest {
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "MessagingModuleStarterImp";
	List<MessagingFactory> messagingFactoryImplementations;
	private MessagingModuleStarterImp starter;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = LoggerFactorySpy.getInstance();
		loggerFactorySpy.resetLogs(testedClassName);
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		messagingFactoryImplementations = new ArrayList<>();
		starter = new MessagingModuleStarterImp();
	}

	@Test(expectedExceptions = MessagingInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for MessagingFactory")
	public void testNoFoundImplementationThrowException() throws Exception {
		starter.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);
	}

	@Test
	public void testErrorIsLoggedWhenNoImplementations() throws Exception {
		try {
			starter.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);
		} catch (Exception e) {

		}
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"No implementations found for MessagingFactory");
	}

	@Test(expectedExceptions = MessagingInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "More than one implementations found for MessagingFactory")
	public void testMoreThanOneImplementationThrowException() throws Exception {
		messagingFactoryImplementations.add(new MessagingFactorySpy());
		messagingFactoryImplementations.add(new MessagingFactorySpy());

		starter.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);
	}

	@Test
	public void testErrorIsLoggedWhenMoreThanOneImplementations() throws Exception {
		try {
			messagingFactoryImplementations.add(new MessagingFactorySpy());
			messagingFactoryImplementations.add(new MessagingFactorySpy());
			starter.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);
		} catch (Exception e) {

		}
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingFactorySpy found as implemetation for MessagingFactory");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"MessagingFactorySpy found as implemetation for MessagingFactory");
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"More than one implementations found for MessagingFactory");
	}

	@Test
	public void testForFoundImplementation() throws Exception {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		messagingFactoryImplementations.add(messagingFactorySpy);

		MessagingFactory choosenMessagingFactory = starter
				.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);

		assertEquals(choosenMessagingFactory, messagingFactorySpy);
	}

	@Test
	public void testLoggingNormalStartup() throws Exception {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		messagingFactoryImplementations.add(messagingFactorySpy);

		starter.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingFactorySpy found as implemetation for MessagingFactory");
		assertEquals(loggerFactorySpy.getNoOfInfoLogMessagesUsingClassName(testedClassName), 1);
	}

}
