package Simulator;

import mainPackage.CVSE;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//some few GOPs at a time
public class RequestGenerator_GOPlevel extends RequestGenerator_Streamlevel{
    public RequestGenerator_GOPlevel(){
        super();
    }
    public void OneRandomRequest() {

        int videoChoice=(int)(Math.random()* ( CVSE.VR.videos.size()));
        int operation=(int)(Math.random()*(CVSE.GTS.possible_Operations.size()));
        String setting=""+(Math.random()*2); //random between 0 and 1 as setting identifier
        long deadline=0; //did not specified deadline
        int gopnum=(int)(Math.random())*CVSE.VR.videos.get(videoChoice).getTotalSegments();
        //setting.settingIdentifier=randomRes;
        OneSpecificRequest(videoChoice,gopnum,gopnum,CVSE.GTS.possible_Operations.get(operation).operationname,setting,deadline,0);
    }

    //FEW GOP per generation, instead of a lot.
    public void generateProfiledRandomRequests(String filename,long seed,int totalVideos,int totalRequest,long timeSpan,int avgslack,double sdslack) {
        int highPeriod=10000,lowPeriod=30000;
        double highAmp=2.0;
        //random into array, modify, sort array, write to file
        Random r =new Random(seed);
        int parametertypes=2; //number of parameter types
        int i=0;
        int reqperround=10; //number of GOP request per round incoming request.
        int indexloop=0;
        try {
            FileWriter F = new FileWriter("BenchmarkInput/" + filename + ".txt");
            PrintWriter writer = new PrintWriter(F);

            ArrayList<requestprofile> rqes = new ArrayList<>();
            //String commandList[]={"Framerate","Resolution","Bitrate","Codec"};
            int fold = 0;
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
                            //thisslacktime=50000;
                            thisslacktime=40000;
                        }else{
                            //thisslacktime=17000;
                            thisslacktime=12000;
                        }
                    }else{ //if set, use slacktime that is set
                        thisslacktime=avgslack;
                    }
                    long deadline = (long) (r.nextGaussian() * sdslack) + thisslacktime;
                    deadline += appear;
                    int vidSegmentCount= CVSE.VR.videos.get(positionMatchup[q]).getTotalSegments();
                    int settingnum = (q + fold) % parametertypes;
                    int startingID=indexloop%vidSegmentCount;
                    int endID;
                    if(indexloop%vidSegmentCount>vidSegmentCount-reqperround){
                        endID=vidSegmentCount;
                    }else{
                        endID=(indexloop+reqperround)%vidSegmentCount;
                    }
                    //System.out.println("rqe[]="+(randomDone+q)+" positionMatup[]="+q);
                    rqes.add(new requestprofile(positionMatchup[q], acmd, "" + settingnum, appear, deadline,startingID,endID)); //setting ToBeDetermined
                    //System.out.println("b");
                    totalSegmentcount += endID-startingID;
                    //System.out.println("c");
                    if (totalSegmentcount - totalRequest >= 0) {
                        break;
                    }
                }
                fold++;
                indexloop+=reqperround;
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
