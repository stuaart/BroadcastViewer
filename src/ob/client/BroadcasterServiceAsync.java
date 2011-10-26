package ob.client;

import ob.client.model.Broadcaster;
import ob.client.model.bambuser.Oembed;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BroadcasterServiceAsync 
{
	void getBroadcasters_(boolean active, AsyncCallback<Broadcaster[]> callback);
	void getAllBroadcasters_(AsyncCallback<Broadcaster[]> callback);	
	void updateBroadcaster_(Broadcaster b, AsyncCallback<Void> callback);
	void getOembed(String videoId, AsyncCallback<Oembed> callback);
	void startServerPoll(AsyncCallback<Void> callback);

	// Admin use
	void addBroadcasters(AsyncCallback<Void> callback);
	void deleteBroadcaster(String bid, AsyncCallback<Void> callback);
}
