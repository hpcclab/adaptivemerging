package Scheduler;

import Streampkg.Stream;
import Streampkg.StreamGOP;
import mainPackage.CVSE;
import miscTools.TaskQueue;


// mergable GOPTaskScheduler, this is just mostly pairing merger class to GOPTaskScheduler.
// currently pending queue is not really used
public class GOPTaskScheduler_mergable extends GOPTaskScheduler_common {

    public Merger MRG;
    protected TaskQueue pendingqueue; //keep track of pending task (submitted, but didn't)


    public double SDco=2;
    private long oversubscriptionlevel;
    public GOPTaskScheduler_mergable(){
        super();
        pendingqueue = new TaskQueue();
        MRG= new Merger(Batchqueue,pendingqueue, machineInterfaces);
    }

    public boolean emptyQueue() {
        if (Batchqueue != null && pendingqueue != null) {
            return (Batchqueue.isEmpty() || pendingqueue.isEmpty());
        }
        return true;
    }

    //bloated version of addStream, check duplication and similarity first
    public void addStream(Stream ST){
        CVSE.AC.AssignStreamPriority(ST);
        //Batchqueue.addAll(ST.streamGOPs); // can not just mass add without checking each piece if exist
        for(StreamGOP X:ST.streamGOPs) {
            if (!CVSE.CACHING.checkExistence(X)) {
                if (CVSE.config.taskmerge) {
                    MRG.mergeifpossible(X, SDco);
                } else {
                    //dont merge check
                    synchronized (Batchqueue) {
                        Batchqueue.add(X);
                    }
                }
            }else{
                System.out.println("GOP cached, no reprocess");
            }
        }
            //assignwork thread start
            //taskScheduling();
    }
    //function that do something before task X get sent
    protected void preschedulefn(StreamGOP X){
        synchronized (pendingqueue) {
            pendingqueue.add(X);
        }
    }
    //function that do something after task X get sent
    protected void postschedulefn(StreamGOP X){
        //System.out.println("overwritten postschedulefn is CALLED\n\n\n");

        //either use function below, or dataUpdate's ackCompletedVideo, BUT NOT BOTH

        if(CVSE.config.taskmerge) {
            MRG.removeStreamGOPfromTables(X);
        }
        //////code above works better for simulation but for real production dataUpdate's ackCompletedVideo should be the one to report (commented out at the moment)

    }
}