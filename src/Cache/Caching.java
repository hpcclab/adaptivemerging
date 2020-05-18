package Cache;

import Streampkg.StreamGOP;

public class Caching {
    public Caching(){
    }
    //always return false in this simplest version, all items not exist
    public boolean checkExistence(StreamGOP x)
    {
        return false;
    }
    // if cached, return Path String for cached video
    public String getCachedPath(StreamGOP x)
    {
        return "";
    }
    // if caching policy can remember the video, implement this function
    public void addCached(StreamGOP x){

    }
}
