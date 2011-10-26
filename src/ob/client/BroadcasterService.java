package ob.client;

import ob.model.Broadcaster;
import ob.client.model.bambuser.Oembed;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("broadcasters")
public interface BroadcasterService extends RemoteService 
{
	ob.client.model.Broadcaster[] getBroadcasters_(boolean active);
	ob.client.model.Broadcaster[] getAllBroadcasters_();

	void updateBroadcaster_(ob.client.model.Broadcaster b); 

	Oembed getOembed(String videoId);

	void startServerPoll();


	// Admin use
	void addBroadcasters();
	void deleteBroadcaster(String bid);
}
