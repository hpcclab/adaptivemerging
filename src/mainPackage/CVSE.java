package mainPackage;

import Cache.Caching;
import IOWindows.OutputWindow;
import IOWindows.WebserviceRequestGate;
import Repository.VideoRepository;
import ResourceManagement.ResourceProvisioner;
import Scheduler.AdmissionControl;
import Scheduler.GOPTaskScheduler;
import Scheduler.SystemConfig;
import Simulator.RequestGenerator;
import TimeEstimatorpkg.TimeEstimator;

public class CVSE {
    public static SystemConfig config;
    public static VideoRepository VR;
    public static Caching CACHING;
    public static GOPTaskScheduler GTS; //merger created inside GTS if use mergeableGTS
    public static ResourceProvisioner VMP;
    public static TimeEstimator TE;
    public static AdmissionControl AC;
    public static RequestGenerator RG;
    public static OutputWindow OW;


    public static WebserviceRequestGate WG;
}
