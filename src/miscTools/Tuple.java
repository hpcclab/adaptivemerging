package miscTools;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable,Comparable<Tuple>{
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
    public int compareTo(final Tuple o) {
        Comparable myElement = (Comparable)this.x;
        Comparable oElement = (Comparable)o.x;
        int comparison=myElement.compareTo(oElement);
        if(comparison!=0){
            return comparison;
        }else{
            myElement = (Comparable)this.y;
            oElement = (Comparable)o.y;
            return myElement.compareTo(oElement);
        }
    }
}