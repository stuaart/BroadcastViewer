package ob.server;

import ob.client.BroadcasterService;

import ob.model.Broadcaster;

import ob.client.BroadcasterCacheService;

import ob.client.model.Config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import java.net.URL;
import java.net.URLConnection;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;


public class BroadcasterCacheServiceImpl extends RemoteServiceServlet
										 implements BroadcasterCacheService
{

	private static final Config config = ConfigServiceImpl.getConfigStatic();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException 
	{
    	updateCacheStatic();
	}

	@Override
	public void updateCache()
	{
		this.updateCacheStatic();
	}

	public static void updateCacheStatic()
	{
		List<Broadcaster> bl = null; 

		final EntityManager em = EMFSingleton.getEntityManager();
		
		try
		{
			bl = em.createQuery("SELECT FROM " 
				 			    + Broadcaster.class.getName())
				   .getResultList();
			for (final Broadcaster b : bl)
			{
				em.getTransaction().begin();
				String typeStr = "";
				if (config.isLive())
					typeStr = "&type=live";

				String url = config.getBambuserAPIURL() 
							 + "&limit=1&username=" 
							 + b.getBroadcastId() + typeStr;

				URLConnection uc = new URL(url).openConnection();
				BufferedReader in = new BufferedReader(
					new InputStreamReader(uc.getInputStream())
				);
				String line;
				final StringBuffer s = new StringBuffer();
				while ((line = in.readLine()) != null) 
					s.append(line);
				in.close();


				final ObjectMapper mapper = new ObjectMapper();
				final Map<String, Object> map = 
					mapper.readValue(s.toString(), Map.class);
				
				final ArrayList<Object> videos = 
					(ArrayList<Object>)map.get("result");
				
				final Map<String, String> video = 
					(Map<String, String>)videos.get(0);
				
				b.setVideoId(String.valueOf(video.get("vid")));
				b.setThumbnailURL(
					(BroadcasterServiceImpl.getOembedStatic(b.getVideoId()))
				 				  .getThumbnailURL()
				);

/*				// TEMP HACK
				final float[] l = b.getLatLng();
				float[] l_ = 
					new float[] {l[0] + (float)((Math.random() - 0.5) * 0.1), 
								 l[1] + (float)((Math.random() - 0.5) * 0.1)};
				b.setLatLng(l_);
*/
				em.flush();

				em.getTransaction().commit();

			}

			
			System.out.println("Updated Broadcaster cache");

		}
		catch (final Throwable t)
		{
			System.out.println(t.toString());
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				System.out.println("Rolling current transaction back");
			}

		}
		em.close();
	}
}
