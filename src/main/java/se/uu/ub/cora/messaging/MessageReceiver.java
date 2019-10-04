package se.uu.ub.cora.messaging;

import java.util.Map;

public interface MessageReceiver {

	void receiveMessage(Map<String, Object> headers, String message);

}
