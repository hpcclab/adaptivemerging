package Cache;

import Streampkg.StreamGOP;

import java.util.HashMap;

public class CachingWithPathTable extends Caching {

    private HashMap<String,String> Table =new HashMap<String,String>();;

    public CachingWithPathTable(){
        super();
    }
    //don't forget to implement this with something to notify settings, currently it count all settings the same
    public String convertStreamGopToString(StreamGOP x){
        return x.videoname+"/"+String.format("%04d",x.segment); //simple version
        //return x.videoname+"/"+String.format("%04d",x.segment)+" "+x.cmdSet.toString(); //store cmd and param too
    }
    public boolean checkExistence(String x){
        return Table.containsKey(x);
    }
    public boolean checkExistence(StreamGOP x)
    {
        return checkExistence(convertStreamGopToString(x));
    }
    //modify path if it is not the same
    public String getCachedPath(StreamGOP x)
    {
        return "streams/"+x.videoname+"/"+String.format("%04d",x.segment)+".ts";
    }

    public void addCached(StreamGOP x){
        Table.put(convertStreamGopToString(x),getCachedPath(x));
    }
}
