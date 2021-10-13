/**
 * The messaging package contains classes and interfaces to interact with a JMS messaging system.
 * <p>
 * There are two main functionallities provided by the cora-messaging module.
 * <p>
 * Sending messages is done through the {@link MessageSender} class which can be created using
 * {@link MessagingProvider#getTopicMessageSender(MessageRoutingInfo)}.
 * <p>
 * Recieving messages is done by implementing the {@link MessageReceiver} interface and connecting
 * it to a specific message queue through a {@link MessageListener} which can be created using
 * {@link MessagingProvider#getTopicMessageListener(MessageRoutingInfo)}.
 * <p>
 * 
 * 
 * A project that provides a bridge to specific implementation of a JMS queue, MUST implement
 * {@link se.uu.ub.cora.messaging.MessagingFactory} which includes implementations of
 * {@link MessageListener} and {@link MessageSender}. Known bridge projects are cora-rabbitmq and
 * cora-amqp. Bridge projects present during runtime will be automatically loaded via service
 * loader.
 */
package se.uu.ub.cora.messaging;