package Cache;

import Streampkg.StreamGOP;

import java.io.File;

public class CachingFileScan extends Caching {
    public CachingFileScan(){
        super();
    }
    public boolean checkExistence(StreamGOP x)
    {
        //currently only check if file is exist, not checking for set-up
        File targetfile = new File(getCachedPath(x));
        return targetfile.exists();
    }
    // if cached, return Path String for cached video
    public String getCachedPath(StreamGOP x)
    {
        return "streams/"+x.videoname+"/"+String.format("%04d",x.segment)+".ts";
    }
}
