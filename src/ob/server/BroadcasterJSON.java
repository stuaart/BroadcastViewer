package ob.server;

import ob.model.Broadcaster;

import ob.client.model.Config;

import com.google.gwt.core.client.GWT;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class BroadcasterJSON extends HttpServlet
{

	private static final Config config = ConfigServiceImpl.getConfigStatic();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException 
	{
		final List<Broadcaster> bl_ = 
			BroadcasterServiceImpl.getBroadcasters(true);
		final List<Broadcaster> bl = new ArrayList<Broadcaster>();
		bl.addAll(bl_);
		Collections.sort(bl);


    	final PrintWriter out = resp.getWriter();
		out.println("{\"children\":[");
		
		boolean filler = false;
		int max = bl.size();
		if (max > config.getMaxGrid())
			max = config.getMaxGrid();

		final double sqrt = Math.sqrt(max);
		if (sqrt % Math.round(sqrt) != 0 && max % 2 != 0)
			filler = true;

		int t = 0;

		for (int i = 0; i < max; ++i)
		{
			out.println(makeEntry(bl.get(i).getThumbnailURL(),
								  String.valueOf(bl.get(i).getViews()),
								  String.valueOf(bl.get(i).getViews()),
								  bl.get(i).getBroadcastId(),
								  bl.get(i).getBroadcastId())
			);
			t += bl.get(i).getViews();

			if (i < max - 1)
				out.println(",");
			//else if (filler)
			//{
			//	out.println(",");
			//	final String vs = String.valueOf(t / bl.size());
			//	out.println(makeEntry(null, vs, vs, "filler", "filler"));
			//}
		}
		out.println("], \"id\": \"overview\",");
		out.println("\"name\": \"All Broadcasters\"}");
		out.flush();
	}

	private final String makeEntry(final String thumbnailURL, 
								   final String views, final String area, 
								   final String id, final String name)
	{

		final StringBuffer s = new StringBuffer();
		s.append("{");
		s.append("\"children\": [], \"data\": {");
		s.append("\"$color\": \"#AE5032\",");
		if (thumbnailURL == null)
			s.append("\"image\": null,");
		else
			s.append("\"image\": \"" + thumbnailURL + "\",");
		s.append("\"$area\": " + area + ",");
		s.append("\"views\": " + views);
		s.append("},");
		s.append("\"id\": \"" + id + "\",");
		s.append("\"name\": \"" + name + "\"");
		s.append("}");

		return s.toString();
	}
}
