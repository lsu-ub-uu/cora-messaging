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

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

class MessagingModuleStarterImp implements MessagingModuleStarter {
	private Logger loggerForClass = LoggerProvider
			.getLoggerForClass(MessagingModuleStarterImp.class);
	private MessagingFactory foundMessagingFactory = null;
	private int numberOfImplementations = 0;

	@Override
	public MessagingFactory startUsingMessagingFactoryImplementations(
			Iterable<MessagingFactory> messagingFactoryImplementations) {
		chooseAndCountFactories(messagingFactoryImplementations);
		throwErrorIfNotOne();
		return foundMessagingFactory;
	}

	private void throwErrorIfNotOne() {
		logAndThrowErrorIfNone();
		logAndThrowErrorIfMoreThanOne();
	}

	private void chooseAndCountFactories(
			Iterable<MessagingFactory> messagingFactoryImplementations) {
		for (MessagingFactory messagingFactory : messagingFactoryImplementations) {
			numberOfImplementations++;
			foundMessagingFactory = messagingFactory;
			loggerForClass.logInfoUsingMessage(foundMessagingFactory.getClass().getSimpleName()
					+ " found as implemetation for MessagingFactory");
		}
	}

	private void logAndThrowErrorIfNone() {
		if (numberOfImplementations == 0) {
			String errorMessage = "No implementations found for MessagingFactory";
			loggerForClass.logFatalUsingMessage(errorMessage);
			throw new MessagingInitializationException(errorMessage);
		}
	}

	private void logAndThrowErrorIfMoreThanOne() {
		if (numberOfImplementations > 1) {
			String errorMessage = "More than one implementations found for MessagingFactory";
			loggerForClass.logFatalUsingMessage(errorMessage);
			throw new MessagingInitializationException(errorMessage);
		}
	}

}
