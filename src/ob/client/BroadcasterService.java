package ob.client;

import ob.model.Broadcaster;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("broadcasters")
public interface BroadcasterService extends RemoteService 
{
	ob.client.model.Broadcaster[] getAllBroadcasters() 
		throws IllegalArgumentException;

	// Temporary
	void addBroadcasters() throws IllegalArgumentException;
}
