package ResourceManagement;

import Streampkg.StreamGOP;

import java.util.HashMap;

public abstract class MachineInterface {


    public int id;
    public String VM_class;
    public HashMap<String,String> properties;
    protected int status;
    public int estimatedQueueLength=0;
    public long estimatedExecutionTime=0;
    public long elapsedTime=0;
    public long actualSpentTime=0;
    public int port;
    public boolean autoschedule=false;

    //stats
    public long tmp_taskdone=0, total_requestdone =0, total_taskdone =0; //work done can be multiple task per item
    public long tmp_taskmiss=0, total_requestmiss =0, total_taskmiss =0;
    public long tmp_overtime =0; // no need for total
    public long tmp_undertime =0;
    public double tmp_weighted_overtime =0,tmp_weighted_undertime =0;

    public MachineInterface(){
    }

    public MachineInterface(String vclass, int iport, int inid, boolean iautoschedule){
            this.VM_class=vclass;
            id=inid;
            port=iport;
            autoschedule=iautoschedule;
    }
    public boolean isWorking(){
        return status==1;
    }

    public abstract boolean sendJob(StreamGOP segment);
    public void addOperation(Operations.simpleoperation newOP){} //introduce new operation to the system, interface for future feature, do nothing for now
    public abstract void dataUpdate();
    public abstract boolean sendShutdownmessage();
    public void close(){}

}