package Streampkg;

import java.util.ArrayList;
import java.util.Map;

//each individual parameter of the operation's data
public class TranscodingRequest implements Comparable<TranscodingRequest>{
    String operation; //redundant saving
    String parameter; //redundant saving
    long adeadline=Long.MAX_VALUE;
    Map<String,String> otherMetadata; //user id etc.

    public TranscodingRequest(String op,String para,long dl, Map metadta){
        operation=op;
        parameter=para;
        adeadline=dl;
        otherMetadata=metadta;
    }
    public void chkUpdatedeadline(long dl){
        if(dl<adeadline){
            adeadline=dl;
        }
    }
    @Override
    public int compareTo(TranscodingRequest o) {
        return o.parameter.compareToIgnoreCase(this.parameter);
    }
}
