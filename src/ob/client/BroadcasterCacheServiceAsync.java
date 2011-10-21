package ob.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BroadcasterCacheServiceAsync
{
	void updateCache(AsyncCallback<Void> callback);
}
