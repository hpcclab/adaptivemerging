package Scheduler;

import ResourceManagement.MachineInterface;
import Streampkg.Stream;
import Streampkg.StreamGOP;
import mainPackage.CVSE;
import miscTools.TaskQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

// base class of all GOPTaskScheduler, have common functions, taskScheduling function itself schedule task in FCFS.
public abstract class GOPTaskScheduler {
    Semaphore readytoWork;
    protected TaskQueue Batchqueue;
    public  ArrayList<MachineInterface> machineInterfaces = new ArrayList<MachineInterface>();
    protected  int maxpending = 0;
    public  int workpending = 0;
    public  long  maxElapsedTime; //use for setting Deadline
    public  List<Operations.simpleoperation> possible_Operations= new ArrayList<>();

    public GOPTaskScheduler(){
        readytoWork=new Semaphore(1);
        Batchqueue= new TaskQueue();
    }
    public int getBatchqueueLength(){
        return Batchqueue.size();
    }
    public boolean add_VM(MachineInterface t,boolean autoSchedule){
        //overwrite this
        maxpending += CVSE.config.localqueuelengthperCR; //4?
        machineInterfaces.add(t);
        return true;
    }

    public boolean remove_VM(int which) {
        machineInterfaces.remove(which);
        maxpending -= CVSE.config.localqueuelengthperCR; //4?
        return true;
    }

    public void addStream(Stream ST) {
        CVSE.AC.AssignStreamPriority(ST);
        for (StreamGOP X : ST.streamGOPs) {
            if(!CVSE.CACHING.checkExistence(X)) {
                synchronized (Batchqueue) {
                    Batchqueue.add(X);
                }
            }else{
                System.out.println("GOP cached, no reprocess");
            }
        }
        //taskScheduling();
    }

    public boolean emptyQueue() {
        if (Batchqueue != null ) {
            return (Batchqueue.isEmpty());
        }
        return false;
    }
    public int FCFS(){
        int dispatched=0;
        for(int i = 0; i< machineInterfaces.size(); i++){ //get a free machine
            int assignable= machineInterfaces.get(i).estimatedQueueLength - CVSE.config.localqueuelengthperCR; //get number of task can assign to this machine
            while ((!Batchqueue.isEmpty()) && assignable>0) {
                StreamGOP X;
                synchronized (Batchqueue) {
                    X = Batchqueue.removeDefault();
                }
                X.dispatched=true;
                machineInterfaces.get(i).sendJob(X);
                dispatched++;
                System.out.println("FCFS scheduler send job " + X.getPath() + " to " + machineInterfaces.get(i).toString());
            }
        }
        return dispatched;
    }
    public void taskScheduling() {
        System.out.println("called non overwritten taskScheduling function, doing FCFS assignment");
        try {
            readytoWork.acquire();
        }catch(Exception e){
            System.out.println("Sem of task scheduling error");
        }

        FCFS();
        readytoWork.release();
        }
    //turn off VMS socket connection sockets
    public void close(){
        for(int i = 0; i< machineInterfaces.size(); i++){
            machineInterfaces.get(i).close();
        }
    }
    public void readlistedOperations() {
        File listfile = new File("profile/operations.txt");
        if(!listfile.exists()){
            System.out.println("\n\nWarning profile/operations.txt does not exist \n\n");
        }else{
            try {
                Scanner scanner=new Scanner(listfile);
                while(scanner.hasNext()){
                    String line[]=scanner.nextLine().split(",");
                    if(line.length==2){
                        addOperation(line[0],line[1]);
                    }else{
                        System.out.println("ill formed line?");
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
            System.out.println("videorepository can not find list.txt or read fail");
            //e.printStackTrace();
        }
        }
        //operations as file
    }
    //provide operation name and batchscript, and it'll be added
    public void addOperation(String name,String batchscript) {
        addOperation(new Operations.simpleoperation(name,batchscript));
    }
    public void addOperation(Operations.simpleoperation op) {
        System.out.println("operation: "+op.operationname+" is added to the system");
        possible_Operations.add(op);

        for (int i=0; i<machineInterfaces.size();i++){
            machineInterfaces.get(i).addOperation(op);
        }

    }
}
