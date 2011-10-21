package ob.client;

import ob.client.model.Broadcaster;
import ob.client.model.bambuser.Oembed;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BroadcasterServiceAsync 
{
	void getAllBroadcasters(AsyncCallback<Broadcaster[]> callback);
	void updateBroadcaster(Broadcaster b, AsyncCallback<Void> callback);
	void getOembed(String videoId, AsyncCallback<Oembed> callback);
	void startServerPoll(AsyncCallback<Void> callback);

	// Temporary
	void addBroadcasters(AsyncCallback<Void> callback);

}
