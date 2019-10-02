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

/**
 * Container with information of where to route messages.
 *
 */

public class MessageRoutingInfo {

	public final String hostname;
	public final String port;
	public final String virtualHost;
	public final String routingKey;
	public final String exchange;

	public MessageRoutingInfo(String hostname, String port, String virtualHost, String exchange,
			String routingKey) {
		this.hostname = hostname;
		this.port = port;
		this.virtualHost = virtualHost;
		this.exchange = exchange;
		this.routingKey = routingKey;
	}

}
