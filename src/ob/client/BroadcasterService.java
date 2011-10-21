package ob.client;

import ob.model.Broadcaster;
import ob.client.model.bambuser.Oembed;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("broadcasters")
public interface BroadcasterService extends RemoteService 
{
	ob.client.model.Broadcaster[] getAllBroadcasters();

	void updateBroadcaster(ob.client.model.Broadcaster b); 

	Oembed getOembed(String videoId);

	void startServerPoll();


	// Temporary
	void addBroadcasters();
}
