package ob.server;

import ob.model.Broadcaster;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;


public class BroadcasterJSON extends HttpServlet
{

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException 
	{
    	final PrintWriter out = resp.getWriter();
		out.println("{\"children\":[");
		
		final List<Broadcaster> bl = 
			BroadcasterServiceImpl.getBroadcasterList();

		for (int i = 0; i < bl.size(); ++i)
		{
			out.println("{");
			out.println("\"children\": [], \"data\": {");
			out.println("\"$color\": \"#AE5032\",");
			out.println("\"image\": \"" + bl.get(i).getThumbnailURL() + "\",");
			out.println("\"$area\": " 
						+ String.valueOf(50 + 10 * bl.get(i).getViews()) + ",");
			out.println("\"views\": " 
						+ String.valueOf(50 + 10 * bl.get(i).getViews()));
			out.println("},");
			out.println("\"id\": \"" + bl.get(i).getBroadcastId() + "\",");
			out.println("\"name\": \"" + bl.get(i).getBroadcastId() + "\"");
			out.println("}");
			if (i < bl.size() - 1)
				out.println(",");
		}
		out.println("], \"id\": \"overview\",");
		out.println("\"name\": \"All Broadcasters\"}");
		out.flush();
	}

}
