package Simulator;

import mainPackage.CVSE;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//whole stream at a time
public class RequestGenerator_Streamlevel extends RequestGenerator{
    public RequestGenerator_Streamlevel(){
        super();
    }
    public void OneRandomRequest(){

        int videoChoice=(int)(Math.random()* ( CVSE.VR.videos.size()));
        int operation=(int)(Math.random()*(CVSE.GTS.possible_Operations.size()));
        String setting=""+(Math.random()*2); //random between 0 and 1 as setting identifier
        long deadline=0; //did not specified deadline
        //setting.settingIdentifier=randomRes;
        OneSpecificRequest(videoChoice,-1,-1,CVSE.GTS.possible_Operations.get(operation).operationname,setting,deadline,0);
    }


    public requestprofile[] modifyrqeb4sort(requestprofile[] original_rqe,int videos,long segmentcounts){
        //set first few requests to start from Time 0, start off with some load right away
        int maxchange=Math.min(original_rqe.length,10);
        for(int i=0;i<maxchange;i++){
            original_rqe[i].appearTime=0;
        }
        System.out.println("modify before sort, set few video's starting time to 0");
        return original_rqe;
    }
    protected static void cloneCommmon(requestprofile[] original_rqe,int oriindex,int offset) { //match everything
        original_rqe[oriindex].videoChoice = original_rqe[oriindex - offset].videoChoice;
        original_rqe[oriindex].startgopnum = original_rqe[oriindex - offset].startgopnum;
        original_rqe[oriindex].endgopnum = original_rqe[oriindex - offset].endgopnum;
    }
    protected static void cloneA(requestprofile[] original_rqe,int oriindex,int offset){ //match everything
        original_rqe[oriindex].setting = original_rqe[oriindex - offset].setting;
        original_rqe[oriindex].command = original_rqe[oriindex - offset].command;
        cloneCommmon(original_rqe,oriindex,offset);
    }
    protected static void cloneB(requestprofile[] original_rqe,int oriindex,int offset){
        if(original_rqe[oriindex].setting.equalsIgnoreCase(original_rqe[oriindex - offset].setting)){ //make sure setting are different
            original_rqe[oriindex].setting= ""+(Integer.parseInt(original_rqe[oriindex].setting)^1 );
        }
        original_rqe[oriindex].command = original_rqe[oriindex - offset].command;
        cloneCommmon(original_rqe,oriindex,offset);
    }
    protected static void cloneC(requestprofile[] original_rqe, int oriindex, int offset, Random r){
        original_rqe[oriindex].setting = original_rqe[oriindex - offset].setting;
        while(original_rqe[oriindex].command.equalsIgnoreCase(original_rqe[oriindex - offset].command)){ //make sure command are different, rerandom until not match
            original_rqe[oriindex].command= CVSE.GTS.possible_Operations.get(r.nextInt(4)).operationname;
        }
        cloneCommmon(original_rqe,oriindex,offset);
        //validation
        //System.out.println("original (oriindex="+oriindex+":"+original_rqe[oriindex].videoChoice+" "+original_rqe[oriindex].command+" "+original_rqe[oriindex].setting);
        //System.out.println("copy (offset="+offset+"):"+original_rqe[oriindex-offset].videoChoice+" "+original_rqe[oriindex-offset].command+" "+original_rqe[oriindex-offset].setting);
    }
    private int calc_altered(requestprofile rqe){
        if(rqe.startgopnum==-1) {
            return 2 * CVSE.VR.videos.get(rqe.videoChoice).getTotalSegments();
        }else{
            return 2*(rqe.endgopnum-rqe.startgopnum);
        }
    }
    //modify the rqe after sorting by appearance time done
    public requestprofile[] modifyrqeaftersort(requestprofile[] original_rqe,Random r,int videos,int requestcount,long segmentcounts) {

        int cmdspace=CVSE.GTS.possible_Operations.size();
        int paramspace=2;
        int cloneindex=original_rqe.length-1;
        int requestspace=(CVSE.VR.videos.size())*cmdspace*paramspace; //100 videos, 3 cmd, 2 params so request space =600 ?
        int expectedAcount=(int)(requestcount*0.05); // 0.05 for 5% duplication rate
        int expectedBcount=(int)(requestcount*0.15);
        int expectedCcount=(int)(requestcount*0.2);
        int typeA_duplicated=Math.max(requestcount-requestspace,0);
        int typeB_duplicated=Math.max(requestcount-requestspace/paramspace,0);
        int typeC_duplicated=Math.max(requestcount-requestcount/paramspace/cmdspace,0);

        System.out.println("natural duplicatedA ="+typeA_duplicated+" duplicatedB="+typeB_duplicated+" duplicatedC="+typeC_duplicated);
        int altered;
        //start clone from the end instead of from the front, a b c type are more scattered now
        String typeCopied="";
        while((typeA_duplicated < expectedAcount || typeB_duplicated < expectedBcount|| typeC_duplicated < expectedCcount)&&cloneindex >4) {
            if (typeA_duplicated < expectedAcount && cloneindex >4) {
                //copy previous few
                int pminus = Math.abs(r.nextInt(3)) + 1; //pick 1-4 spots ago
                while(typeCopied.equalsIgnoreCase((original_rqe[cloneindex].command))){ //try not to repeat the same copy too many times
                    cloneindex-=1;
                }
                typeCopied=original_rqe[cloneindex].command;
                cloneA(original_rqe, cloneindex, pminus);
                altered=calc_altered(original_rqe[cloneindex]);
                cloneindex -= 3; //so not too often happened
                typeA_duplicated += altered ;
            }
            if (typeB_duplicated < expectedBcount && cloneindex >4) {
                //copy previous few
                int pminus = Math.abs(r.nextInt(3)) + 1;
                while(typeCopied.equalsIgnoreCase((original_rqe[cloneindex].command))){ //try not to repeat the same copy too many times
                    cloneindex-=1;
                }
                typeCopied=original_rqe[cloneindex].command;
                cloneB(original_rqe, cloneindex, pminus);
                altered=calc_altered(original_rqe[cloneindex]);
                cloneindex -= 3;
                typeB_duplicated += altered ;
            }
            if (typeC_duplicated < expectedCcount && cloneindex >4) { //it's actually less likely we need to inject type c match
                //copy previous few
                int pminus = Math.abs(r.nextInt(3)) + 1;
                while(typeCopied.equalsIgnoreCase((original_rqe[cloneindex].command))){ //try not to repeat the same copy too many times
                    cloneindex-=1;
                }
                typeCopied=original_rqe[cloneindex].command;
                cloneC(original_rqe, cloneindex, pminus,r);
                altered=calc_altered(original_rqe[cloneindex]);
                cloneindex -= 3;
                typeC_duplicated += altered;
            }
            System.out.println("duplicatedA ="+typeA_duplicated+" duplicatedB="+typeB_duplicated+" duplicatedC="+typeC_duplicated);
        }

        return original_rqe;

    }
    //enforce least duplicate or match, unless neccessery, then modify to add matching
    public void generateProfiledRandomRequests(String filename,long seed,int totalVideos,int totalRequest,long timeSpan,int avgslack,double sdslack){
        int highPeriod=10000,lowPeriod=30000;
        double highAmp=4.0;
        //random into array, modify, sort array, write to file
        Random r =new Random(seed);
        int parametertypes=2; //number of parameter types
        int i=0;
        try {
            FileWriter F = new FileWriter("BenchmarkInput/" + filename + ".txt");
            PrintWriter writer = new PrintWriter(F);

            ArrayList<requestprofile> rqes = new ArrayList<>();
            //String commandList[]={"Framerate","Resolution","Bitrate","Codec"};
            int fold = 0; //each fold means there is at least one type C matchable,every 2 fold then a type B matched
            // , every 4 fold then a type A matched
            int positionMatchup[] = new int[totalVideos];
            long totalSegmentcount = 0;
            //randomly make it not match at all
            while (totalSegmentcount - totalRequest < 0) {
                //created index 0-totalVideos and shuffle them
                positionMatchup = miscTools.utils.positionshuffle(r, totalVideos);

                //create the request
                for (int q = 0; q < totalVideos; q++) {
                    // video choice is in the positionMatchup
                    String acmd = CVSE.GTS.possible_Operations.get((positionMatchup[q] +2+ fold/parametertypes) % CVSE.GTS.possible_Operations.size()).operationname;// ensure least command overlap as possible
                    //long appear = randTimeLinear(r,timeSpan);
                    long appear =randTimeInterval(r,timeSpan,highPeriod,lowPeriod,highAmp);
                    int thisslacktime;
                    if(avgslack==0){ // if not set, use default value for now
                        if(acmd.equalsIgnoreCase("Codec")){
                            thisslacktime=40000;
                        }else{
                            thisslacktime=12000;
                        }
                    }else{ //if set, use slacktime that is set
                        thisslacktime=avgslack;
                    }
                    long deadline = (long) (r.nextGaussian() * sdslack) + thisslacktime;
                    deadline += appear;
                    int settingnum = (q + fold) % parametertypes;
                    //System.out.println("rqe[]="+(randomDone+q)+" positionMatup[]="+q);
                    rqes.add(new requestprofile(positionMatchup[q], acmd, "" + settingnum, appear, deadline)); //setting ToBeDetermined
                    //System.out.println("b");
                    totalSegmentcount += CVSE.VR.videos.get(positionMatchup[q]).getTotalSegments();
                    //System.out.println("c");
                    if (totalSegmentcount - totalRequest >= 0) {
                        break;
                    }
                }
                fold++;
            }
            int randomDone = rqes.size();
            requestprofile rqe[] = (requestprofile[]) rqes.toArray(new requestprofile[rqes.size()]);
        /*
        for(i=0;i<rqe.length;i++){
            System.out.println("array "+rqe[i].videoChoice);
        }
        */
            //modify
            modifyrqeb4sort(rqe, randomDone, totalSegmentcount); //make some few segment start at time 0
            //sort
            Arrays.sort(rqe);
            System.out.println("rqe length=" + rqe.length);
            //modify again?
            modifyrqeaftersort(rqe, r, randomDone, rqe.length, totalSegmentcount); //enforce some duplication
            // write to file
            System.out.println("randomized " + totalSegmentcount + " segments");
            for (i = 0; i < randomDone; i++) {
                writer.println(rqe[i].videoChoice + " " +rqe[i].startgopnum + " " +rqe[i].endgopnum + " " + rqe[i].command + " " + rqe[i].setting + " " + rqe[i].appearTime + " " + rqe[i].deadline);
            }
            //System.out.println("file wrote");
            writer.close();
        }catch (Exception e){
            System.out.println("Request generator error:"+e);
            e.printStackTrace();
        }
    }
}
