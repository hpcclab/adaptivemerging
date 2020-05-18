package ResourceManagement;

import Streampkg.StreamGOP;
import mainPackage.CVSE;

import java.util.Random;

//does not actually have a socket, this code is simulating caching through Network, it has bandwidth and latency
//currently each file have fixed size, rather than having a transfer size per GOP
public class MachineInterface_SimNWcache extends MachineInterface {
    //interface's parameters
    //pseudo thread's parameters
    private int workDone; //count each work as one
    private int NworkDone; //count each work as suggested in StreamGOP.requestcount
    private int deadlineMiss,NdeadlineMiss;
    private long synctime=0; //spentTime+requiredTime is imaginary total time to clear the queue
    private long realspentTime=0; //realspentTime is spentTime without Syncing
    //
    private long bandwidth=250; // unit here is MB, MegaByte per seconds, file size unit =KB but time unit is millisecond
    // , so kilo vs mega compensate for the millisecond unit
    private int delay_mean=20; //milliseconds
    private double delay_SD=3.0;
    //
    //
    private Random r=new Random();



    public void setBandwidth(long bandwidth) { //unit received is megabit, so convert to Megabyte
        this.bandwidth = bandwidth/8;
    }
    public long getBandwidth() { //convert back to Megabits
        return bandwidth*8;
    }
    public MachineInterface_SimNWcache(String vclass, int iport, int inid, boolean iautoschedule) {
        super(vclass,iport,inid,iautoschedule);
        setBandwidth(iport);
        status=1;
    }

    //not used right now,
    public MachineInterface_SimNWcache(String vclass, int inid, boolean iautoschedule, int ibandwidth, int idelay_mean, double idelay_SD) {
        super(vclass,ibandwidth,inid,iautoschedule);
        setBandwidth(ibandwidth);
        delay_mean=idelay_mean;
        delay_SD=idelay_SD;
        estimatedExecutionTime=(long)(delay_mean+delay_SD); //always have delay of a task in time estimation
    }
    public boolean isWorking(){
        return status==1;
    }
    //push in the data
    public  boolean sendJob(StreamGOP segment){
        double sampleddelay=(long) (delay_mean+delay_SD*r.nextGaussian());
        double transfertime=(long)bandwidth/segment.fileSize;
        segment.setPath(segment.getPath().replaceAll("\\\\","/"));
        estimatedQueueLength++;
        estimatedExecutionTime += transfertime;
        //simulate time
        System.out.println("delay="+sampleddelay);
        synctime+=transfertime;
        realspentTime+=transfertime;
        //System.out.println("synctime="+synctime);
        //System.out.println("realspentTime="+realspentTime);
        boolean missed=false;
        //if transfer finished after synctime+delay, it misses
        for (String cmd:segment.cmdSet.keySet()){
            for(String param:segment.cmdSet.get(cmd).keySet()) {
                if (segment.getdeadlineof(cmd, param) <= synctime + sampleddelay) {
                    if (!missed) {
                        deadlineMiss++;
                        missed = true;
                    }
                    NdeadlineMiss++;
                }
            }
        }
        workDone++;
        NworkDone+=segment.requestcount;
        //
        return true;
    }
    //get back the runtime stat
    public  void dataUpdate(){
        //Sync time
        if(CVSE.GTS.maxElapsedTime>synctime){
            //System.out.println("node sync time forward "+synctime +"-> "+CVSE.GTS_mergable.maxElapsedTime);
            synctime=CVSE.GTS.maxElapsedTime;
        }
        //
        //System.out.println("dataUpdate");
        CVSE.GTS.workpending-=(estimatedQueueLength);
        //we completed the scheduling and execution, reset values
        CVSE.GTS.machineInterfaces.get(id).estimatedQueueLength = 0;
        CVSE.GTS.machineInterfaces.get(id).estimatedExecutionTime = (long)(delay_mean+delay_SD);
        CVSE.GTS.machineInterfaces.get(id).elapsedTime=synctime;
        CVSE.GTS.machineInterfaces.get(id).actualSpentTime=realspentTime;
        //System.out.println("actualSpentTime="+CVSE.GTS_mergable.machineInterfaces.get(id).actualSpentTime+" realspentTime="+realspentTime);
        //TimeEstimator.updateTable(this.id, answer.runtime_report); //disable for now, broken

        CVSE.GTS.machineInterfaces.get(id).total_taskmiss =deadlineMiss;
        CVSE.GTS.machineInterfaces.get(id).total_taskdone =workDone;
        CVSE.GTS.machineInterfaces.get(id).total_requestdone =NworkDone;
        CVSE.GTS.machineInterfaces.get(id).total_requestmiss =NdeadlineMiss;
    }
    //shut it down, do nothing
    public  boolean sendShutdownmessage(){
        return true;
    }
    //shut it down, do nothing
    public void close(){}
}
