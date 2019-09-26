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

import java.util.ServiceLoader;

public class MessagingProvider {

	private static MessagingFactory messagingFactory;
	private static MessagingModuleStarter starter = new MessagingModuleStarterImp();

	private MessagingProvider() {
		preventConstructorFromEverBeingCalledEvenByReflection();
	}

	private void preventConstructorFromEverBeingCalledEvenByReflection() {
		throw new UnsupportedOperationException();
	}

	public static MessageSender getTopicMessageSender(ChannelInfo channelInfo) {
		ensureMessagingFactoryIsSet();
		return messagingFactory.factorTopicSenderMessage(channelInfo);
	}

	private static void ensureMessagingFactoryIsSet() {
		if (null == messagingFactory) {
			getMessagingFactoryImpUsingModuleStarter();
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
}
