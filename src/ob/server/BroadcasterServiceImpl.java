package ob.server;

import ob.client.BroadcasterService;
import ob.model.Broadcaster;
import ob.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;

import javax.persistence.Query;
import javax.persistence.EntityManager;

@SuppressWarnings("serial")
public class BroadcasterServiceImpl extends RemoteServiceServlet 
							        implements BroadcasterService 
{

	private static Runnable externalPoll = null;
	private final Timer timer = new Timer();
    private static final long POLL_INTERVAL = 10000;

	public static void deleteBroadcaster(final Broadcaster b)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Attempting to retrieve & delete Broadcaster");
		try
		{
			em.getTransaction().begin();
			final Broadcaster b_ = em.find(Broadcaster.class, 
										   b.getBroadcastId());			
			em.remove(b_);
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

	public static void updateBroadcaster(final Broadcaster b)
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Attempting to retrieve & update Broadcaster");
		try
		{
			em.getTransaction().begin();
					
			final Broadcaster b_ = em.find(Broadcaster.class, 
										   b.getBroadcastId());
			/*final List<Broadcaster> bl = 
				em.createQuery("SELECT b FROM " 
							   + Broadcaster.class.getName()
							   + " b WHERE b.broadcastId = :id" )
				  .setParameter("id", b.getBroadcastId());
				  .getResultList();
			if (bl.isEmpty())
			{
				em.persist(b);
				em.refresh(b);
				System.out.println("Couldn't find Broadcaster, so persisted new"
								   + " Broadcaster, key = " + b.getKey());
			}
			else if (bl.size() == 1)
			{
				final Broadcaster b_ = bl.get(0);
				b_.update(b);
				System.out.println("Updated existing Broadcaster");
			}
			else
			{
				System.out.println("Error: too got too many Broadcasters for "
								   + "query");
			}
*/
			if (b_ == null)
			{
				em.persist(b);
				em.refresh(b);
				System.out.println("Couldn't find Broadcaster, so persisted new"
								   + " Broadcaster, id = " 
								   + b.getBroadcastId());
			}
			else
			{
				b_.update(b);
				System.out.println("Updated existing Broadcaster");
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


	// Service methods below

	public void startExternalPoll() throws IllegalArgumentException
	{
		if (externalPoll == null)
		{
			externalPoll = new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						try 
						{
							Thread.sleep(POLL_INTERVAL);
						} 
						catch (final Throwable e) 
						{
							System.out.println(e.toString());
							System.out.println("Extenral poll interrupted");
						}
					}
 				}
				
			};
			externalPoll.run();
		}
	}

	public ob.client.model.Broadcaster[] getAllBroadcasters() 
		throws IllegalArgumentException 
	{
		List<ob.client.model.Broadcaster> bs = 
			new ArrayList<ob.client.model.Broadcaster>();

		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Getting all Broadcasters from DB");
		try
		{
			final List<Broadcaster> bl = 
				em.createQuery("SELECT FROM " 
								+ Broadcaster.class.getName())
				  .getResultList();

			em.getTransaction().begin();
			
			for (final Broadcaster b : bl)
			{
				bs.add(new ob.client.model.Broadcaster(b.getBroadcastId(),
													   b.getJabberId(),
													   b.getLatLng(), 
													   b.getOrientation(), 
													   b.getTimestamp())
				);
				System.out.println("Got Broadcaster, key=" + b.getBroadcastId() 
								   + ", latlng=" + b.getLatLng()[0]);
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

		return bs.toArray(new ob.client.model.Broadcaster[0]);
 	}

	// TEST METHOD BELOW
	public void addBroadcasters() throws IllegalArgumentException
	{
		System.out.println("Making some random Broadcasters");
		for (int i = 0; i < 1; ++i)
		{
			final Broadcaster b = new Broadcaster();
			b.setBroadcastId("stuaart");
			b.setLatLng(new float[]{(float)(Math.random() * 1.0 + 51.00), 
									(float)(Math.random() + 1.0)});
			b.setOrientation(new float[]{(float)Math.random(), 
										 (float)Math.random(), 
										 (float)Math.random()});
			updateBroadcaster(b);
			System.out.println("Persisted new Broadcaster, key = " 
							   + b.getBroadcastId());
		}
	}
}
