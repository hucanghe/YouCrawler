package youtube;
public class Video {
		    String watchLink;
		    String name;
		    String pubTime;
		    String title;

		    public String getWatchLink()
		    {
		      return watchLink;
		    }

		    public void setWatchLink(String watchLink)
		    {
		      this.watchLink = watchLink;
		    }

		    public String getName()
		    {
		      return name;
		    }

		    public void setName(String name)
		    {
		      this.name = name;
		    }

		    public String getPubTime()
		    {
		      return pubTime;
		    }

		    public void setPubTime(String pubTime)
		    {
		      this.pubTime = pubTime;
		    }

		    public String getTitle()
		    {
		      return title;
		    }

		    public void setTitle(String title)
		    {
		      this.title = title;
		    }

		    public Video(String watchLink, String name, String pubTime, String title)
		    {
		      super();
		      this.watchLink = watchLink;
		      this.name = name;
		      this.pubTime = pubTime;
		      this.title = title.indexOf("#") < 0 ? title : title.substring(0, title.indexOf("#"));
		    }

}
