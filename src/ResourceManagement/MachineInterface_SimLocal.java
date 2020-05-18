package ResourceManagement;

import Streampkg.StreamGOP;
import mainPackage.CVSE;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//does not actually have a socket, this code is the combination of transcodingVM, transcodingthread and interface
//Warning: this mode is immediate complete but the estimatedQueuelength and estimatedExecutionTime still needs to be there for compatibility
public class MachineInterface_SimLocal extends MachineInterface {
    //interface parameters are in MachineInterface

    //pseudo thread's parameters
    private Random r=new Random();
    private long node_synctime =0; //spentTime+requiredTime is imaginary total time to clear the queue
    private long node_realspentTime =0; //realspentTime is spentTime without Syncing
    private long node_aftersync_itemdone=0,node_aftersync_itemmiss=0; //these need total sum
    private long node_aftersync_taskdone=0,node_aftersync_taskmiss=0;


    public int node_focus_task=10,node_statindex=0;
    private long node_missArr[] =new long[50]; private long node_miss; //put 50 as current max
    //use node_focus_task as N work done
    private long node_undertimeArr[] =new long[50]; //negative num for overtime
    private long node_sum_undertime;
    private long node_sum_overtime;
    private double node_wundertimeArr[] =new double[50]; //negative num for overtime
    private double node_sum_wundertime;
    private double node_sum_wovertime;
    public int mergeEffectedMiss=0;
    public List<StreamGOP> completedTask=new LinkedList<StreamGOP>();
    /*
    private int l_workDone; //count each work as one
    private int l_NworkDone; //count each work as suggested in StreamGOP.requestcount
    private int l_deadlineMiss,l_NdeadlineMiss;
    private long l_overtime=0;
    private double l_overtime_weighted=0;
    private long l_undertime=0;
    private double l_undertime_weighted=0;
    */

    public MachineInterface_SimLocal(String vclass, int iport, int inid, boolean iautoschedule) {
        super(vclass,iport,inid,iautoschedule);
        status=1;
    }

    public boolean isWorking(){
        return status==1;
    }
    //push in the data
    public  boolean sendJob(StreamGOP segment){
        segment.setPath(segment.getPath().replaceAll("\\\\","/"));
        estimatedQueueLength++;
        estimatedExecutionTime += segment.estimatedExecutionTime;
        //simulate time
        double exetime=0;
        if(CVSE.config.addProfiledDelay) {
            //System.out.println("est="+segment.estimatedExecutionTime+" sd:"+segment.estimatedExecutionSD);
            exetime=(long) (segment.estimatedExecutionTime+segment.estimatedExecutionSD*r.nextGaussian());
        }
        //System.out.println("delay="+delay);
        node_synctime +=exetime;
        node_realspentTime +=exetime;
        //System.out.println("synctime="+synctime);
        //System.out.println("realspentTime="+realspentTime);
        boolean missed=false;
        for (String cmd:segment.cmdSet.keySet()){
            for(String param:segment.cmdSet.get(cmd).keySet()) {
            double slackleft = segment.getdeadlineof(cmd,param) - node_synctime;
            long slacktime = (segment.getdeadlineof(cmd,param) - segment.arrivalTime);

            node_miss -= node_missArr[node_statindex]; //desum old miss record, if missed
            node_missArr[node_statindex] = 0; //reset the miss record
            //System.out.println("node_statindex="+node_statindex+" diff="+slackleft+" slacktime="+slacktime);
            //desum undertime
            if (node_undertimeArr[node_statindex] < 0) {// miss
                node_sum_overtime -= node_undertimeArr[node_statindex];
                node_sum_wovertime -= node_wundertimeArr[node_statindex];
            } else {
                node_sum_undertime -= node_undertimeArr[node_statindex]; //desum
                node_sum_wundertime -= node_wundertimeArr[node_statindex];
            }

            if (slackleft < 0) {
                if( (segment.flags&1)==1) {
                    mergeEffectedMiss++;
                    System.out.println("a task miss because of merging");
                }
                if (!missed) { //havent miss before
                    node_aftersync_itemmiss++;
                    missed = true;
                }
                node_missArr[node_statindex]++; //it miss, so count
                node_aftersync_taskmiss++;

                if (slacktime != 0) {
                    node_sum_overtime -= (long) slackleft;
                    node_sum_wovertime -= slackleft / slacktime;
                } else {
                    System.out.println("ERROR: Time since dispatch=0");
                }
                node_miss += node_missArr[node_statindex];
            } else {
                if (slacktime != 0) {
                    double usefulslack = slacktime - exetime;
                    node_sum_undertime += (long) slackleft;
                    node_sum_wundertime += slackleft / usefulslack;
                } else {
                    System.out.println("ERROR: Time since dispatch=0");
                }
            }
            //System.out.println("node_sum_undertime="+node_sum_undertime+" node_sum_overtime="+node_sum_overtime);
            //System.out.println("node_sum_wundertime="+node_sum_wundertime+" node_sum_wovertime="+node_sum_wovertime);
            node_undertimeArr[node_statindex] = (long) slackleft; //set undertime
            node_wundertimeArr[node_statindex] = slackleft / slacktime;
            node_statindex = (node_statindex + 1) % node_focus_task;
        }
        }
        node_aftersync_itemdone++;
        //System.out.println("request count="+segment.requestcount);
        node_aftersync_taskdone +=segment.requestcount;
        completedTask.add(segment);
        //
        return false;
    }
    //get back the runtime stat
    public void dataUpdate(){
        System.out.println("send Data Update");
        //Sync time
        if(CVSE.GTS.maxElapsedTime> node_synctime){
            //System.out.println("node sync time forward "+synctime +"-> "+CVSE.GTS_mergable.maxElapsedTime);
            node_synctime =CVSE.GTS.maxElapsedTime;
        }
        // change to using the circular, tmp



        //System.out.println("dataUpdate");
        CVSE.GTS.workpending-=(estimatedQueueLength);

         //we completed the scheduling and execution
        CVSE.GTS.machineInterfaces.get(id).estimatedQueueLength = 0;
        CVSE.GTS.machineInterfaces.get(id).estimatedExecutionTime = 0;
        CVSE.GTS.machineInterfaces.get(id).elapsedTime= node_synctime;
        CVSE.GTS.machineInterfaces.get(id).actualSpentTime= node_realspentTime;
        //System.out.println("actualSpentTime="+CVSE.GTS_mergable.machineInterfaces.get(id).actualSpentTime+" realspentTime="+realspentTime);
        //TimeEstimator.updateTable(this.id, answer.runtime_report); //disable for now, broken

        CVSE.GTS.machineInterfaces.get(id).total_taskmiss +=node_aftersync_itemmiss;
        CVSE.GTS.machineInterfaces.get(id).total_requestmiss += node_aftersync_taskmiss;
        CVSE.GTS.machineInterfaces.get(id).total_taskdone += node_aftersync_itemdone;
        CVSE.GTS.machineInterfaces.get(id).total_requestdone += node_aftersync_taskdone;
        CVSE.GTS.machineInterfaces.get(id).tmp_taskdone = node_aftersync_taskdone;
        CVSE.GTS.machineInterfaces.get(id).tmp_taskmiss = node_aftersync_taskmiss;
        node_aftersync_itemmiss=node_aftersync_itemdone=node_aftersync_taskdone=node_aftersync_taskmiss=0;


        CVSE.GTS.machineInterfaces.get(id).tmp_overtime =node_sum_overtime/node_focus_task;
        CVSE.GTS.machineInterfaces.get(id).tmp_undertime =node_sum_undertime/node_focus_task;

        CVSE.GTS.machineInterfaces.get(id).tmp_weighted_overtime =node_sum_wovertime/node_focus_task;
        CVSE.GTS.machineInterfaces.get(id).tmp_weighted_undertime =node_sum_wundertime/node_focus_task;

        CVSE.VMP.ackCompletedVideo(completedTask);
        completedTask.clear();
        //data are self expired, no need to reset or resum
    }
    //shut it down, do nothing
    public  boolean sendShutdownmessage(){
        return true;
    }
    //shut it down, do nothing
    public void close(){}
    }
