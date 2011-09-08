package ob.server;

import ob.client.BroadcasterService;
import ob.client.model.Broadcaster;
import ob.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.Query;
import javax.persistence.EntityManager;

@SuppressWarnings("serial")
public class BroadcasterServiceImpl extends RemoteServiceServlet 
							        implements BroadcasterService 
{

	public Broadcaster[] getBroadcasters() 
		throws IllegalArgumentException 
	{
		List<Broadcaster> bs = new ArrayList<Broadcaster>();

		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Getting Broadcasters from DB");
		try
		{
			final Query q = 
				em.createQuery("SELECT FROM " 
								+ ob.model.Broadcaster.class.getName());
			final List<ob.model.Broadcaster> bl = q.getResultList();
			for (final ob.model.Broadcaster b : bl)
			{
				bs.add(new Broadcaster(b.getKey().getId(),
									   b.getBroadcastId(),
									   b.getLatLng(), 
									   b.getOrientation(), 
									   b.getTimestamp())
				);
				System.out.println("Got Broadcaster, key=" + b.getKey() 
								   + ", latlng=" + b.getLatLng()[0]);
			}
		}
		catch (final Throwable t)
		{
			System.out.println(t.toString());
		}
		finally
		{
			em.close();
		}

		return bs.toArray(new Broadcaster[0]);
 	}

	public String addBroadcasters() throws IllegalArgumentException
	{
		final EntityManager em = EMFSingleton.getEntityManager();
		System.out.println("Making some random Broadcasters");
		try
		{
			for (int i = 0; i < 1; ++i)
			{
				final ob.model.Broadcaster b = new ob.model.Broadcaster();
				b.setBroadcastId("stuaart");
				b.setLatLng(new float[]{(float)(Math.random() * 1.0 + 53.00), 
										(float)(Math.random() + 1.0)});
				b.setOrientation(new float[]{(float)Math.random(), 
											 (float)Math.random(), 
											 (float)Math.random()});
				em.persist(b);
				em.refresh(b);
				System.out.println("Persisted new Broadcaster, key = " 
								   + b.getKey());
			}
		}
		catch (final Throwable t)
		{
			System.out.println(t.toString());
		}
		finally
		{
			em.close();
		}

		return "Success";
	}
}
