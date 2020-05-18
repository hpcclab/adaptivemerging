package Streampkg;

import Repository.RepositoryGOP;
import Scheduler.SystemConfig;
import mainPackage.CVSE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class StreamGOP extends RepositoryGOP implements Comparable<StreamGOP>,java.io.Serializable {
    public HashMap<String, HashMap<String,TranscodingRequest>> cmdSet=new HashMap<String, HashMap<String,TranscodingRequest>>(); //key is Operation, value is hashmap of parameter
    //the Map in 2nd layer's key is parameter, then value is each instance of TranscodingRequest
    public transient Stream parentStream;
    public Settings videoSetting = null; //legacy one, from Vaughan's RealMode

    public String videoname = "";
    public long deadLine=Long.MAX_VALUE; //global deadline for the entire StreamGOP
    public long fileSize =900; //deprecated, use parameter of cmdSet instead
    public boolean dispatched=false;
    public long arrivalTime=0;
    public long estimatedExecutionTime=0;
    public double estimatedExecutionSD=0;
    public int flags=0; //can be used to store some marked status, currently, %2==1 means it got PASSED in their queue
    public int requestcount=0; //be 1 unless merged

    public StreamGOP(){ //arbitary request //instant deadline miss in this case
        super();
        deadLine=presentationTime;
    }
    public StreamGOP(long arrivaltime){ //arbitary request
        super();
        deadLine=arrivaltime+presentationTime;
        arrivalTime=arrivaltime;
    }

    public StreamGOP(String name,Stream p,RepositoryGOP x,long starttime,long arrivaltime){ //does not assign the
        super(x);
        videoname=name;
        parentStream=p;
        deadLine=presentationTime+starttime;
        arrivalTime=arrivaltime; //not actually used but store it just in case

        //System.out.println("X presentationTime="+x.presentationTime);
        //System.out.println("this deadline="+deadLine);
    }
    public StreamGOP(String name,Stream p,RepositoryGOP x, String Command,String Setting,long startTime,long arrivaltime){
        this(name,p,x,startTime,arrivaltime);
        addCMD(Command, Setting,deadLine);
    }
    //video setting is implementation of Vaughan for real mode.
    public StreamGOP(String name,Stream p,RepositoryGOP x, String Command,String Setting,long startTime,long arrivaltime,Settings vidSetting){
        this(name,p,x,startTime,arrivaltime);
        videoSetting = vidSetting;
        addCMD(Command, Setting,deadLine);
    }
    //deep clone
    public StreamGOP(StreamGOP X){
        this.setPath(X.getPath());
        this.segment=X.segment;
        this.isTranscoded=X.getIsTranscoded();
        this.setting=X.setting;
        this.presentationTime=X.presentationTime;
        this.arrivalTime=X.arrivalTime;
        //System.out.println("X presentationTime="+X.presentationTime);
        //
        this.videoname=X.videoname;
        this.parentStream=X.parentStream;
        this.deadLine=X.deadLine;
        this.estimatedExecutionSD=X.estimatedExecutionSD;
        this.estimatedExecutionTime=X.estimatedExecutionTime;
        this.requestcount=X.requestcount;
        this.videoSetting=X.videoSetting;
        //
        for(String command : X.cmdSet.keySet()){
            HashMap<String,TranscodingRequest> param= new HashMap<String,TranscodingRequest>(X.cmdSet.get(command));
            cmdSet.put(command,param);
        }
    }
    public boolean containCmdParam(String cmd,String param){
        if(cmdSet.containsKey(cmd)) {
            if (cmdSet.get(cmd).containsKey(param)) {
                return true;
            }
        }
        return false;
    }
    public long getdeadlineof(String cmd,String param){
        if(containCmdParam(cmd,param)) {
                return cmdSet.get(cmd).get(param).adeadline;
        } //else
        System.out.println("does not contain data for this deadline!");
        return -1;
    }

    public void addCMD(String cmd,String param,long in_deadline) {
        HashMap<String, TranscodingRequest> acmdRecord;
        //System.out.println("call addcmd"+Command+" "+Setting);

        if (!cmdSet.containsKey(cmd)) { // operation didn't existed
            acmdRecord = new HashMap<String, TranscodingRequest>();
            cmdSet.put(cmd,acmdRecord);
        }else{
            acmdRecord=cmdSet.get(cmd);
        }

        if (!acmdRecord.containsKey(param)) { //parameter didn't existed
            acmdRecord.put(param,new TranscodingRequest(cmd,param,in_deadline,null));
            requestcount++;
        } else {    //any update? new individual deadline?
            System.out.println("addCMD: already have this cmd");
            acmdRecord.get(param).chkUpdatedeadline(in_deadline);
        }

        if (in_deadline < deadLine) { //if deadline is earlier than current most urgent, set new deadline
            deadLine = in_deadline;
        }
    }

    public void getAllCMD(StreamGOP aGOP){
        for (String command : aGOP.cmdSet.keySet()) {
            for (String aparam : aGOP.cmdSet.get(command).keySet()) {
                addCMD(command, aparam,aGOP.getdeadlineof(command,aparam));
                //System.out.println("a call to add cmd");
            }
        }
    }

    private double priority;

    @Override
    public int compareTo(StreamGOP t1) {
        double diff=this.priority-t1.getPriority();
        if(diff<0){
            return -1;
        }else if(diff>0){
            return 1;
        } else{
            return 0;
        }
    }
    public double getPriority()
    {
        return priority;
    }
    public void setPriority(double priority)
    {
        this.priority = priority;
    }
    //need to re
    public String outputDir() {
        //return System.getProperty("user.dir") + "./webapps/CVSS_Implementation_war_exploded/repositoryvideos/" + videoname + "/out.m3u8";
        //project dir
        //return CVSE.config.path + "streams/" + videoname;
        //local web demo
        return CVSE.config.outputDir + videoname + videoSetting.type + videoSetting.settingNum + File.separator;
        //return "/var/www/html/2019WebDemo/streams/" + videoname;
    }

    public String outputFile(){
        return CVSE.config.outputDir + videoname + videoSetting.type + videoSetting.settingNum + File.separator + segment + ".ts";
    }

    public String toString()
    {
        return "estimatedExeT: "+estimatedExecutionTime+"estimatedExeSD: "+estimatedExecutionSD+"deadline: "+deadLine+" "+cmdSet.toString();
    }
    public void print(){

    }
}