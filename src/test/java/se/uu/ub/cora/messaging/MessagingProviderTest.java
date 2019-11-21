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

package se.uu.ub.cora.messaging;

import static org.testng.Assert.assertEquals;
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
	private MessageRoutingInfo messagingRoutingInfo;
	private MessagingModuleStarter defaultStarter;

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
		messagingRoutingInfo = new MessageRoutingInfo("messaging.alvin-portal.org", "5672",
				"alvin");
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
	public void testSetMessagingFactoryForUsingInAnotherTest() throws Exception {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		MessageSender messageSender = MessagingProvider.getTopicMessageSender(messagingRoutingInfo);

		assertEquals(messagingFactorySpy.messagingRoutingInfo, messagingRoutingInfo);
		assertEquals(messageSender, messagingFactorySpy.messageSender);
	}

	@Test
	public void testStartingOfProviderFactoryCanOnlyBeDoneByOneThreadAtATime() throws Exception {
		Method declaredMethod = MessagingProvider.class
				.getDeclaredMethod("ensureMessagingFactoryIsSet");
		assertTrue(Modifier.isSynchronized(declaredMethod.getModifiers()));
	}

	@Test
	public void testNonExceptionThrowingStartupForTopicMessageSender() throws Exception {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageSender(messagingRoutingInfo);
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

		MessagingProvider.getTopicMessageSender(messagingRoutingInfo);

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingProvider starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"MessagingProvider started");
	}

	@Test
	public void testInitUsesDefaultMessagingModuleStarter() throws Exception {
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

			MessagingProvider.getTopicMessageSender(messagingRoutingInfo);
		} catch (Exception e) {
			caughtException = e;
		}
		assertTrue(caughtException instanceof MessagingInitializationException);
		assertEquals(caughtException.getMessage(), "No implementations found for MessagingFactory");
	}

	@Test
	public void testMessagingFactoryImplementationsArePassedOnToStarter() throws Exception {

		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageSender(messagingRoutingInfo);

		Iterable<MessagingFactory> iterable = starter.messagingFactoryImplementations;
		assertTrue(iterable instanceof ServiceLoader);

	}

	@Test
	public void testCallEmptyTopicListener() throws Exception {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(null);

		Iterable<MessagingFactory> iterable = starter.messagingFactoryImplementations;
		assertTrue(iterable instanceof ServiceLoader);

	}

	@Test
	public void testReturnObjectForTopicMessageListnerIsMessageListener() throws Exception {
		assertTrue(MessagingProvider.getTopicMessageListener(null) instanceof MessageListener);
	}

	@Test
	public void testTopicMessageListenerUsesRoutingInfo() throws Exception {
		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		MessageListener messageListener = MessagingProvider
				.getTopicMessageListener(messagingRoutingInfo);

		assertEquals(messagingFactorySpy.messagingRoutingInfo, messagingRoutingInfo);
		assertEquals(messageListener, messagingFactorySpy.messageListener);
	}

	@Test
	public void testNonExceptionThrowingStartupForTopicMessageListener() throws Exception {
		MessagingModuleStarterSpy starter = startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(messagingRoutingInfo);
		assertTrue(starter.startWasCalled);
	}

	@Test
	public void testLoggingRecordStorageStartedByOtherProviderForTopicMessageListener() {
		startAndSetMessagingModuleStarterSpy();

		MessagingProvider.getTopicMessageListener(messagingRoutingInfo);

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"MessagingProvider starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"MessagingProvider started");
	}

	@Test
	public void testTopicMessageListenerUsesJmsRoutingInfo() throws Exception {

		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		String hostname = "tcp://dev-diva-drafts";
		String port = "61617";
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
	public void testTopicMessageListenerUsesAmqpRoutingInfo() throws Exception {

		MessagingFactorySpy messagingFactorySpy = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactorySpy);

		String hostname = "tcp://dev-diva-drafts";
		String port = "61617";
		String routingKey = "fedora.apim.*";
		String virtualHost = "alvin";
		String exchange = "index";

		AmqpMessageRoutingInfo amqpRoutingInfo = new AmqpMessageRoutingInfo(hostname, port,
				virtualHost, exchange, routingKey);

		MessagingProvider.getTopicMessageListener(amqpRoutingInfo);

		assertTrue(messagingFactorySpy.messagingRoutingInfo instanceof AmqpMessageRoutingInfo);

		AmqpMessageRoutingInfo storedJmsRoutingInfo = (AmqpMessageRoutingInfo) messagingFactorySpy.messagingRoutingInfo;

		assertEquals(storedJmsRoutingInfo.hostname, amqpRoutingInfo.hostname);
		assertEquals(storedJmsRoutingInfo.port, amqpRoutingInfo.port);
		assertEquals(storedJmsRoutingInfo.routingKey, amqpRoutingInfo.routingKey);
		assertEquals(storedJmsRoutingInfo.virtualHost, amqpRoutingInfo.virtualHost);
		assertEquals(storedJmsRoutingInfo.exchange, amqpRoutingInfo.exchange);
	}

}
