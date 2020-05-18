package Scheduler;

import ResourceManagement.MachineInterface;
import Streampkg.StreamGOP;
import TimeEstimatorpkg.retStat;
import mainPackage.CVSE;


//extends GOPTaskScheduler, with more VM type support, more scheduling options
public class GOPTaskScheduler_common extends GOPTaskScheduler {

    public GOPTaskScheduler_common(){
        super();
        //if(CVSE.config.mapping_mechanism.equalsIgnoreCase("ShortestQueueFirst")){
            //add server list to ShortestQueueFirst list too?
        //}
    }
    //overwrite with common VM types
    public boolean add_VM(MachineInterface t,boolean autoSchedule){
        if(autoSchedule) {
            maxpending += CVSE.config.localqueuelengthperCR; //4?
        }
        machineInterfaces.add(t);
        return true; //for success
    }

    //shortest queue first, without time estimator, just use queue length
    protected MachineInterface ShortestQueueLength(StreamGOP x, int[] pending_queuelength, long[] pending_executiontime, double SDcoefficient, boolean realSchedule) {
        long estimatedT;
        if(machineInterfaces.size()>0) {
            MachineInterface answer = machineInterfaces.get(0);
            long minFT;
            if((pending_queuelength[0] < CVSE.config.localqueuelengthperCR) || !realSchedule) {
                minFT = pending_queuelength[0];
            }else{
                minFT=Integer.MAX_VALUE;   //don't select me, i'm full
            }
            for (int i = 1; i < machineInterfaces.size(); i++) {
                MachineInterface aMachine = machineInterfaces.get(i);
                if (aMachine.isWorking()) {
                    if (aMachine.autoschedule) {
                        if((pending_queuelength[i] < CVSE.config.localqueuelengthperCR) || !realSchedule) {
                            estimatedT = pending_queuelength[i];
                            if (estimatedT < minFT) {
                                answer = aMachine;
                                minFT = estimatedT;
                            }
                        }else {
                            //System.out.println("queue is full");
                        }
                    }else{
                        System.out.println("not considering non-auto assign machine");
                    }
                }else{
                    System.out.println("warning, a machine is not ready");
                }
            }
            if(realSchedule) {
                System.out.println("decided to place on machine " + answer.VM_class + " id= " + answer.id + " new minFT=" + minFT + " queuelength=" + answer.estimatedQueueLength + "/" + CVSE.config.localqueuelengthperCR);
            }
            return answer;
        }
        System.out.println("BUG: try to schedule to 0 VM");
        return null;
        }
    //shortest job (queue) first (time based), minimum completion time first, minimum execution time first
    //SJF,MCT,MET
    protected MachineInterface simplemachineselect(StreamGOP x, int[] pending_queuelength, long[] pending_executiontime, String mode, double SDcoefficient, boolean realSchedule){
        //currently machine 0 must be autoscheduleable
        long estimatedT;
        if(machineInterfaces.size()>0) {
            MachineInterface answer= machineInterfaces.get(0);
            long minFT;
            long minET=0;
            double minSD=0;
            //set initial value to machine 1
            if((pending_queuelength[0] < CVSE.config.localqueuelengthperCR) || !realSchedule){ //if not real assignment, we can violate queue length
                    retStat chk = CVSE.TE.getHistoricProcessTime(answer, x);
                    //System.out.println("chk.mean="+chk.mean+" chk.SD"+chk.SD+" SDco="+SDcoefficient);
                    if(mode.equalsIgnoreCase("MET")){
                        minFT=(long) (chk.mean + chk.SD * SDcoefficient);
                    }else {
                        minFT = pending_executiontime[0]; //for SJF,MCT
                        if (mode.equalsIgnoreCase("MCT")) { // MCT add ET of this task too
                            minFT += (long) (chk.mean + chk.SD * SDcoefficient);
                        }
                    }
                    minET=chk.mean;
                    minSD=chk.SD;
            }else{
                minFT=Integer.MAX_VALUE;   //don't select me, i'm full
            }


            //System.out.println("first est time="+minFT);
            //System.out.println("VMINTERFACE SIZE="+machineInterfaces.size());
            for (int i = 1; i < machineInterfaces.size(); i++) {
                MachineInterface aMachine = machineInterfaces.get(i);
                if (aMachine.isWorking()) {
                    if (aMachine.autoschedule) {
                        if((pending_queuelength[i] < CVSE.config.localqueuelengthperCR) || !realSchedule) {
                        //calculate new choice
                            retStat chk = CVSE.TE.getHistoricProcessTime(aMachine, x);
                            long checkTime;
                            //System.out.println("chk.mean="+chk.mean+" chk.SD"+chk.SD+" SDco="+SDcoefficient);
                            if(mode.equalsIgnoreCase("MET")){ //for MET, simply compare execution time
                                checkTime= (long) (chk.mean + chk.SD * SDcoefficient);
                            }else {
                                checkTime = pending_executiontime[i]; //for SJF,MCT
                                if (mode.equalsIgnoreCase("MCT")) { //MCT add ET of this task too
                                    checkTime += (long) (chk.mean + chk.SD * SDcoefficient);
                                }
                            }
                            if (checkTime < minFT) {
                                answer = aMachine;
                                minFT = checkTime;
                                minET = chk.mean;
                                minSD = chk.SD;
                            }
                        }else {
                            //System.out.println("queue is full");
                        }
                    }else{
                        System.out.println("not considering non-auto assign machine");
                    }
                }else{
                    System.out.println("warning, a machine is not ready");
                }
            }
            if(realSchedule) {
                x.estimatedExecutionTime = minET;
                x.estimatedExecutionSD=minSD;
                System.out.println("decided to place on machine " + answer.VM_class + " id= " + answer.id + " new minFT=" + minFT + " queuelength=" + answer.estimatedQueueLength + "/" + CVSE.config.localqueuelengthperCR);
            }
            return answer;
        }
        System.out.println("BUG: try to schedule to 0 VM");
        return null;
    }

    //will have more ways to assign works later
    protected MachineInterface selectMachine(StreamGOP x){
        //System.out.println("assigning works");
        int[] queuelength=new int[machineInterfaces.size()];
        long[] executiontime=new long[machineInterfaces.size()];
        for(int i = 0; i< machineInterfaces.size(); i++){
            queuelength[i]= machineInterfaces.get(i).estimatedQueueLength;
            executiontime[i]= machineInterfaces.get(i).estimatedExecutionTime;
        }
        if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("MCT")){ //Minimum Completion Time
            //use SDco or 2 ??
            return simplemachineselect(x,queuelength,executiontime,"MCT",2,true);
        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("SJF")){  //Shortest Job(queue) First
            return simplemachineselect(x,queuelength,executiontime,"SJF",2,true);
        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("MET")){  //Minimum Execution time First
            return simplemachineselect(x,queuelength,executiontime,"MET",2,true);

        }else if(CVSE.config.scheduler_machineselectionpolicy.equalsIgnoreCase("SQL")){ //Shortest Queue Length,
            // don't need time estimator
            return ShortestQueueLength(x,queuelength,executiontime,2,true);
        }else{
            //what should be a default? SQL maybe?
            return ShortestQueueLength(x,queuelength,executiontime,2,true);
        }

    }
    //function that do something before task X get sent
    protected void preschedulefn(StreamGOP X){

    }
    public void taskScheduling(){ // first function call to submit some works to other machine

        System.out.println("call submit work");
        try {
            readytoWork.acquire();
        }catch(Exception e){
            System.out.println("Sem of task scheduling error");
        }

        while ((!Batchqueue.isEmpty()) && workpending < maxpending) {
            StreamGOP X;
            //select a task by a criteria
            synchronized (Batchqueue) {
                X = Batchqueue.removeDefault();
            }
            preschedulefn(X);

            MachineInterface chosenVM = selectMachine(X);
            System.out.println("ChosenVM=" + chosenVM);

            if (CVSE.config.enableCRscalingoutofInterval && (chosenVM.estimatedQueueLength > CVSE.config.localqueuelengthperCR)) {
                //do reprovisioner, we need more VM!
                //ResourceProvisioner.EvaluateClusterSize(0.8,Batchqueue.size());
                System.out.println("queue too long, please scale up!");
                CVSE.VMP.EvaluateClusterSize(-2);
                //re-assign works
                chosenVM = selectMachine(X);
                System.out.println("ChosenVM=" + chosenVM);
            }

            if (CVSE.config.run_mode.equalsIgnoreCase("sim")) {
                retStat thestat = CVSE.TE.getHistoricProcessTime(chosenVM, X);
                //System.out.println("dry run, mean="+thestat.mean+" sd="+thestat.SD);
                X.estimatedExecutionTime = thestat.mean;
                X.estimatedExecutionSD = thestat.SD;
                //X.estimatedDelay
            }
            //System.out.println("before dispatch");

            //change StreamGOP type to Dispatched
            X.dispatched = true;
            //X.parentStream=null;

            //then it's ready to send out
            chosenVM.sendJob(X);
            postschedulefn(X);
            System.out.println("send job " + X.getPath() + " to " + chosenVM.toString());
            //System.out.println("estimated queuelength=" + chosenVM.estimatedQueueLength);
            //System.out.println("estimated ExecutionTime=" + chosenVM.estimatedExecutionTime);
            workpending++;
            System.out.println("workpending=" + workpending + " maxpending=" + maxpending);
            if (workpending == maxpending) {
                System.out.println("workpending==maxpending");
                CVSE.VMP.collectData();
            }
        }
        readytoWork.release();
    }

    //function that do something after task X get sent
    protected void postschedulefn(StreamGOP X){

    }
}
