package ob.server;

import ob.client.BroadcasterService;

import ob.client.model.bambuser.Oembed;
import ob.client.model.Config;

import ob.model.Broadcaster;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.codehaus.jackson.map.ObjectMapper;


@SuppressWarnings("serial")
public class BroadcasterServiceImpl extends RemoteServiceServlet 
							        implements BroadcasterService 
{

	private static Runnable serverPoll = null;
    private static final long POLL_INTERVAL = 10000;

	private static final Config config = ConfigServiceImpl.getConfigStatic();



	public static final List<Broadcaster> getAllBroadcasters() 
	{
		return getBroadcasters(false);
	}

	// Read-only method for getting a list of Broadcasters
	public static final List<Broadcaster> getBroadcasters(final boolean active)
	{
		List<Broadcaster> bl = new ArrayList<Broadcaster>(); 

		final EntityManager em = EMFSingleton.getEntityManager();
		
		try
		{
			em.getTransaction().begin();

			final List<Broadcaster> bl_ = 
				em.createQuery("SELECT FROM " 
				   			   + Broadcaster.class.getName())
				  .getResultList();
			// TODO: should be query? See below TODO			
			if (active)
			{
				for (final Broadcaster b : bl_)
				{
					if (b.getVideoId() == null || b.getThumbnailURL() == null)
						continue;

					bl.add(b);
				}
			}
			else
				bl = bl_;

			em.getTransaction().commit();			
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

		System.out.println("Getting " + (active ? "active" : "all") + " (" 
						   + bl.size() + ") Broadcasters:");
		for (final Broadcaster b : bl)
		{
			System.out.println("\t[" + b.getBroadcastId() + "]:vid=" 
							   + b.getVideoId() + ", thumbnail=" 
							   + (b.getThumbnailURL() != null ? "set" : "null")
			);
		}
		return Collections.unmodifiableList(bl);
	}

	// Read-only method for getting a single Broadcaster
	public static final Broadcaster getBroadcaster(final String bid)
	{
		Broadcaster b = null;

		final EntityManager em = EMFSingleton.getEntityManager();
		em.getTransaction().begin();
		try
		{
					
			b = em.find(Broadcaster.class, bid);

			em.getTransaction().commit();			
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

		return b;
	}

	// Persist this Broadcaster
	public static void addBroadcaster(final Broadcaster b)
	{

		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Attempting to add a new Broadcaster");
		try
		{	
			em.getTransaction().begin();
			BroadcasterCacheServiceImpl.updateBroadcaster(b);
			em.persist(b);
			em.refresh(b);
			em.getTransaction().commit();
		}
		catch (final Throwable t)
		{
			System.out.println(t.toString());
			t.printStackTrace();
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

	// Remove a particular broadcaster
	public static void deleteBroadcasterStatic(final String bid)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Attempting to retrieve & delete Broadcaster");
		try
		{
			em.getTransaction().begin();
			Broadcaster b_ = em.find(Broadcaster.class, bid);
			if (b_ != null)
			{
				em.remove(b_);
				b_ = null;
			}
			em.getTransaction().commit();
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


	@Override
	public void deleteBroadcaster(final String bid)
	{
		deleteBroadcasterStatic(bid);
	}

	@Override
	public void startServerPoll() throws IllegalArgumentException
	{
		;
	}
/*		System.out.println("startServerPoll()");
		if (serverPoll != null)
		{
			System.out.println("Server poll already running");
			return;
		}
		else
			System.out.println("Initiating server poll");

		serverPoll = new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					try 
					{
						System.out.println("Poll process, serverside");
						
						BroadcasterCacheServiceImpl.updateCacheStatic();
						
						// And sleep
						Thread.currentThread().sleep(POLL_INTERVAL);
					} 
					catch (final Throwable e) 
					{
						System.out.println(e.toString());
						System.out.println("Extenral poll interrupted");
					}
				}
			}
		};

		if (serverPoll != null)
			serverPoll.run();

	}
*/

	private final ob.client.model.Broadcaster bFromB(final Broadcaster b)
	{
		final ob.client.model.Broadcaster b_ = 
			new ob.client.model.Broadcaster(b.getBroadcastId(), b.getJabberId(),
											b.getLatLng(), b.getOrientation(), 
											b.getTimestamp()
			);
		b_.setViews(b.getViews());
		b_.setVideoId(b.getVideoId());
		b_.setThumbnailURL(b.getThumbnailURL());

		return b_;
	}

	@Override
	public final ob.client.model.Broadcaster[] getAllBroadcasters_() 
	{
		return getBroadcasters_(false);
	}


	@Override
	public final ob.client.model.Broadcaster[] 
		getBroadcasters_(final boolean active) 
	{
		final List<ob.client.model.Broadcaster> bs = 
			new ArrayList<ob.client.model.Broadcaster>();
 
		List<Broadcaster> bl = new ArrayList<Broadcaster>();

		final EntityManager em = EMFSingleton.getEntityManager();
		
		try
		{
			em.getTransaction().begin();
			// TODO: why does this query not work??
			/*if (active)
			{
				bl = em.createQuery("SELECT b FROM " + Broadcaster.class.getName()
									+ " b WHERE b.videoId is not null"
									+ " AND b.thumbnailURL is not null")
					   .getResultList();
			}
			else
			{
				bl = em.createQuery("SELECT FROM " 
					 			    + Broadcaster.class.getName())
					   .getResultList();
			}*/
	
	
			bl = em.createQuery("SELECT FROM " 
				 			    + Broadcaster.class.getName())
				   .getResultList();
			

			for (final Broadcaster b : bl)
			{
				// TODO: temp hack here, see above query issue
				if (active && (b.getVideoId() == null || 
							   b.getThumbnailURL() == null))
				{
					continue;
				}

				final ob.client.model.Broadcaster b_ = bFromB(b);
				bs.add(b_);
			
				System.out.println("getAllBroadcasters(); key=" 
								   + b.getBroadcastId() 
								   + ", latlng=" + b.getLatLng()[0]
								   + ", videoId=" + b.getVideoId()
								   + ", views=" + b.getViews());
			}

			em.getTransaction().commit();			
		}
		catch (final Throwable t)
		{
			System.out.println(t.toString());
			t.printStackTrace();
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
				System.out.println(
					"getBroadcasters(): Rolling current transaction back"
				);
			}

		}

		em.close();
		return bs.toArray(new ob.client.model.Broadcaster[0]);
 	}

	@Override
	public void updateBroadcaster_(final ob.client.model.Broadcaster b)
	{

		final EntityManager em = EMFSingleton.getEntityManager();
		em.getTransaction().begin();
		
		try
		{
			final Broadcaster b_ = 
				em.find(Broadcaster.class, b.getBroadcastId());
			b_.update(b);

			System.out.println("Updated Broadcaster, key=" + b.getBroadcastId() 
							   + ", latlng=" + b.getLatLng()[0]
							   + ", videoId=" + b.getVideoId()
							   + ", views=" + b.getViews());
			em.flush();
			em.getTransaction().commit();
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
				System.out.println(
					"updateBroadcaster_(): Rolling current transaction back"
				);
			}

		}

		em.close();
	}

	public static final Oembed getOembedStatic(final String videoId)
	{
		Oembed oembed = null;
		try
		{

			final String url = ConfigServiceImpl
									.getConfigStatic()
									.getBambuserOembedURL() 
							   + videoId;
			
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
			final Map<String, String> map = 
				mapper.readValue(s.toString(), Map.class);

			oembed = new Oembed();
			oembed.setType(map.get("type"));
			oembed.setVersion(map.get("version"));
			oembed.setAuthorName(map.get("author_name"));
			oembed.setAuthorURL(map.get("author_url"));
			oembed.setTitle(map.get("title"));
			oembed.setThumbnailURL(map.get("thumbnail_url"));
			oembed.setThumbnailWidth(map.get("thumbnail_width"));
			oembed.setThumbnailHeight(map.get("thumbnail_height"));
			oembed.setProviderName(map.get("provider_name"));
			oembed.setProviderURL(map.get("provider_url"));
			oembed.setHTML(map.get("html"));
			oembed.setWidth(map.get("width"));
			oembed.setHeight(map.get("height"));
		}
		catch (final Throwable e)
		{
			System.out.println(e.toString());
		}

		return oembed;
	}

	@Override
	public final Oembed getOembed(final String videoId)
	{
		return getOembedStatic(videoId);
	}




	// TEST METHOD BELOW
	@Override
	public void addBroadcasters()
	{
		System.out.println("Making some random Broadcasters");
		final Broadcaster b1 = new Broadcaster();
		b1.setBroadcastId("stuaart");
		b1.setLatLng(new float[]{(float)(Math.random() * 0.2 + 52.9499), 
								(float)(Math.random() - 1.1481)});
		b1.setOrientation(new float[]{(float)Math.random(), 
									 (float)Math.random(), 
									 (float)Math.random()});
		b1.setViews(5);
		
		final Broadcaster b2 = new Broadcaster();
		b2.setBroadcastId("drmartin");
		b2.setLatLng(new float[]{(float)(Math.random() * 0.2 + 52.9499), 
								(float)(Math.random() - 1.1481)});
		b2.setOrientation(new float[]{(float)Math.random(), 
									 (float)Math.random(), 
									 (float)Math.random()});
		b2.setViews(10);

		final Broadcaster b3 = new Broadcaster();
		b3.setBroadcastId("binaryprincess");
		b3.setLatLng(new float[]{(float)(Math.random() * 0.2 + 52.9499), 
								(float)(Math.random() - 1.1481)});
		b3.setOrientation(new float[]{(float)Math.random(), 
									 (float)Math.random(), 
									 (float)Math.random()});
		b3.setViews(2);

		System.out.println("Persisting and refreshing");

		addBroadcaster(b1);
		addBroadcaster(b2);
		addBroadcaster(b3);
		
		System.out.println("Persisted new Broadcasters, keys: b1=" 
						   + b1.getBroadcastId()
						   + ", b2=" + b2.getBroadcastId()
						   + ", b3=" + b3.getBroadcastId());

	}

}
