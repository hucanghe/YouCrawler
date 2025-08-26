package youtube;

class YouTuber
{
  String urlString;
  String name;
  String videoLabel;

  public YouTuber(String aUrlString, String aName, String aVideoLabel)
  {
    super();
    this.urlString = aUrlString;
    this.name = aName;
    this.videoLabel = aVideoLabel;
  }

  public String getUrlString()
  {
    return urlString;
  }

  public void setUrlString(String urlString)
  {
    this.urlString = urlString;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getVideoLabel()
  {
    return videoLabel;
  }

  public void setVideoLabel(String videoLabel)
  {
    this.videoLabel = videoLabel;
  }

}
