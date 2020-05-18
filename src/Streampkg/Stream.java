package Streampkg;

import Repository.Video;
import Scheduler.SystemConfig;
import mainPackage.CVSE;

import java.util.ArrayList;

public class Stream {
    public Video video; // for reference of which video we are talking about
    public String id;
    private int status;
    public ArrayList<StreamGOP> streamGOPs;
    public long presentationTime;
    public Settings videoSettings = null;

    public void ScheduleVideoSegments(){

    }
    public Stream(){
        status=0;
        video =new Video();
        if(CVSE.config.run_mode.equalsIgnoreCase("sim")){

        }else {
            presentationTime = System.currentTimeMillis() + 1000; //thisTime+Constant for now, should really be scheduleTime
        }
        streamGOPs= new ArrayList<StreamGOP>();
    }
    public Stream(Video v,String command,String settings,long deadline,long arrivalTime){
        this(v,command,settings,0,v.repositoryGOPs.size(),deadline,arrivalTime);
    }
    public Stream(Video v,String command, String settings, int startSegment,long deadline,long arrivalTime){
        this(v,command,settings,startSegment,v.repositoryGOPs.size(),deadline,arrivalTime);
    }

    public Stream(Video v,String command,String settings,int startSegment,int endSegment,long deadline,long arrivalTime){

        status=0;
        video =v;
        if(deadline==0) { //ST==0, did not specified a preliminary deadline
            //normally dont fall in this case anyway in sim mode
            if(CVSE.config.run_mode.equalsIgnoreCase("sim")) {

                presentationTime= CVSE.GTS.maxElapsedTime+2000; //add a prelinary value
            }else{
                presentationTime = System.currentTimeMillis() + 2000; //thisTime+Constant for now, should really be scheduleTime
            }
        }else{
            presentationTime=deadline;
        }
        streamGOPs= new ArrayList<StreamGOP>();
        //for(RepositoryGOP x: v.repositoryGOPs){
        for(int i=startSegment;i<endSegment;i++){
            String designatedSettings;
            if(settings.equalsIgnoreCase("TBD")){ //change TBD to stream&gops specific
                System.out.println("this Stream using TBD translating");
                //designatedSettings=(i+1)+"_"+v.name;
                designatedSettings=settings;
                //designatedSettings=v.name;
                //System.out.println("setting="+designatedSettings);
            }else{
                designatedSettings=settings;
            }
            StreamGOP xcopy=new StreamGOP(video.name,this,v.repositoryGOPs.get(i),command,designatedSettings,presentationTime,arrivalTime);
            //System.out.println("deadline of "+video.name+" "+(i+1)+"="+xcopy.getDeadLine());
            streamGOPs.add(xcopy);
        }
    }

    ////////// real mode
    public Stream(Video v, Settings settings){
        this(v,settings.type, settings.resWidth,0,v.repositoryGOPs.size(),(long)0,(long)0, settings);
    }
    public Stream(Video v,String command, String settings,int startSegment,int endSegment,long addedslackTime,long arrivalTime,Settings vidSettings){
        videoSettings = vidSettings;
        status=0;
        video =v;
        if(addedslackTime==0) { //ST==0, did not specified start time, make a new startTime
            //normally dont fall in this case anyway in sim mode
            if(CVSE.config.run_mode.equalsIgnoreCase("sim")) {

                presentationTime= CVSE.GTS.maxElapsedTime+2000; //add a prelinary value
            }else{
                presentationTime = System.currentTimeMillis() + 2000; //thisTime+Constant for now, should really be scheduleTime
            }
        }else{
            presentationTime=addedslackTime;
        }
        streamGOPs= new ArrayList<StreamGOP>();

        for(int i=startSegment;i<endSegment;i++){
            StreamGOP xcopy=new StreamGOP(video.name,this,v.repositoryGOPs.get(i),command,settings,presentationTime,arrivalTime,vidSettings);
            //System.out.println("deadline of "+video.name+" "+(i+1)+"="+xcopy.getDeadLine());
            streamGOPs.add(xcopy);
        }
    }
    public int GetStatus(){
        return status;
    }
}