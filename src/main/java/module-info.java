/**
 * The messaging module provides interfaces and classes to interact with a JMS messaging system.
 */
module se.uu.ub.cora.messaging {
	requires transitive se.uu.ub.cora.logger;

	exports se.uu.ub.cora.messaging;

	uses se.uu.ub.cora.messaging.MessagingFactory;
}