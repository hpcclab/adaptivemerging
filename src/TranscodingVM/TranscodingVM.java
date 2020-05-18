package TranscodingVM;

import Streampkg.StreamGOP;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import com.amazonaws.services.ec2.model.Instance;

/**
 * Created by pi on 5/21/17.
 */
//TODO: best logical upgrade is to use FST instead of just serialization https://github.com/RuedigerMoeller/fast-serialization
//TODO: evaluate and implement jobqueue? activeMQ? rabbitMQ? Apache Qpid? threadpools?



public class TranscodingVM extends Thread{

    protected int myport;
    protected String centerAddr;
    //private ServerSocket ss;
    public Socket s;
    public ServerSocket ss;
    protected TranscodingThread TT;
    public ObjectOutputStream oos=null;
    public ObjectInputStream ois=null;
    private int status;
    //private Instance instance = new Instance();

    public TranscodingVM(){}

    public TranscodingVM(String itype,String vclass,String addr,int port){
        myport=port;
        centerAddr=addr;
        TT=new TranscodingThread();
        TT.VM_class=vclass;
        TT.type=itype;
    }
    public void createRecvSocket(){
        status=0;
        try {
            //s = new Socket(centerAddr, myport);
            ss = new ServerSocket(myport);
            System.out.println("waiting at "+myport);
            s = ss.accept();
        }catch(Exception e){
            myport++;
            System.out.println("createRecvSocket Failed: " + e);
        }
        try{
            while(!s.isConnected()){
                System.out.println("socket is not connected");
                sleep(1000);
            }
            //ss.close();
            oos = new ObjectOutputStream(s.getOutputStream());

            oos.flush();
            oos.reset();
            sleep(500);
            ois = new ObjectInputStream(s.getInputStream());

            ss.close();
            status = 1;

            System.out.println("succesfully set status=running");


        }catch(Exception e){
            System.out.println("createRecvSocket Failed: " + e);
        }
    }
    private void SendSegmentToVideoMerger()
    {

    }

    public void run(){
        createRecvSocket();

        //TODO: have a way to gracefully terminate without causing error and force quit
        try {
            while(!s.isClosed()){
                StreamGOP objectX =(StreamGOP) ois.readObject();
                //System.out.println("ObjectX's path"+objectX.getPath());
                if(objectX.cmdSet.containsKey("shutdown")){
                    //receive shutting down message, close down receiving communication
                    //whatever in the queue will still be processed until queue is empty
                    AddJob(objectX); //still add to the queue
                    System.out.println("Shutting Down");
                    close();
                    break;
                }else if (objectX.cmdSet.containsKey("query")){
                    if(!TT.runtime_report.isEmpty()) {
                        //System.out.println("reporting: " + TT.runtime_report.get(0).x + " " + TT.runtime_report.get(0).y);
                    }
                    double deadLineMiss=0;
                    if(objectX.deadLine>TT.synctime){ //syncTime
                        TT.synctime=objectX.deadLine;
                    }
                    oos.writeObject(new runtime_report(TT.jobs.size(),TT.requiredTime,TT.synctime,TT.realspentTime,TT.NworkDone,TT.workDone,TT.deadlineMiss,TT.NdeadlineMiss,deadLineMiss,TT.completedTask,TT.runtime_report));
                    TT.completedTask.clear(); //got asked so clear it out
                }else if (objectX.cmdSet.containsKey("fullstat")){
                    if(!TT.runtime_report.isEmpty()) {
                        //System.out.println("reporting: " + TT.runtime_report.get(0).x + " " + TT.runtime_report.get(0).y);
                    }
                    double deadLineMiss=0;
                    if(TT.workDone!=0){
                        deadLineMiss=(1.0*TT.deadlineMiss)/TT.workDone;
                    }
                    if(objectX.deadLine>TT.synctime){ //syncTime
                        TT.synctime=objectX.deadLine;
                    }
                    //System.out.println("real spent Time="+TT.realspentTime); //good
                    oos.writeObject(new runtime_report(TT.jobs.size(),TT.requiredTime,TT.synctime,TT.realspentTime,TT.NworkDone,TT.workDone,TT.deadlineMiss,TT.NdeadlineMiss,deadLineMiss,TT.completedTask,TT.runtime_report));
                    TT.completedTask.clear(); //got asked so clear it out
                    //don't remove old stat, maybe reset stat later if keep full stat too!
                    //TT.deadlineMiss=0;
                    //TT.workDone=0;
                }else{
                    //System.out.println("localthread: work adding");
                    AddJob(objectX);
                }
            }
        }catch(Exception e){
            System.out.println("run Failed: " + e);
        }
        System.out.println("closed");
    }

    protected void AddJob(StreamGOP segment)
    {
        TT.requiredTime += segment.estimatedExecutionTime;
        //debugging,
        //System.out.println("got segment "+segment.getSegmentNum());
        // System.out.println("it's at: "+segment.getPath());
        // System.out.println("it's at: "+segment.userSetting.outputDir());
        File file=new File(segment.outputDir());
        file.mkdirs();

        TT.jobs.add(segment);
        //System.out.println("Thread Status="+TT.isAlive() +" "+TT.isInterrupted()+" ");
        if (!TT.isAlive()) {
            TT.start();
        }
    }
    public void close(){
        try {
            s.close();
        } catch (Exception e) {
            System.out.println("Failed: " + e);
        }
    }
    //have a main?
    /*
    public static void main(String[] args){
        //TranscodingVMEC2.("ec2","test",2);

    }
    */
}