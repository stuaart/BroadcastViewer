package ob.client;

import ob.client.model.Broadcaster;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BroadcasterServiceAsync 
{
	void getAllBroadcasters(AsyncCallback<Broadcaster[]> callback);

	// Temporary
	void addBroadcasters(AsyncCallback<Void> callback);

}
