package ob.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMFSingleton
{
	private static EntityManagerFactory emFactory = null;

	public static EntityManager getEntityManager()
	{
		if (emFactory == null)
		{
			emFactory = 
				Persistence.createEntityManagerFactory("transactions-optional");
		}
		return emFactory.createEntityManager();
	}

	private EMFSingleton()
	{
	}
}
