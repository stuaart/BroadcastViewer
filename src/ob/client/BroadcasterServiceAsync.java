package ob.client;

import ob.client.model.Broadcaster;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BroadcasterServiceAsync 
{

	void getBroadcasters(AsyncCallback<Broadcaster[]> callback);
	void addBroadcasters(AsyncCallback<String> callback);

}
