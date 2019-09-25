package se.uu.ub.cora.messaging;

public class MessagingProvider {

	private MessagingProvider() {
		preventConstructorFromEverBeingCalledEvenByReflection();
	}

	private void preventConstructorFromEverBeingCalledEvenByReflection() {
		throw new UnsupportedOperationException();
	}

	public static void setMessagingFactory(MessagingFactory messagingFactorySpy) {
		// TODO Auto-generated method stub

	}
}
