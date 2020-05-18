package TimeEstimatorpkg;

import ResourceManagement.MachineInterface;
import Streampkg.StreamGOP;

import java.util.HashMap;


// very basic time estimator that always give result as 1

public class TimeEstNone  extends TimeEstimator{
    public TimeEstNone(){
    }
    public void updateTable(String VMclass, HashMap<String,histStat> runtime_report) {

    }
    public retStat getHistoricProcessTime(MachineInterface VM, StreamGOP segment){
        return new retStat(1, 0); //set at arbitary value
    }
    public void populate(String VMclass){

    }
}
