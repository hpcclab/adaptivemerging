package mainPackage;

import Cache.Caching;
import IOWindows.OutputWindow;
import IOWindows.WebserviceRequestGate;
import Repository.VideoRepository;
import ResourceManagement.ResourceProvisioner;
import Scheduler.AdmissionControl;
import Scheduler.GOPTaskScheduler_mergable;
import Scheduler.SystemConfig;
import Simulator.RequestGenerator_GOPlevel;
import TimeEstimatorpkg.TimeEstProfileMode;

import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * Created by pi on 6/29/17.
 */
public class SimModeTest {
    private static void setUpCVSE_forsim(String configfile,String overwriteOpt){
        //Set things up
        CVSE.config=new SystemConfig(configfile);
        if(overwriteOpt!=null){
            CVSE.config.profileRequestsBenchmark=overwriteOpt;
        }
        CVSE.VR = new VideoRepository();
        CVSE.VR.addAllKnownVideos();
        CVSE.AC = new AdmissionControl();
        CVSE.GTS = new GOPTaskScheduler_mergable();
        CVSE.GTS.readlistedOperations();
        CVSE.TE=new TimeEstProfileMode();
        CVSE.VMP= new ResourceProvisioner(CVSE.config.minCR); //says we need at least two machines
        CVSE.CACHING = new Caching(); //change to other type if need something that work
        CVSE.OW=new OutputWindow(); //todo, actually call its function from VMP
        //VMP.setGTS(GTS);
        //load Videos into Repository
        //CVSE.RG= new RequestGenerator_Streamlevel();
        CVSE.RG= new RequestGenerator_GOPlevel();

    }
    public static String trysleep(int time){
        try {
            sleep(time);
        }catch (Exception e) {
        return "Failed: " + e;
    }
        return "";
    }
    public static String Sim(String confFile, String opt) {
        CVSE _CVSE=new CVSE();
        if(opt.contains("BenchmarkInput/")){
                opt=opt.replaceFirst("BenchmarkInput/","");
                System.out.println("config trim BenchmarkInput/ out");
            }
        if(confFile.contains("config/")){ // will auto insert anyway
            opt=opt.replaceFirst("config/","");
        }
        Scanner scanner = new Scanner(System.in);
        //read config file
        if(!opt.equalsIgnoreCase("testconfig.txt")) { //if benchmark profile is freshly generated, CVSE is already set-up
            setUpCVSE_forsim("config/" + confFile,opt);
        }
        int rqn = 1, interval, n;
        if (CVSE.config.profiledRequests) {
            // read benchmark input
            if (!opt.equalsIgnoreCase("config")&&!opt.equalsIgnoreCase("testconfig.txt")) {
                System.out.println("overwrite profileRequestBenhmark with " + opt);
                CVSE.config.profileRequestsBenchmark = opt;
            }
            CVSE.RG.ReadProfileRequests(CVSE.config.profileRequestsBenchmark);
            //
            //sleep(3000);
            System.out.println("start sim");

            //CVSE.RG.contProfileRequestsGen(); //use Tick to call it

            while (!CVSE.RG.finished || !CVSE.GTS.emptyQueue()) {
                trysleep(300);
                System.out.println("wait for sim to finish");
                //CVSE.RG.contProfileRequestsGen(); //probably good idea to call here...
            }
            System.out.println("\nAll request have been released\n");
            int count=0;
            while (!CVSE.GTS.emptyQueue()) {
                System.out.println("wait for pending work to finish");
                trysleep(300);
                if(count>3){
                    System.out.println("Freeze, should do something?");
                    //CVSE.GTS.
                }
                count++;
            }
            System.out.println("All queue are emptied");
        } else if (CVSE.config.openWebRequests) {
            ////create open socket, receive new profile request then do similar to profiledRequests
            CVSE.WG=new WebserviceRequestGate();
            CVSE.WG.addr="http://localhost:9902/transcoderequest";

            // example of actual request: http://localhost:9902/transcoderequest/?videoid=1,cmd=resolution,setting=180
            // (assume 10 is id of bigbugbunny
            // TODO: figure about timing of the request, both deadline and arrival (in webservicegate class)
            try {
                CVSE.WG.startListener();
            }catch(Exception e){
                System.out.println("Request listener fail:"+e);
                return "failed";
            }
            System.out.println("webservice enabled");
        } else {
            while (rqn != 0) {
                System.out.println("enter video request numbers to generate and their interval and how many times");
                rqn = scanner.nextInt();
                interval = scanner.nextInt();
                n = scanner.nextInt();
                //create a lot of request to test out
                CVSE.RG.nRandomRequest(rqn, interval, n);
            }
        }
        // Check point, enter any key to continue
        //System.out.println("enter any key to terminate the system");
        //scanner.next();
        //wind down process
        if(CVSE.VMP==null){
            System.out.println("VMP is down");
        }else{
            if(CVSE.VMP.DU==null){
                System.out.println("DU is down");
            }
        }
        CVSE.VMP.DU.printstat();
        CVSE.VMP.DU.graphplot();

        trysleep(300);
        CVSE.GTS.close();
        CVSE.VMP.closeAll();

        return "success";



    }

    //sandbox testing something strange, not really doing the program code
    private static String genbenchmarkTrace(int seed) {



        CVSE _CVSE=new CVSE();
        setUpCVSE_forsim("config/nuConfig.properties",null);
        int[] sr;
        if (seed == 0) {
            //30
            sr = new int[] {699,1911,16384,9999,555,687,9199,104857,212223,777,1920, 1080, 768, 1990, 4192, 262144, 800, 12345, 678, 521, 50, 167, 1, 251, 68, 6, 333, 1048575, 81, 7};
            //5
            //sr = new int[] {699,1911,16384,9999,555};
            //sr = new int[]{699};
        } else {
            sr=new int[1];
            sr[0]=seed;
        }

        for (int j = 0; j < sr.length; j++) {
            for (int i = 1000; i <= 2500; i += 500) {
                //_CVSE.RG.generateProfiledRandomRequests("wcodec" + i + "r_180000_10000_3000_s" + sr[j], sr[j], 100, i, 180000, 10000, 3000);
                //use default avgslacktime value, 10000 for most operations, 8000 for codec
                _CVSE.RG.generateProfiledRandomRequests("wcodec" + i + "r_600000_10000_3000_s" + sr[j], sr[j], 100, i, 600000, 0, 3000);
            }
        }

        //CVSE.VMP.DU.graphplot();

        return "done";
    }


    //for test
    public static void main(String[] args) {

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("makeconfig")) {
                System.out.println(genbenchmarkTrace(Integer.parseInt(args[1])));
            } else { //run
                System.out.println(Sim(args[2], args[1]));
            }
        } else {
            System.out.println(genbenchmarkTrace(0));
            //String benchmarkname="testconfig";

            ///
            String benchmarkname="wcodec1000r_600000_10000_3000_s1";
            //String benchmarkname="nocodec4000r_90000_10000_12000_s1";

            /*
            // For 4 parameters
            // CVSE.RG.generateProfiledRandomRequests(benchmarkname,1024, 88, 1000, 180000, 10000, 3000);
            CVSE.RG.generateProfiledRandomRequests(benchmarkname,1024, 100, 9000, 180000, 10000, 3000);
                */
            System.out.println(Sim("nuConfig.properties", benchmarkname+".txt"));
            //

             //*/
        }
    //System.out.println(genbenchmarkTrace(0));
            //DirectoryTest();


    }

}