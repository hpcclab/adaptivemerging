package mainPackage;

import Repository.VideoRepository;
import Scheduler.SystemConfig;
import TimeEstimatorpkg.TimeEstNone;
import TranscodingVM.TranscodingVM;

//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Unmarshaller;
import java.io.*;


//This class is meant to be main Jar of the cloud machine

public class cloudMain {
    private static void setUpCVSE_forNode(){
        //Set things up
        CVSE.VR = new VideoRepository();
        //CVSE.AC = new AdmissionControl();
        //CVSE.GTS = new GOPTaskScheduler_common();
        //CVSE.GTS.readlistedOperations();
        CVSE.TE=new TimeEstNone(); //using no TimeEstimator
        //CVSE.VMP= new ResourceProvisioner( CVSE.config.minCR); //says we need at least two machines
        //CVSE.CACHING = new Caching(); //change to other type if need something that work
        //CVSE.OW=new OutputWindow(); //todo, actually call its function from VMP

        //VMP.setGTS(GTS);
        //load Videos into Repository
        //CVSE.VR.addAllRealVideos();
        //CVSE.RG= new RequestGenerator();
    }
    private static void manualSetting(){

        CVSE.config=new SystemConfig("/home/shared/config/nuConfigWeb.properties");
        CVSE.config.addProfiledDelay = true;
        CVSE.config.path = "/home/shared/";
        CVSE.config.outputDir="/home/shared/streams/";

    }
    public static void main(String[] args) throws InterruptedException {
        //File configfile = new File("config/config_node.xml");
/*
        JAXBContext ctx = null;
        try {
            ctx = JAXBContext.newInstance(CVSE.config.class);

            Unmarshaller um = ctx.createUnmarshaller();
            SystemConfig rootElement = (SystemConfig) um.unmarshal(configfile);

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        manualSetting(); // replace config reading
        setUpCVSE_forNode();
        String port="15061";
        try {
            BufferedReader idread= new BufferedReader(new FileReader("/home/shared/portid"));
            String tmp;
            while ((tmp = idread.readLine()) != null) {
                port=tmp;
            }
            idread.close();

            //DEBUG, ack what i've read
            /*
                BufferedWriter writer = new BufferedWriter(new FileWriter("/home/shared/portid_ack"));
                writer.write(port);
                writer.close();
            */
        }catch(Exception e){
            System.out.println("read bug");
        }
        //create
        TranscodingVM TC = new TranscodingVM("localContainer","chameleonBM","0.0.0.0", Integer.parseInt(port));
        //CVSE.TE.populate("localContainer");
        TC.start();
        TC.join();
    }
}
