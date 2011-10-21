package ob.client.model.bambuser;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Oembed implements IsSerializable
{

	
	private String type;
	
	private String version;
	
	private String author_name;
	
	private String author_url;
	
	private String title;
	
	private String thumbnail_url;
	
	private String thumbnail_width;
	
	private String thumbnail_height;
	
	private String provider_name;
	
	private String provider_url;
	
	private String html;
	
	private String width;
	
	private String height;


	public Oembed() {}

	public final String getType()
	{ 
    	return this.type;
    }
	
	public void setType(final String type)
	{ 
    	this.type = type;
    }
	
	public final String getVersion()
	{ 
    	return this.version;
    }
	public void setVersion(final String version)
	{ 
    	this.version = version;
    }

	public final String getAuthorName()
	{ 
    	return this.author_name;
    }
	public void setAuthorName(final String author_name)
	{ 
    	this.author_name = author_name;
    }

	public final String getAuthorURL()
	{ 
    	return this.author_url;
    }
	public void setAuthorURL(final String author_url)
	{ 
    	this.author_url = author_url;
    }

	public final String getTitle()
	{ 
    	return this.title;
    }
	public void setTitle(final String title)
	{ 
    	this.title = title;
    }

	public final String getThumbnailURL()
	{ 
    	return this.thumbnail_url;
    }
	public void setThumbnailURL(final String thumbnail_url)
	{ 
    	this.thumbnail_url = thumbnail_url;
    }

	public final String getThumbnailWidth()
	{ 
    	return this.thumbnail_width;
    }
	public void setThumbnailWidth(final String thumbnail_width)
	{ 
    	this.thumbnail_width = thumbnail_width;
    }

	public final String getThumbnailHeight()
	{ 
    	return this.thumbnail_height;
    }
	public void setThumbnailHeight(final String thumbnail_height)
	{ 
    	this.thumbnail_height = thumbnail_height;
    }

	public final String getProvider_Name()
	{ 
    	return this.provider_name;
    }
	public void setProviderName(final String provide_name)
	{ 
    	this.provider_name = provider_name;
    }

	public final String getProviderURL()
	{ 
    	return this.provider_url;
    }
	public void setProviderURL(final String provider_url)
	{ 
    	this.provider_url = provider_url;
    }

	public final String getHTML()
	{ 
    	return this.html;
    }
		public void setHTML(final String html)
	{ 
    	this.html = html;
    }

	public final String getWidth()
	{ 
    	return this.width;
    }
		public void setWidth(final String width)
	{ 
    	this.width = width;
    }

	public final String getHeight()
	{ 
    	return this.height;
    }
	public void setHeight(final String height)
	{ 
    	this.height = height;
    }

}
