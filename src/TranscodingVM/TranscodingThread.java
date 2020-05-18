package TranscodingVM;

import Scheduler.SystemConfig;
import Streampkg.StreamGOP;
import mainPackage.CVSE;
import miscTools.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//import com.amazonaws.services.s3.AmazonS3;

public class TranscodingThread extends Thread{
    public String type;
    public BlockingQueue<StreamGOP> jobs = new LinkedBlockingQueue<StreamGOP>();
    //or
    //public BlockingQueue<StreamGOP> jobs = new PriorityBlockingQueue<>();
    public ConcurrentHashMap<String, Tuple<Long,Integer>> runtime_report=new ConcurrentHashMap<>(); //setting identifier number, < average, count>
    public int workDone; //count each work as one
    public int NworkDone; //count each work as suggested in StreamGOP.requestcount
    public int deadlineMiss,NdeadlineMiss;
    long requiredTime; //TODO: make sure all these are thread safe, maybe block when add new item to the queue
    long synctime=0; //spentTime+requiredTime is imaginary total time to clear the queue
    long realspentTime=0; //realspentTime is spentTime without Syncing
    public List<StreamGOP> completedTask=new LinkedList<StreamGOP>();
    public String VM_class;
    private Random r=new Random();

    private void TranscodeSegment()
    {
        int i=0;
        int exit=0;
        long delay=0;
        while(true) {
            long savedTime=System.nanoTime()/1000000;
            try{
                StreamGOP aStreamGOP = jobs.poll(1, TimeUnit.MINUTES);
                if(aStreamGOP!=null) {
                    if (aStreamGOP.cmdSet.containsKey("shutdown")) {
                        exit = 1;
                        System.out.println("VM's queue is empty and receiving shutting down command");
                        break;
                    }

                    System.out.println("In TranscodeSegment of transcoding thread");

                 if (CVSE.config.addProfiledDelay) {
                        //System.out.println("est="+aStreamGOP.estimatedExecutionTime+" sd:"+aStreamGOP.estimatedExecutionSD);
                        delay = (long) (aStreamGOP.estimatedExecutionTime + aStreamGOP.estimatedExecutionSD * r.nextGaussian());
                    }

                    //System.out.println(aStreamGOP.getPath());
                    String filename = aStreamGOP.getPath().substring(aStreamGOP.getPath().lastIndexOf("/") + 1, aStreamGOP.getPath().length());
                    //extra line for windows below, need test if work with linux
                    filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.length());
                    System.out.println("Segment name: " + aStreamGOP.segment);
                    System.out.println("Segment output directory: " + aStreamGOP.videoSetting.outputDir());
                    String[] command;
                    String bashdir="";
                    if(aStreamGOP.videoSetting.type.equals("resolution")){
                        bashdir="bash/resize.sh";
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.resWidth, aStreamGOP.videoSetting.resHeight, aStreamGOP.videoSetting.outputDir(), filename};
                    }else if(aStreamGOP.videoSetting.type.equals("framerate")){
                        bashdir="bash/framerate.sh";
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.resWidth, aStreamGOP.videoSetting.resHeight, aStreamGOP.videoSetting.outputDir(), filename};
                    }else if(aStreamGOP.videoSetting.type.equals("bitrate")) {
                        bashdir="bash/bitrate.sh";
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.resWidth, aStreamGOP.videoSetting.resHeight, aStreamGOP.videoSetting.outputDir(), filename};
                    }else if(aStreamGOP.videoSetting.type.equals("codec")){
                        bashdir="bash/codec.sh";
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.resWidth, aStreamGOP.videoSetting.resHeight, aStreamGOP.videoSetting.outputDir(), filename};
                    }else if(aStreamGOP.videoSetting.type.equals("blackwhite")) {
                        bashdir="bash/bw.sh";
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.outputDir(), filename};
                        System.out.println(CVSE.config.path + bashdir);
                        System.out.println(CVSE.config.path + aStreamGOP.getPath());
                        System.out.println(aStreamGOP.videoSetting.outputDir());
                        System.out.println(filename);
                    }
                    else{
                        System.out.println("Unknown Command");
                        command = new String[]{"bash", CVSE.config.path + bashdir, CVSE.config.path + aStreamGOP.getPath(), aStreamGOP.videoSetting.resWidth, aStreamGOP.videoSetting.resHeight, aStreamGOP.videoSetting.outputDir(), filename};
                    }

                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen
                    try {
                        Process p = pb.start();
                        p.waitFor();

                    } catch (Exception e) {
                        System.out.println("Did not execute bashfile :(");
                    }



                    System.out.println("finished a segment");
                    //time calculations
                    long finishedtime=System.nanoTime()/1000000;
                    long timespan=finishedtime-savedTime;
                    synctime=finishedtime;
                    realspentTime+=timespan;
                    workDone++;
                    if(finishedtime>aStreamGOP.deadLine){
                        //MISS
                        deadlineMiss++;
                    }else{
                        //ONTIME //TODO: make stat count work for merged task too.

                    }

                    completedTask.add(aStreamGOP); //mark task as finished
                    //put to S3
                        /* //EC2
                        if (useS3) {
                            File file = new File(aStreamGOP.userSetting.outputDir() + "/" + filename);
                            //System.out.println("from "+"output "+aStreamGOP.userSetting.outputDir()+"/"+filename);
                            //System.out.println("from "+"output "+aStreamGOP.userSetting.outputDir().substring(aStreamGOP.userSetting.outputDir().lastIndexOf("/"),aStreamGOP.userSetting.outputDir().length())+"/"+filename);
                            if (file.exists()) {
                                mainPackage.S3Control.PutFile(bucketName, "output" + aStreamGOP.userSetting.outputDir().substring(aStreamGOP.userSetting.outputDir().lastIndexOf("/"), aStreamGOP.userSetting.outputDir().length()) + "/" + filename, file, s3);
                            } else {
                                System.out.println("tried to upload nonexist file");
                            }
                            file.delete();
                        }
                        ///*/
                    /*  //TC
                        if (delay != 0) {
                            sleep(delay);
                        }
                        if(System.currentTimeMillis()>aStreamGOP.deadLine){ //don't support counting for merging task's individual deadline evaluation yet
                            System.out.println("DEADLINE missed (realmode)"+System.currentTimeMillis()+" "+aStreamGOP.deadLine );
                            deadlineMiss++;
                            NdeadlineMiss+=aStreamGOP.requestcount;
                        }
                        elapsedTime = System.nanoTime()/1000000 - savedTime;
                        synctime+=elapsedTime;
                        realspentTime+=elapsedTime;


                }else{
                        elapsedTime=delay;
                        //System.out.println("delay="+delay);
                        synctime+=elapsedTime;
                        realspentTime+=elapsedTime;
                        boolean missed=false;
                        for (String cmd:aStreamGOP.deadlineSet.keySet()){
                            if(aStreamGOP.getdeadlineof(cmd)<=synctime){
                                if(!missed) {
                                    deadlineMiss++;
                                    missed=true;
                                }
                                NdeadlineMiss++;
                            }
                        }
                    }

                    //it's done, reduce estimationTime
                    this.requiredTime-=aStreamGOP.estimatedExecutionTime;
                    //get RunTime, reduce from nano to millisecond
                    workDone++;
                    NworkDone+=aStreamGOP.requestcount;
                    //runtime_report ?
                } catch (InterruptedException e) {
                e.printStackTrace();
            }

            */
                }else{
                    System.out.println("A thread wait 1 minute without getting any works!");
                    System.out.println("total spentTime= "+realspentTime);
                }
            } catch (Exception e) {
                System.out.println("Thread Error:" +e.getMessage());
            }
        }
        if(exit!=0){
            //receive exit command, now what?
        }
    }

    public void run(){
        TranscodeSegment();
    }

}