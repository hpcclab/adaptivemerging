package mainPackage;

import Cache.Caching;
import DockerManagement.DockerManager;
import IOWindows.OutputWindow;
import IOWindows.WebserviceRequestGate;
import Repository.VideoRepository;
import ResourceManagement.MachineInterface;
import ResourceManagement.MachineInterface_SocketIO;
import ResourceManagement.ResourceProvisioner;
import Scheduler.AdmissionControl;
import Scheduler.GOPTaskScheduler_mergable;
import Scheduler.SystemConfig;
import Simulator.RequestGenerator_Streamlevel;
import Streampkg.Settings;
import Streampkg.StreamManager;
import TimeEstimatorpkg.TimeEstNone;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;

import java.io.IOException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class RealModeTest {

        private static void setUpCVSE_forreal(String configfile){
            //Set things up
            CVSE.config=new SystemConfig(configfile);
            CVSE.VR = new VideoRepository();
            CVSE.AC = new AdmissionControl();
            CVSE.GTS = new GOPTaskScheduler_mergable();
            CVSE.GTS.readlistedOperations();
            CVSE.TE=new TimeEstNone(); //using no TimeEstimator
            CVSE.VMP= new ResourceProvisioner( CVSE.config.minCR); //says we need at least two machines
            CVSE.CACHING = new Caching(); //change to other type if need something that work
            CVSE.OW=new OutputWindow(); //todo, actually call its function from VMP

            //VMP.setGTS(GTS);
            //load Videos into Repository
            CVSE.VR.addAllRealVideos();
            CVSE.RG= new RequestGenerator_Streamlevel();
        }

        public static void RealLocalThreads() throws IOException {

        //Step 1: Retrieve Real Videos from Video Repository

        setUpCVSE_forreal("config/nuConfigWeb.properties");
        StreamManager SM = new StreamManager();
        //seems fine for tbe most part
        //checking...
        int num = Integer.MAX_VALUE;

        //Step 2 Request a stream by requesting an index number
        while (num != 0) {

            Scanner scanner = new Scanner(System.in);

            for (int i = 0; i < CVSE.VR.videos.size(); i++) {
                System.out.println(CVSE.VR.videos.get(i).name + ": " + i);
            }
            System.out.println("Enter the video that you would like to have streamed: ");
            num = scanner.nextInt();

            Settings newSettings = new Settings();

            newSettings.resolution = true;
            newSettings.resWidth = "352";
            newSettings.resHeight = "240";
            newSettings.videoname = CVSE.VR.videos.get(num).name;

            SM.InitializeStream(num, newSettings, CVSE.GTS);
        }

        for (int i = 0; i < CVSE.VR.videos.size(); i++) {
            System.out.println(CVSE.VR.videos.get(i).name);
        }

        /*

        while(!GTS.emptyQueue()){
                System.out.println("wait for pending work to finish");
                sleep(300);
            }

        System.out.println("All queue are emptied");

        //*/

        //wind down process
        CVSE.GTS.close();
        CVSE.VMP.closeAll();

        //Step 6: remove all the folders and contents in the streams folder
        /*
        try {
            SM.RemoveProcessedStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*/
    }
    public static void WebRequestTest() throws IOException {


        //Step 1: Retrieve Real Videos from Video Repository

        StreamManager SM = new StreamManager();
        setUpCVSE_forreal("config/nuConfigWeb.properties");
        ////create open socket, receive new profile request then do similar to profiledRequests
        CVSE.WG=new WebserviceRequestGate();
        CVSE.WG.addr="http://localhost:9901/transcoderequest";
        if(SM == null){
            System.out.println("SM is null " + SM);
        }
        else{
            System.out.println("SM is fine " + SM);
        }

        CVSE.WG.SM = SM;

        // example of actual request: http://localhost:9901/transcoderequest/?videoid=1,cmd=resolution,setting=180
        // (assume 10 is id of bigbuckbunny
        // TODO: figure about timing of the request, both deadline and arrival (in webservicegate class)
        ///*
        try {
            CVSE.WG.startListener();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("webservice enabled");
        if(CVSE.WG.SM == null){
            System.out.println("webgate SM is null " + CVSE.WG.SM);
        }
        else{
            System.out.println("webgate SM is fine " + CVSE.WG.SM);
        }
        //*/
        // int num = Integer.MAX_VALUE;

        /*
        //Step 2 Request a stream by requesting an index number
        while (num != 0) {

            Scanner scanner=new Scanner(System.in);

            for (int i=0;i< VideoRepository.videos.size();i++){
                System.out.println(VideoRepository.videos.get(i).name + ": " + i);
            }
            System.out.println("Enter the video that you would like to have streamed: ");
            num = scanner.nextInt();

            Settings newSettings = new Settings();

            newSettings.resolution = true;
            newSettings.resWidth = "640";
            newSettings.resHeight = "480";
            newSettings.videoname = VideoRepository.videos.get(num).name;

            SM.InitializeStream(num, newSettings, GTS);
        }

        //VMP.closeAll();
        //GTS.close();

        */
    }
    private static void DirectoryTest() {
        System.out.println("Directory SimModeTest");

        System.out.println(CVSE.config.repository);

        for (int i = 0; i < CVSE.VR.videos.size(); i++) {
            System.out.println(CVSE.VR.videos.get(i).name);
        }
    }

    private static void CreateContainerTest() throws InterruptedException, DockerException, DockerCertificateException {
        int VMcount=0;
            for (int i=0;i<1;i++){
                //String result = DockerManager.CreateContainers(1);
                System.out.println("container thread "+ CVSE.config.CR_ports.get(VMcount));
                String IP=DockerManager.CreateContainers(CVSE.config.CR_ports.get(VMcount)+"").split(",")[0]; //get IP from docker
                try {
                    sleep(400);
                }catch(Exception e){
                    System.out.println("sleep bug in AddInstance (localVMThread)");
                }
                MachineInterface t=new MachineInterface_SocketIO(CVSE.config.CR_class.get(VMcount),IP, CVSE.config.CR_ports.get(VMcount),VMcount, CVSE.config.CR_autoschedule.get(VMcount)); //no ip needed
                //CVSE.GTS.add_VM(t,CVSE.config.CR_autoschedule.get(VMcount));
                VMcount++;
            }
        //DockerManager.RemoveAllContainers();
    }

    private static void ReadVideos(){
        //setUpCVSE_forreal("config/nuConfigWeb.properties");

        //load video repo so we know their v numbers
            VideoRepository VR = new VideoRepository();
            //VR.addAllKnownVideos();
            VR.addAllRealVideos();

            for(int i=0;i<VR.videos.size();i++){
                System.out.println("Video "+i+" name: "  + VR.videos.get(i).name);
            }
    }

    private static void RemoveContainers() throws DockerException, InterruptedException {
        DockerManager.RemoveAllContainers();
    }


    public static void main(String[] args) throws IOException, InterruptedException, DockerException, DockerCertificateException {
        //ReadVideos();
        //RemoveContainers();
        WebRequestTest();
        //CreateContainerTest();
        //MachineInterface t=new MachineInterface_SocketIO(CVSE.config.CR_class.get(0),"localhost", CVSE.config.CR_ports.get(0),0,CVSE.config.CR_autoschedule.get(0)); //no ip needed //this passed

    }
}