package miscTools;

import java.util.Random;

public class utils {
    //return 0-size index array in shuffled order
    public static int[] positionshuffle(Random r, int size){
        int[] positionmatchup=new int[size];
        //create index in order
        for(int p=0;p<size;p++){
            positionmatchup[p]=p;
        }
        //randomly shuffle, swap s with random index
        for(int s=0;s<size;s++){
            int tmp=positionmatchup[s];
            int swappair=Math.abs(r.nextInt()%size);
            positionmatchup[s]=positionmatchup[swappair];
            positionmatchup[swappair]=tmp;
        }
        return positionmatchup;
    }

    public static void printAll(Object ... aList){
        boolean first=true;
        for(Object each: aList){

            System.out.print(first ? " " : "");
            first = false;
            System.out.print(each.toString());
        }
        System.out.print("\n");
    }
}
