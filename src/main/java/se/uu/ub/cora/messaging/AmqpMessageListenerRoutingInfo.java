/*
 * Copyright 2023, 2025 Uppsala University Library
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

/**
 * AmqpMessageListenerRoutingInfo holds listening info for connecting to an AmqpMessageBroker, there
 * are two different constructors that can be used if you want to connect to an existing queue or
 * have a queue autocreated.
 */
public class AmqpMessageListenerRoutingInfo extends MessageRoutingInfo {

	public final String virtualHost;
	public String queueName;
	public String exchange;
	public String routingKey;

	/**
	 * This constructor should be used when connecting to an existing queue.
	 * 
	 * @param hostname
	 *            A String with the hostname of the message server
	 * @param port
	 *            An int with the port of the message server
	 * @param virtualHost
	 *            A String with the virtual host to connect to
	 * @param queueName
	 *            A String with the queue name to connect to
	 */
	public AmqpMessageListenerRoutingInfo(String hostname, int port, String virtualHost,
			String queueName) {
		super(hostname, port);
		this.virtualHost = virtualHost;
		this.queueName = queueName;
	}

	/**
	 * This constructor should to be used when queue and binding needs to be auto created.
	 * 
	 * @param hostname
	 *            A String with the hostname of the message server
	 * @param port
	 *            An int with the port of the message server
	 * @param virtualHost
	 *            A String with the virtual host to connect to
	 * @param exchange
	 *            A String representing the name of the exchange to which the auto-created queue
	 *            will be bound.
	 * @param routingKey
	 *            A String specifying the routing key used for binding between the exchange and the
	 *            auto-created queue. The format of the routing key must conform to the exchange
	 *            type.
	 */
	public AmqpMessageListenerRoutingInfo(String hostname, int port, String vhost, String exchange,
			String routingKey) {
		super(hostname, port);
		this.virtualHost = vhost;
		this.exchange = exchange;
		this.routingKey = routingKey;
	}
}
