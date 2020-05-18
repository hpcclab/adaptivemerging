package Simulator;

import Streampkg.Stream;
import mainPackage.CVSE;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;


// this class only contain usable playback of the generated requests
// the workload generation is done in class that extends this class.
public abstract class RequestGenerator {
    private Semaphore canGenTask;
    public boolean finished=false;
    protected static ArrayList<requestprofile> rqe_arr=new ArrayList<>();

    // static String videonameList[]={"bbb_trailer","ff_trailer_part1","ff_trailer_part3"}; //not using
    public RequestGenerator(){
        canGenTask=new Semaphore(1);
    }

    public void OneRandomRequest(){
    }

    public void OneSpecificRequest( int videoChoice,int gopstartnum,int gopendnum, String command, String setting, long deadline, long arrival ){
        System.out.print("create one specific request: ");
        System.out.println("videoChoice "+videoChoice);
        Stream ST;
        if(gopstartnum==-1) { //whole steam
            ST = new Stream(CVSE.VR.videos.get(videoChoice), command, setting, deadline, arrival); //admission control can work in constructor, or later?
        }else{
            ST = new Stream(CVSE.VR.videos.get(videoChoice), command, setting, gopstartnum, gopendnum, deadline, arrival);
        }
        System.out.println(ST.video.name);
        CVSE.GTS.addStream(ST);
    }

    //simple static RandomRequest Generator
    public void nRandomRequest(int Request_Numbers, int interval, int n){
        //interval =-1 for random delay

        int round = 1;
        do {
            for (int i = 0; i < Request_Numbers; i++) {
                OneRandomRequest();
            }
            round++;
            if(interval>0&&round<n) {
                try {
                    sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while(round<n);
    }

    //read all data profile to rqe
    public void ReadProfileRequests(String filename){
        System.out.println("overwrite readprofilerequest");
        File F=new File("BenchmarkInput/"+filename);
        Scanner scanner= null;
        try {
            scanner = new Scanner(F);
            while(scanner.hasNext()){
                String aline[]=scanner.nextLine().split("\\s+");
                if(aline.length==7){
                    //System.out.println("New Benchmark format");
                    requestprofile aRequest=new requestprofile(Integer.parseInt(aline[0]),aline[3],aline[4],Long.parseLong(aline[5]),Long.parseLong(aline[6]),Integer.parseInt(aline[1]),Integer.parseInt(aline[2]));
                    rqe_arr.add(aRequest);
                }else if((aline.length==5)) {
                    //System.out.println("Legacy Benchmark profile read");
                    requestprofile aRequest=new requestprofile(Integer.parseInt(aline[0]),aline[1],aline[2],Long.parseLong(aline[3]),Long.parseLong(aline[4]));
                    rqe_arr.add(aRequest);
                }else{
                    System.out.println("invalid format found");
                }
            }
        } catch (Exception e) {
            System.out.println("read benchmarkProfileFail"+e);
        }
    }
//
    public void generateProfiledRandomRequests(String filename,long seed,int totalVideos,int totalRequest,long timeSpan,int avgslack,double sdslack) {
    }
    int currentIndex=0;
    //Something bug?, with merging sometime doesn't finish...
    public void contProfileRequestsGen(){
        if(canGenTask.tryAcquire()) {
            System.out.println("Check task arrival");
            System.out.println("rqe arr size="+rqe_arr.size());
            if (currentIndex < rqe_arr.size()) {
                while (rqe_arr.get(currentIndex).appearTime <= CVSE.GTS.maxElapsedTime) {
                    requestprofile arqe = rqe_arr.get(currentIndex);
                    currentIndex++;
                    OneSpecificRequest(arqe.videoChoice,arqe.startgopnum,arqe.endgopnum, arqe.command, arqe.setting, arqe.deadline, arqe.appearTime);
                    if (currentIndex >= rqe_arr.size()) {
                        System.out.println("all task arrived");
                        finished = true;
                        break;
                    }
                    System.out.println("currentIndex=" + currentIndex + " rqe_arr size=" + rqe_arr.size());
                }
            }
            canGenTask.release();
            System.out.println("Finish Request Gen");
        }else{
            System.out.println("RequestGenerator is still busy, not generating task");
        }
    }
    public long nextappearTime(){
        if(currentIndex<rqe_arr.size()) {
            return rqe_arr.get(currentIndex).appearTime;
        }
        return -1;
    }

    //simple linear random
    public long randTimeLinear(Random r,long timeSpan){
        return Math.abs(r.nextLong() % timeSpan);
    }
    //create high and low work load intensity pulse
    public long randTimeInterval(Random r,long timeSpan,int hiperiodlength,int lowperiodlength,double highperiodAmp){
        long pulselength=hiperiodlength+lowperiodlength;
        long pseudopulselength=lowperiodlength+ (long)(hiperiodlength*highperiodAmp);
        long pseudotimeSpan=timeSpan/pulselength*pseudopulselength;

        long rand=Math.abs(r.nextLong() % pseudotimeSpan);
        long flop=rand/pseudopulselength;
        long remain=rand%pseudopulselength;
        long transformedTime=flop*pulselength;
        if(remain<=hiperiodlength*highperiodAmp){ //end in high range
            return transformedTime+(long)(remain/highperiodAmp);
        }else{ // end in low range
            return transformedTime+ (long)(remain-hiperiodlength*highperiodAmp) ;
        }
    }
}