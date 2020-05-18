package Scheduler;

import ResourceManagement.MachineInterface;
import Streampkg.StreamGOP;
import Streampkg.TaskRequest;
import TimeEstimatorpkg.retStat;
import mainPackage.CVSE;

import java.util.*;

public class Merger {

    private  HashMap<TaskRequest,StreamGOP> mergePending_tasklvl =new HashMap<Streampkg.TaskRequest,StreamGOP>();;
    private  HashMap<Streampkg.TaskRequest,StreamGOP> mergePending_operationlvl =new HashMap<Streampkg.TaskRequest,StreamGOP>();
    private  HashMap<Streampkg.TaskRequest,StreamGOP> mergePending_datalvl =new HashMap<Streampkg.TaskRequest,StreamGOP>();
    public  long merged_tasklvl_count =0;
    public  long probecounter=0;
    private miscTools.TaskQueue pendingqueue;
    private miscTools.TaskQueue Batchqueue;
    public  ArrayList<MachineInterface> machineInterfaces;
    public Merger(miscTools.TaskQueue bq,miscTools.TaskQueue pq,ArrayList<MachineInterface> vi) {
        Batchqueue=bq;
        pendingqueue=pq;
        machineInterfaces =vi;
    }

    //binary search version, only work if original miss ==0
    public int Bsearch_trybetweenPositions(Streampkg.TaskRequest key,HashMap<Streampkg.TaskRequest,StreamGOP> thislvlmap,StreamGOP X,StreamGOP original,StreamGOP merged,int originalmiss) {
        miscTools.TaskQueue newVQ = new miscTools.TaskQueue(Batchqueue);
        GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
        int firstpos=0;
        newVQ.remove(original);

        int newfirstpos=firstpos,searchlimit=newVQ.size()-1;
        while(newfirstpos<searchlimit-1){
            int tryposition=(searchlimit+newfirstpos)/2;
            newVQ.add(tryposition,merged);
            int test=countDLMiss( newVQ,merged,Integer.MAX_VALUE,GTS.SDco);
            if(Math.abs(test)<originalmiss){
                System.out.println("found a position");
                return tryposition; //found perfect condition
            }
            if(test<-1){
                return -1; //both merged one and other have miss their deadline, no perfect exist
            }
            if(test==-1){
                newfirstpos=tryposition; //x need to move forward
            }else{
                searchlimit=tryposition; // x need to move backward
            }
            newVQ.remove(tryposition); //it fail, so remove this
        }
        return -1;
    }
    public int linearsearch_trybetweenPositions(Streampkg.TaskRequest key,HashMap<Streampkg.TaskRequest,StreamGOP> thislvlmap,StreamGOP X,StreamGOP original,StreamGOP merged,int originalmiss) {
        miscTools.TaskQueue newVQ = new miscTools.TaskQueue(Batchqueue);
        GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
        //put merged to latest position (at the moment)
        newVQ.remove(original);
        //get latest position for X not to miss their deadline
        int latestpos=findlatestposition(newVQ, merged); //note, we won't have merged task in the queue
        System.out.println("latestpos="+latestpos);
        //put merged to position that it'll not miss
        newVQ.add(latestpos,merged); //should we test +1??
        long check=countDLMiss( newVQ,Math.abs(originalmiss),GTS.SDco);
        System.out.println(check +" vs "+originalmiss);
        if ( Math.abs(check)<= Math.abs(originalmiss)) {
            //make change to real queue
            System.out.println("merged");
            Batchqueue.remove(original); //remove from old position
            original.getAllCMD(X);
            Batchqueue.add(latestpos,original); //re add at new specific position
            markEffected(original);
            return latestpos;
        }
        return -1;
    }


    private int virtualQueueCheckReplace(StreamGOP Original,StreamGOP merged,int threshold){
        GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
        miscTools.TaskQueue newVQ = new miscTools.TaskQueue(Batchqueue);
        int indexofOriginal=newVQ.indexOf(Original);
        if(indexofOriginal<0||indexofOriginal>newVQ.size()){
            System.out.println("anomaly in virtualQueueCheckReplace index="+indexofOriginal);
            return -999;
        }else {
            newVQ.set(newVQ.indexOf(Original), merged); //can not find original???
            return countDLMiss(newVQ, null,threshold,GTS.SDco);
        }
    }

    private MachineInterface v_selectMachine(StreamGOP x, int[] queuelength, long[] executiontime){

        GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
        //System.out.println("select machine using "+CVSE.config.scheduler_machineselectionpolicy);

        if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("MCT")){ //Minimum Completion Time
            //use SDco or 2 ??
            return GTS.simplemachineselect(x,queuelength,executiontime,"MCT",2,false);
        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("SJF")){  //Shortest Job(queue) First
            return GTS.simplemachineselect(x,queuelength,executiontime,"SJF",2,false);
        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("MET")){  //Minimum Execution time First
            return GTS.simplemachineselect(x,queuelength,executiontime,"MET",2,false);

        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("SQL")){ //Shortest Queue Length,
            // don't need time estimator
            return GTS.ShortestQueueLength(x,queuelength,executiontime,2,false);
        }else{
            //what should be a default? SQL maybe?
            return GTS.ShortestQueueLength(x,queuelength,executiontime,2,false);
        }
    }
    private int countOriginalMiss(StreamGOP X,double SDco){
        miscTools.TaskQueue newVQ = new miscTools.TaskQueue(Batchqueue);
        newVQ.add(X);
        //System.out.println("countOriginalMiss");
        //System.out.println(newVQ.toArray()[0]);

        return countDLMiss(newVQ,Integer.MAX_VALUE,SDco);

    }
    private int countDLMiss(miscTools.TaskQueue virtualQueue,int rthreshold,double SDco) {
        return countDLMiss(virtualQueue,null,rthreshold,SDco);
    }

    private void fillEstimatorArray(int[] ql,long[] et){
        for(int i = 0; i< machineInterfaces.size(); i++){ //this queuelength of already assigned tasks use SD=2
            ql[i]= machineInterfaces.get(i).estimatedQueueLength;
            et[i]= machineInterfaces.get(i).estimatedExecutionTime;
        }
    }
    //return 0 for no miss, x for x missed deadline
    private int countDLMiss(miscTools.TaskQueue virtualQueue,StreamGOP target,int rthreshold,double SDco){
        // perform check
        int missed=0,targetmiss=0;
        int threshold=Math.abs(rthreshold);
        miscTools.TaskQueue virtualQueue_copy = new miscTools.TaskQueue(virtualQueue);
        //copy virtual queue
        int[] queuelength=new int[machineInterfaces.size()];
        long[] executiontime=new long[machineInterfaces.size()];
        fillEstimatorArray(queuelength,executiontime);
        //System.out.println("count Original miss function");

        for(int i=0;i<virtualQueue_copy.size();i++){
            probecounter++;
            //get a GOP

            StreamGOP aGOP=virtualQueue_copy.removeDefault();
            //get a machine
            //System.out.println("got GOP");
            MachineInterface machine= v_selectMachine(aGOP,queuelength,executiontime);
            //System.out.println("got machine");
            int machine_index= machineInterfaces.indexOf(machine);
            //System.out.println("got interface");
            //update our queue
            retStat thestat=CVSE.TE.getHistoricProcessTime(machine,aGOP);
            //System.out.println("got stat");
            executiontime[machine_index]+=thestat.mean+thestat.SD*SDco; //thestat.SD;
            queuelength[machine_index]++;
            long finishTimeofX= executiontime[machine_index];
            if(finishTimeofX>aGOP.deadLine){
                missed++;
                if(aGOP==target){
                    targetmiss=1;
                }
                if(missed>threshold){
                    //miss over the limit, no need to consider further
                    return (1-targetmiss*2)*missed; //1 for no target miss, -1 for target miss
                }
            }
        }
        //System.out.println("count DL miss return");

        return missed;
    }

    //find latest position possible to inject task to the unsorted queue
    private int findlatestposition(miscTools.TaskQueue  virtualQueue,StreamGOP themergedtask){

        // perform check
        miscTools.TaskQueue virtualQueue_copy = new miscTools.TaskQueue(virtualQueue);
        //copy virtual queue
        int[] queuelength=new int[machineInterfaces.size()];
        long[] executiontime=new long[machineInterfaces.size()];
        fillEstimatorArray(queuelength,executiontime);
        double tmpSDCO=0;
        for(int i=0;i<virtualQueue_copy.size();i++) {
            probecounter++;
            // try themergedtask first, if fail then return
            MachineInterface machine = v_selectMachine(themergedtask, queuelength, executiontime);
            int machine_index = machineInterfaces.indexOf(machine);
            retStat thestat=CVSE.TE.getHistoricProcessTime(machine,themergedtask);
            if(executiontime[machine_index]+thestat.mean+thestat.SD*tmpSDCO>themergedtask.deadLine){
                return i-1; //can be -1 (miss on first position)
            }

            if(i==0){
                //after first position evaluation, use SDco=2
                tmpSDCO=2;
            }
            //not missing, therefore assign a new task
            //get a GOP
            StreamGOP aGOP = virtualQueue_copy.removeDefault();
            //get a machine
            machine = v_selectMachine(aGOP, queuelength, executiontime);
            machine_index = machineInterfaces.indexOf(machine);
            //update our queue
            thestat = CVSE.TE.getHistoricProcessTime(machine, aGOP);
            executiontime[machine_index] += thestat.mean+thestat.SD*tmpSDCO; // use SDco=
            queuelength[machine_index]++;
            long finishTimeofX = executiontime[machine_index];
            if (finishTimeofX > aGOP.deadLine) {
                //do nothing for now
            }
        }
        return virtualQueue_copy.size()-1; // nowhere is missed
    }

    public int removeValuefromHashMap(HashMap H,StreamGOP X){
        int count = 0;
        synchronized (H) {
            Iterator<Map.Entry> iterator = H.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                if (entry.getValue() == X) {
                    iterator.remove();
                    count++;
                }
            }
        }
        return count;
    }

    public void removeStreamGOPfromTables(StreamGOP X){
        //remove anything with this value (need removeAll, not remove or it'll only remove the first one)

        //The Way below can cause errors, if removing multiple objects
        /*
        mergePending_tasklvl.values().removeAll(Collections.singleton(X));
        mergePending_datalvl.values().removeAll(Collections.singleton(X));
        */
        //testing this
        removeValuefromHashMap(mergePending_operationlvl,X);
        removeValuefromHashMap(mergePending_datalvl,X);

    }
    public void updateTable(){

    }

    public boolean trymerge(StreamGOP X,int originalmiss,Streampkg.TaskRequest requestSig,HashMap<Streampkg.TaskRequest,StreamGOP> LVmap_pending) {

        StreamGOP itspair = LVmap_pending.get(requestSig);
        System.out.println("try merge");
        if (itspair.dispatched) {
            System.out.println("too late to merge, pair already dispatched");
            return false;
        } else {
            //System.out.println("not too late to merge");
            // create merged StreamGOP
            StreamGOP merged = new StreamGOP(itspair); //create a copy of the old one for evaluation, but don't use this object
            merged.getAllCMD(X); //may already contain some msg
            // do merging
            if(!CVSE.config.mergeOverwriteQueuePolicy){ //if we obey queuing policy
                long checked = 0;
                if(!CVSE.config.mergeaggressiveness.equalsIgnoreCase("Aggressive")){ //if we want to evaluate merge impact...
                    checked = virtualQueueCheckReplace(itspair, merged, Math.abs(originalmiss)); //assume direct replace to the object
                }
                /*
                System.out.println(checked + " vs " + originalmiss);
                retStat chkmerged = TimeEstimator.getHistoricProcessTime(CVSE.config.CR_class.get(0), merged);
                System.out.println("runtime merged:" + chkmerged.mean + "(" + chkmerged.SD + ")");
                retStat chkX = TimeEstimator.getHistoricProcessTime(CVSE.config.CR_class.get(0), X);
                System.out.println("runtime X:" + chkX.mean + "(" + chkX.SD + ")");
                retStat chkoriginal = TimeEstimator.getHistoricProcessTime(CVSE.config.CR_class.get(0), itspair);
                System.out.println("runtime itspair:" + chkoriginal.mean + "(" + chkoriginal.SD + ")");
                */
                if (Math.abs(checked) <= Math.abs(originalmiss)) { //worth it, merge!
                    itspair.getAllCMD(X); //reuse itspair object, add new parameters
                    System.out.println("Merge");
                    //don't update execution time yet, lets do that on dispatch event
                    /*
                    if(...){ //if task need to be reinsert to resort the queue
                        Batchqueue.remove(itspair);
                        Batchqueue.add(itspair);
                    }
                    */
                    // validation, count how many tasks are effected by the merging
                    markEffected(itspair);
                    return true;
                }else{
                    System.out.println("no Merge");
                    return false;
                }
            }else{
                System.out.println("try merge with overwriting queue positioning, work with FIFO queue only");
                if(!CVSE.config.mergeaggressiveness.equalsIgnoreCase("Aggressive")){
                    ///////////////////////////////////////////// redo code both upper and below
                    if (CVSE.config.overwriteQueuePolicyHeuristic.equalsIgnoreCase("logarithmic")) {
                        // logarithmic probe
                        if (Bsearch_trybetweenPositions(requestSig, LVmap_pending, X, itspair, merged, originalmiss) != -1) {
                            return true;
                        }
                    }else { //default to linearProbe
                        if (linearsearch_trybetweenPositions(requestSig, LVmap_pending, X, itspair, merged, originalmiss) != -1) {
                            return true;
                        }
                    }
                }else{ // incase of aggressive merge, just merge right away
                    System.out.println("merge");
                    itspair.getAllCMD(X);
                    return true;
                }
            }
        }
        System.out.println("don't merge");
        return false;
    }
    public void removefromPendingQueue(StreamGOP X){
        synchronized (pendingqueue) {
            pendingqueue.remove(X);
        }
    }
    //This is only valid for batch queue that does not resorted, useful for benchmarking purpose only
    public void markEffected(Streampkg.StreamGOP theTask){
        GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
        miscTools.TaskQueue newVQ = new miscTools.TaskQueue(Batchqueue);
        boolean found=false;
        while(!newVQ.isEmpty()){
            StreamGOP T=newVQ.removeDefault();
            if(found){
                T.flags|=1; //mark flag as 1
            }else if(theTask==T){
                found=true;
                T.flags|=1;
            }
        }
    }

    public void mergeifpossible(StreamGOP X,double SDco){
        //check for duplication first
        Streampkg.TaskRequest aRequestlvl1 = new Streampkg.TaskRequest(X,1); //= ... derive from X
        System.out.print(X.videoname+" ");
        if (mergePending_tasklvl.containsKey(aRequestlvl1)) {
            merged_tasklvl_count++;
            System.out.println("match Task lvl (exactly the same request) -> dropping this request");
            //don't even need to check if it is not null or state is not dispatched
        }else {
            //System.out.println("not duplicated");
            //synchronized (mergePending_tasklvl) { // no modification to tasklvl mapping anywhere else
                mergePending_tasklvl.put(aRequestlvl1, X); //or replace???
            //}
            int originalmiss=0;
            if(!CVSE.config.mergeaggressiveness.equalsIgnoreCase("Aggressive")) {
                //count how many miss would happen if not merging
                originalmiss = countOriginalMiss(X, 2); //use SDco=2 here
            }
            //System.out.println("Original miss=" + originalmiss);

            //create task signature, to check for matching
            Streampkg.TaskRequest aRequestlvl2 = new Streampkg.TaskRequest(X, 2);
            Streampkg.TaskRequest aRequestlvl3 = new Streampkg.TaskRequest(X, 3);
            //check level 2
            if (mergePending_operationlvl.containsKey(aRequestlvl2)) {
                if (trymerge(X, originalmiss, aRequestlvl2, mergePending_operationlvl)) {
                    System.out.println("Merged in lvl2");
                    return;
                }
                System.out.println("found Mergeable lvl 2, but not merged.");
            }
            synchronized (mergePending_operationlvl) {
                mergePending_operationlvl.put(aRequestlvl2, X);
            }
            if (mergePending_datalvl.containsKey(aRequestlvl3)) {
                if (trymerge(X, originalmiss, aRequestlvl3, mergePending_datalvl)) {
                    System.out.println("Merged in lvl3");
                    return;
                }
                System.out.println("found Mergeable lvl 3, but not merged.");
            }
            synchronized (mergePending_datalvl) {
                mergePending_datalvl.put(aRequestlvl3, X);
            }


            System.out.println("add to queue directly, not merging with anything");
            Batchqueue.add(X);
        }
    }
}
