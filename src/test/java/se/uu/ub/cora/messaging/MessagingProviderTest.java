/*
 * Copyright 2019, 2025 Uppsala University Library
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

package se.uu.ub.cora.messaging;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ServiceLoader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.spy.MessagingFactorySpy;
import se.uu.ub.cora.messaging.spy.log.LoggerFactorySpy;
import se.uu.ub.cora.messaging.starter.MessagingModuleStarter;
import se.uu.ub.cora.messaging.starter.MessagingModuleStarterImp;
import se.uu.ub.cora.messaging.starter.MessagingModuleStarterSpy;

public class MessagingProviderTest {
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "MessagingProvider";
	private MessagingModuleStarter defaultStarter;
	private AmqpMessageListenerRoutingInfo amqpRoutingInfo;

	@BeforeTest
	public void beforeTest() {
		loggerFactorySpy = LoggerFactorySpy.getInstance();
		loggerFactorySpy.resetLogs(testedClassName);
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		defaultStarter = MessagingProvider.getStarter();
	}

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = LoggerFactorySpy.getInstance();
		loggerFactorySpy.resetLogs(testedClassName);
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		MessagingProvider.setMessagingFactory(null);

		amqpRoutingInfo = new AmqpMessageListenerRoutingInfo("tcp://dev-diva-drafts", 61617,
				"alvin", "someQueue");
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<MessagingProvider> constructor = MessagingProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));

	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<MessagingProvider> constructor = MessagingProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testSetMessagingFactoryForUsingInAnotherTest() {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		MessageSender messageSender = MessagingProvider.getTopicMessageSender(amqpRoutingInfo);

		assertEquals(messagingFactorySpy.messagingRoutingInfo, amqpRoutingInfo);
		assertEquals(messageSender, messagingFactorySpy.messageSender);
	}

	@Test
	public void testStartingOfProviderFactoryCanOnlyBeDoneByOneThreadAtATime() throws Exception {
		Method declaredMethod = MessagingProvider.class
				.getDeclaredMethod("ensureMessagingFactoryIsSet");
		assertTrue(Modifier.isSynchronized(declaredMethod.getModifiers()));
	}

	@Test
	public void testNonExceptionThrowingStartupForTopicMessageSender() {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageSender(amqpRoutingInfo);
		assertTrue(starter.startWasCalled);
	}

	private MessagingModuleStarterSpy startAndSetMessagingModuleStarterSpy() {
		MessagingModuleStarter starter = new MessagingModuleStarterSpy();
		MessagingProvider.setStarter(starter);
		return (MessagingModuleStarterSpy) starter;
	}

	@Test
	public void testLoggingRecordStorageStartedByOtherProviderForTopicMessageSender() {
		startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageSender(amqpRoutingInfo);

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingProvider starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"MessagingProvider started");
	}

	@Test
	public void testInitUsesDefaultMessagingModuleStarter() {
		MessagingProvider.setStarter(defaultStarter);
		assertStarterIsMessagingModuleStarter(defaultStarter);
		makeSureErrorIsThrownAsNoImplementationsExistInThisModule();
	}

	private void assertStarterIsMessagingModuleStarter(MessagingModuleStarter starter) {
		assertTrue(starter instanceof MessagingModuleStarterImp);
	}

	private void makeSureErrorIsThrownAsNoImplementationsExistInThisModule() {
		Exception caughtException = null;
		try {

			MessagingProvider.getTopicMessageSender(amqpRoutingInfo);
		} catch (Exception e) {
			caughtException = e;
		}
		assertTrue(caughtException instanceof MessagingInitializationException);
		assertEquals(caughtException.getMessage(), "No implementations found for MessagingFactory");
	}

	@Test
	public void testMessagingFactoryImplementationsArePassedOnToStarter() {

		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageSender(amqpRoutingInfo);

		Iterable<MessagingFactory> iterable = starter.messagingFactoryImplementations;
		assertTrue(iterable instanceof ServiceLoader);

	}

	@Test
	public void testCallEmptyTopicListener() {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(null);

		Iterable<MessagingFactory> iterable = starter.messagingFactoryImplementations;
		assertTrue(iterable instanceof ServiceLoader);

	}

	@Test
	public void testReturnObjectForTopicMessageListnerIsMessageListener() {
		assertTrue(MessagingProvider.getTopicMessageListener(null) instanceof MessageListener);
	}

	@Test
	public void testTopicMessageListenerUsesRoutingInfo() {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		MessageListener messageListener = MessagingProvider
				.getTopicMessageListener(amqpRoutingInfo);

		assertEquals(messagingFactorySpy.messagingRoutingInfo, amqpRoutingInfo);
		assertEquals(messageListener, messagingFactorySpy.messageListener);
	}

	@Test
	public void testNonExceptionThrowingStartupForTopicMessageListener() {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(amqpRoutingInfo);
		assertTrue(starter.startWasCalled);
	}

	@Test
	public void testLoggingRecordStorageStartedByOtherProviderForTopicMessageListener() {
		startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(amqpRoutingInfo);

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingProvider starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"MessagingProvider started");
	}

	@Test
	public void testTopicMessageListenerUsesJmsRoutingInfo() {

		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		String hostname = "tcp://dev-diva-drafts";
		int port = 61617;
		String routingKey = "fedora.apim.*";
		String username = "admin";
		String password = "admin";

		JmsMessageRoutingInfo jmsRoutingInfo = new JmsMessageRoutingInfo(hostname, port, routingKey,
				username, password);

		MessagingProvider.getTopicMessageListener(jmsRoutingInfo);

		assertTrue(messagingFactorySpy.messagingRoutingInfo instanceof JmsMessageRoutingInfo);

		JmsMessageRoutingInfo storedJmsRoutingInfo = (JmsMessageRoutingInfo) messagingFactorySpy.messagingRoutingInfo;

		assertEquals(storedJmsRoutingInfo.hostname, jmsRoutingInfo.hostname);
		assertEquals(storedJmsRoutingInfo.port, jmsRoutingInfo.port);
		assertEquals(storedJmsRoutingInfo.routingKey, jmsRoutingInfo.routingKey);
		assertEquals(storedJmsRoutingInfo.username, jmsRoutingInfo.username);
		assertEquals(storedJmsRoutingInfo.password, jmsRoutingInfo.password);

	}

	@Test
	public void testTopicMessageListenerUsesAmqpRoutingInfo() {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		MessagingProvider.getTopicMessageListener(amqpRoutingInfo);

		assertTrue(
				messagingFactorySpy.messagingRoutingInfo instanceof AmqpMessageListenerRoutingInfo);

		AmqpMessageListenerRoutingInfo storedRoutingInfo = (AmqpMessageListenerRoutingInfo) messagingFactorySpy.messagingRoutingInfo;

		assertEquals(storedRoutingInfo.hostname, amqpRoutingInfo.hostname);
		assertEquals(storedRoutingInfo.port, amqpRoutingInfo.port);
		assertEquals(storedRoutingInfo.virtualHost, amqpRoutingInfo.virtualHost);
		assertEquals(storedRoutingInfo.queueName, amqpRoutingInfo.queueName);
	}

	@Test
	public void testGetMessagingProviderUniqueId() {
		String uniqueId = MessagingProvider.getMessagingId();

		assertNotNull(uniqueId);
	}

	@Test
	public void testGetMessagingProviderUniqueIdSameForMultipleCalls() {
		String uniqueId = MessagingProvider.getMessagingId();
		String uniqueId2 = MessagingProvider.getMessagingId();

		assertSame(uniqueId, uniqueId2);
	}

}
