package ob.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("broadcasters")
public interface BroadcasterService extends RemoteService 
{
	Broadcaster[] getBroadcasters() throws IllegalArgumentException;
	String addBroadcasters() throws IllegalArgumentException;
}
