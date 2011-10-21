package ob.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("updatecache")
public interface BroadcasterCacheService extends RemoteService
{
	void updateCache();
}
