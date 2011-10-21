package ob.client;

import ob.client.model.Config;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigServiceAsync
{
	void getConfig(AsyncCallback<Config> callback);
}
