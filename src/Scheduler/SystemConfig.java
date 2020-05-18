package Scheduler;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;


public class SystemConfig {
    private ArrayList<String> Stringlist= new ArrayList<>(Arrays.asList("defaultInputPath","defaultOutputPath","defaultBatchScript","path","batchqueuesortpolicy",
            "profileRequestsBenhmark","overwriteQueuePolicyHeuristic","outputDir","run_mode","timeEstimatorMode",
            "scheduler_machineselectionpolicy","file_mode","mergeaggressiveness"));

    private ArrayList<String> intList= new ArrayList<>(Arrays.asList("localqueuelengthperCR","dataUpdateInterval","CRscalingIntervalTick","maxCR","minCR","remedialVM_constantfactor"));
    private ArrayList<String> doubleList= new ArrayList<>(Arrays.asList("lowscalingThreshold","highscalingThreshold","c_const_for_utilitybased","sdmultiplier"));
    private ArrayList<String> boolList= new ArrayList<>(Arrays.asList("addProfiledDelay","taskmerge","profiledRequests","openRequests","mergeOverwriteQueuePolicy",
            "enableTimeEstimator","enableCaching","enableCRscaling","enableCRscalingoutofInterval","useEC2"));

    private Properties prop = new Properties();
    private void setDefaultValues(){
        prop.setProperty("batchqueuesortpolicy","Deadline");
        prop.setProperty("run_mode","real");
        prop.setProperty("file_mode","S3");
        prop.setProperty("addProfiledDelay","false");
        prop.setProperty("mergeaggressiveness","Conservative");
        prop.setProperty("taskmerge","false");
        prop.setProperty("openWebRequests","false");
        prop.setProperty("mergeOverwriteQueuePolicy","true");
        prop.setProperty("enableTimeEstimator","false");
        prop.setProperty("enableCaching","false");
        prop.setProperty("enableCRscaling","false");
        prop.setProperty("enableCRscalingoutofInterval","false");
        prop.setProperty("sdmultiplier","1.0");
    }
    public SystemConfig(){
        setDefaultValues();

    }
    public SystemConfig(String filePath){
        setDefaultValues();
        try(InputStream inp=new FileInputStream(filePath)) {
            prop.load(inp);
            String keystr="";
            ////////// parse all prop to actual configs
            System.out.println(prop.keySet());
            for(Object key :prop.keySet()){
                    keystr=(String)key;
                    switch(keystr) {

                        //String
                        case "defaultInputPath": defaultInputPath= prop.getProperty(keystr); break;
                        case "defaultOutputPath": defaultOutputPath= prop.getProperty(keystr); break;
                        case "defaultBatchScript": defaultBatchScript= prop.getProperty(keystr); break;
                        case "path": path= prop.getProperty(keystr); break;
                        case "batchqueuesortpolicy": batchqueuesortpolicy= prop.getProperty(keystr); break;
                        case "profileRequestsBenchmark": profileRequestsBenchmark= prop.getProperty(keystr); break;
                        case "overwriteQueuePolicyHeuristic": overwriteQueuePolicyHeuristic= prop.getProperty(keystr); break;
                        case "outputDir": outputDir= prop.getProperty(keystr); break;
                        case "run_mode": run_mode= prop.getProperty(keystr); break;
                        case "timeEstimatorMode": timeEstimatorMode= prop.getProperty(keystr); break;
                        case "scheduler_machineselectionpolicy": scheduler_machineselectionpolicy = prop.getProperty(keystr); break;
                        case "file_mode": file_mode= prop.getProperty(keystr); break;
                        //Int
                        case "localqueuelengthperCR": localqueuelengthperCR= Integer.parseInt(prop.getProperty(keystr)); break;
                        case "dataUpdateInterval": dataUpdateInterval= Integer.parseInt(prop.getProperty(keystr)); break;
                        case "CRscalingIntervalTick": CRscalingIntervalTick= Integer.parseInt(prop.getProperty(keystr)); break;
                        case "maxCR": maxCR= Integer.parseInt(prop.getProperty(keystr)); break;
                        case "minCR": minCR= Integer.parseInt(prop.getProperty(keystr)); break;
                        case "remedialVM_constantfactor": remedialVM_constantfactor= Integer.parseInt(prop.getProperty(keystr)); break;
                        //Double
                        case "lowscalingThreshold": lowscalingThreshold= Double.parseDouble(prop.getProperty(keystr)); break;
                        case "highscalingThreshold": highscalingThreshold= Double.parseDouble(prop.getProperty(keystr)); break;
                        case "c_const_for_utilitybased": c_const_for_utilitybased= Double.parseDouble(prop.getProperty(keystr)); break;
                        case "sdmultiplier": sdmultiplier= Double.parseDouble(prop.getProperty(keystr)); break;

                        //Boolean
                        case "addProfiledDelay": addProfiledDelay= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "mergeaggressiveness": mergeaggressiveness= prop.getProperty(keystr); break;
                        case "taskmerge": taskmerge= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "profiledRequests": profiledRequests= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "openWebRequests": openWebRequests = Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "mergeOverwriteQueuePolicy": mergeOverwriteQueuePolicy= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "enableTimeEstimator": enableTimeEstimator= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "enableCaching": enableCaching= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "enableCRscaling": enableCRscaling= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "enableCRscalingoutofInterval": enableCRscalingoutofInterval= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        case "useEC2": useEC2= Boolean.parseBoolean(prop.getProperty(keystr)); break;
                        //List
                        case "repository": String[] items=prop.getProperty(keystr).split("[|]");
                            for(String item : items){
                                repository.add(item);
                            }
                            break;
                        case "CR": String[] CRs=prop.getProperty(keystr).split("[|]");
                            for(String CR : CRs){
                               // System.out.println("splitted as "+CR);
                                setCR(CR);
                            }
                            break;
                        //
                        default: System.out.println("Unknown config");
                        System.out.println("Key : " + keystr + ", Value : " + prop.getProperty(keystr));


                    };

            }
            //////////
        }catch (Exception e){
            System.out.println("read properties fail");
            e.printStackTrace();
        }
    }

    public void setCR(String CR_Texts) {
        String s[] = CR_Texts.split(",");
        if (s.length == 5) {
            CR_type.add(s[0]);
            CR_class.add(s[1]);
            CR_address.add(s[2]);
            CR_ports.add(Integer.parseInt(s[3]));
            CR_autoschedule.add(Boolean.parseBoolean(s[4]));
            //System.out.println(s[0]+" "+s[1]+" "+s[2]+" "+s[3]+" "+s[4]+" ");

        } else {
            System.out.println("invalid format");
        }
    }


    // questionable,debug settings
    public  String defaultInputPath; //
    public  String defaultOutputPath; //
    public  String defaultBatchScript; //
    public  String path;
    public  ArrayList<String> CR_type =new ArrayList<>();
    public  ArrayList<String> CR_class =new ArrayList<>();
    public  ArrayList<Boolean> CR_autoschedule =new ArrayList<>();
    public  ArrayList<String> CR_address =new ArrayList<>();
    public  ArrayList<Integer> CR_ports =new ArrayList<>();


    public  boolean addProfiledDelay=false; //this is profiled delay, from GOPS
    public  String mergeaggressiveness ="Conservative"; ////either Conservative, Adaptive, or Aggressive
    public  String batchqueuesortpolicy="Deadline";
    public  boolean taskmerge=false;
    public  boolean profiledRequests=false;
    public  boolean openWebRequests =false;
    public  String profileRequestsBenchmark;
    public  boolean mergeOverwriteQueuePolicy=false;
    public  String overwriteQueuePolicyHeuristic;

    public  String outputDir;

    public  String run_mode="real";
    public  int localqueuelengthperCR=4;

    // mainstay settings
    public  ArrayList<String> supportedCodecs=new ArrayList<>(8); //not being used right now
    public  ArrayList<String> repository=new ArrayList<>();
    //public static int maximumResolution;
    public  boolean enableTimeEstimator=false; //if true, use Time estimator to do scheduling
    public  String timeEstimatorMode="";
    public  String scheduler_machineselectionpolicy;
    public  boolean enableCaching=false; //if true, use caching system
    public  boolean enableCRscaling=false;
    public  int dataUpdateInterval=1000; //millisecond, 0 to disable
    public  int CRscalingIntervalTick=10; //millisecond, 0 to disable
    public  boolean enableCRscalingoutofInterval=false;
    public  int maxCR; //set max number of ComputingResources
    public  int minCR; //set max number of ComputingResources
    public  int remedialVM_constantfactor=10; //default value=10
    public  double lowscalingThreshold; // for VM provisioning algorithms
    public  double highscalingThreshold;
    public  double c_const_for_utilitybased=0.1; //default value=0.1
    public  double sdmultiplier=1.0; //change SD to be more volatile? default is 1.0 (truthful for the data).
    public  boolean useEC2;
    public  String file_mode="S3"; //can be S3 or
    //public static boolean mergeOverwriteQueuePolicy=true;

    /*
    ////belows are all function intended for unmasher code to use parsing config
    public void setPath(String path) {
        CVSE.config.path = path;
    }
    public void setDefaultInputPath(String defaultInputPath) {
        CVSE.config.defaultInputPath = defaultInputPath;
    }
    public void setDefaultOutputPath(String defaultOutputPath) {
        CVSE.config.defaultOutputPath = defaultOutputPath;
    }
    public void setDefaultBatchScript(String defaultBatchScript) {
        CVSE.config.defaultBatchScript = defaultBatchScript;
    }
    public void setAddProfiledDelay(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.addProfiledDelay = true;
        }else{
            CVSE.config.addProfiledDelay = false;
        }
    }
    public void setconsideratemerge(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.consideratemerge = true;
        }else{
            CVSE.config.consideratemerge = false;
        }
    }
    public void setbatchqueuesortpolicy(String defaultInputPath) {
        CVSE.config.batchqueuesortpolicy = defaultInputPath;
    }
    public void setprofiledRequests(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.profiledRequests = true;
        }else{
            CVSE.config.profiledRequests = false;
        }
    }
    public void setopenRequests(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.openWebRequests = true;
        }else{
            CVSE.config.openWebRequests = false;
        }
    }
    public void setprofileRequestsBenhmark(String input) {
        CVSE.config.profileRequestsBenchmark =input;
    }
    public void setRepository(String repository) {
        CVSE.config.repository.add(repository);
    }

    public void setCR(String CR_Texts) {
        String s[]=CR_Texts.split(",");
        if(s.length==5) {
            CVSE.config.CR_type.add(s[0]);
            CVSE.config.CR_class.add(s[1]);
            CVSE.config.CR_address.add(s[2]);
            CVSE.config.CR_ports.add(Integer.parseInt(s[3]));
            CVSE.config.CR_autoschedule.add(Boolean.parseBoolean(s[4]));
            //System.out.println(s[0]+" "+s[1]+" "+s[2]+" "+s[3]+" ");

        }else {
            System.out.println("invalid format");
        }
    }
    public void setSupportedCodecs(String supportedCodecs) {
        CVSE.config.supportedCodecs.add(supportedCodecs);
    }
    public void setEnableTimeEstimator(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.enableTimeEstimator = true;
        }else{
            CVSE.config.enableTimeEstimator = false;
        }
    }
    public void setTimeEstimatorMode(String timeEstimatorMode) {
        CVSE.config.timeEstimatorMode = timeEstimatorMode;
    }

    public void setSchedulerPolicy(String scheduler_machineselectionpolicy) {
        CVSE.config.scheduler_machineselectionpolicy = scheduler_machineselectionpolicy;
    }
    public void setEnableCaching(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.enableCaching = true;
        }else{
            CVSE.config.enableCaching = false;
        }
    }
    public void setenableCRscaling(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.enableCRscaling = true;
        }else{
            CVSE.config.enableCRscaling = false;
        }
    }
    public void setCRscalingIntervalTick(int CRscalingIntervalTick) {
        CVSE.config.CRscalingIntervalTick = CRscalingIntervalTick;
    }
    public void setlocalqueuelengthperCR(int ilocalqueuelengthperCR) {
        CVSE.config.localqueuelengthperCR = ilocalqueuelengthperCR;
    }
    public void settaskmerge(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.taskmerge = true;
        }else{
            CVSE.config.taskmerge = false;
        }
    }
    public void setdataUpdateInterval(int dataUpdateInterval) {
        CVSE.config.dataUpdateInterval = dataUpdateInterval;
    }
    public void setenableCRscalingoutofInterval(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.enableCRscalingoutofInterval = true;
        }else{
            CVSE.config.enableCRscalingoutofInterval = false;
        }
    }
    public void setmergeOverwriteQueuePolicy(String check) {
        if(check.equalsIgnoreCase("True")) {
            CVSE.config.mergeOverwriteQueuePolicy = true;
        }else{
            CVSE.config.mergeOverwriteQueuePolicy = false;
        }
    }
    public void setoverwriteQueuePolicyHeuristic(String value) {
        CVSE.config.overwriteQueuePolicyHeuristic = value;
    }
    public void setmaxCR(int maxCR) {
        CVSE.config.maxCR = maxCR;
    }
    public void setminCR(int minCR) {
        CVSE.config.minCR = minCR;
    }
    public void setRemedialVM_constantfactor(int remedialVM_constantfactor) {
        CVSE.config.remedialVM_constantfactor = remedialVM_constantfactor;
    }
    public void setLowscalingThreshold(Double lowscalingThreshold) {
        CVSE.config.lowscalingThreshold = lowscalingThreshold;
    }
    public void setHighscalingThreshold(Double highscalingThreshold) {
        CVSE.config.highscalingThreshold = highscalingThreshold;
    }
    public void setC_const_for_utilitybased(double c_const_for_utilitybased) {
        CVSE.config.c_const_for_utilitybased = c_const_for_utilitybased;
    }
    public void setMapping_mechanism(String mapping_mechanism) {
        CVSE.config.mapping_mechanism = mapping_mechanism;
    }

    public void setuseEC2(String useEC2) {
        CVSE.config.useEC2 = Boolean.parseBoolean(useEC2);
    }
    public void setfile_mode(String mode) {
        CVSE.config.file_mode = mode;
    }
    public void setrunMode(String mode) {
        CVSE.config.run_mode = mode;
    }
    public void setOutputDir(String dir) {
        CVSE.config.outputDir = dir; }

     */
}

