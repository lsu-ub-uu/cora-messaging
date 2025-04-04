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

import static java.util.UUID.randomUUID;

import java.util.ServiceLoader;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.starter.MessagingModuleStarter;
import se.uu.ub.cora.messaging.starter.MessagingModuleStarterImp;

/**
 * MessagingProvider provides access to implementing classes that can send and recieve messages. The
 * implementing classes are intended to be provided by other java modules.
 */
public class MessagingProvider {

	private static MessagingFactory messagingFactory;
	private static MessagingModuleStarter starter = new MessagingModuleStarterImp();
	private static Logger log = LoggerProvider.getLoggerForClass(MessagingProvider.class);
	private static String messagingId = randomUUID().toString();

	private MessagingProvider() {
		// prevent constructor from ever being called even by reflection
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a MessageSender for the topic described in the entered messagingRoutingInfo using an
	 * implementation of MessagingFactory, that will be loaded through javas ServiceLoader load
	 * method.
	 * 
	 * @param messagingRoutingInfo
	 *            A messagingRoutingInfo that should contain information about how to reach the
	 *            channel to send messages to
	 * @return A MessageSender for the topic described in messagingRoutingInfo
	 */
	public static MessageSender getTopicMessageSender(MessageRoutingInfo messagingRoutingInfo) {
		ensureMessagingFactoryIsSet();
		return messagingFactory.factorTopicMessageSender(messagingRoutingInfo);
	}

	private static synchronized void ensureMessagingFactoryIsSet() {
		if (null == messagingFactory) {
			log.logInfoUsingMessage("MessagingProvider starting...");
			getMessagingFactoryImpUsingModuleStarter();
			log.logInfoUsingMessage("MessagingProvider started");
		}
	}

	private static void getMessagingFactoryImpUsingModuleStarter() {
		Iterable<MessagingFactory> messagingFactoryImplementations = ServiceLoader
				.load(MessagingFactory.class);
		messagingFactory = starter
				.startUsingMessagingFactoryImplementations(messagingFactoryImplementations);
	}

	/**
	 * Sets a MessagingFactory that will be used to factor message handlers. This possibility to set
	 * a MessagingFactory is provided to enable testing of message handling in other classes and is
	 * not intented to be used in production. The MessagingFactory to use should be provided through
	 * an implementation of MessagingFactory in a seperate java module.
	 * 
	 * @param messagingFactory
	 *            A MessagingFactory to use to create messag handlers for testing
	 */
	public static void setMessagingFactory(MessagingFactory messagingFactory) {
		MessagingProvider.messagingFactory = messagingFactory;
	}

	static void setStarter(MessagingModuleStarter starter) {
		MessagingProvider.starter = starter;
	}

	static MessagingModuleStarter getStarter() {
		return starter;
	}

	public static MessageListener getTopicMessageListener(MessageRoutingInfo messagingRoutingInfo) {
		ensureMessagingFactoryIsSet();
		return messagingFactory.factorTopicMessageListener(messagingRoutingInfo);
	}

	/**
	 * Returns a MessagingId that is unique for this instance of the application.
	 */
	public static String getMessagingId() {
		return messagingId;
	}
}
