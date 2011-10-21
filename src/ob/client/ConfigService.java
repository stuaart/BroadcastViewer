package ob.client;

import ob.client.model.Config;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("config")
public interface ConfigService extends RemoteService
{
	Config getConfig() throws IllegalArgumentException;
}
