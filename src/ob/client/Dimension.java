package ob.client;

public class Dimension
{

	private int width;
	private int height;

	public Dimension()
	{
		width = 0;
		height = 0;
	}

	public Dimension(final int width, final int height)
	{
		this.width = width;
		this.height = height;
	}

	public final int getWidth()
	{
		return width;
	}
	public final int getHeight()
	{
		return height;
	}

	public void setWidth(final int width)
	{
		this.width = width;
	}
	public void setHeight(final int height)
	{
		this.height = height;
	}
}
