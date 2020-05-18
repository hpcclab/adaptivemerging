package TimeEstimatorpkg;

import ResourceManagement.MachineInterface;
import Streampkg.StreamGOP;

import java.util.HashMap;

public class TimeEstLearnMode extends TimeEstimator{
    //rather than have one table for each fine data, also store generalized data
    HashMap<String,HashMap<String,histStat>> OperationlvlTable=new HashMap<>();
    HashMap<String,HashMap<String,histStat>> OperationParamlvlTable=new HashMap<>();

    //(overwrite) updateTable is now VERY basic, overwrite old data with new data
    //currently only work with fineData but not coarse data
    //need better improvement such as averaging...
    //INCOMPLETE
    public void updateTable(MachineInterface VM, HashMap<String,histStat> runtime_report){
        detailedTable.put(VM.VM_class,runtime_report);
        System.out.println("Update TimeEstimator table of VM "+VM.VM_class+" to "+runtime_report);
    }

    //(overwrite) search 3lvl
    public retStat getHistoricProcessTime(MachineInterface VM, StreamGOP segment) {
        retStat test= super.getHistoricProcessTime(VM,segment); //basic finding for detailed data
        if(test.mean>0 || test.SD>0){ //have some fine result
            return test;
        }
        if(test.mean==0 && test.SD==0){ //have machine data but not fine GOP, operation, parameter data

            //try to find similar result?
            test= searchHistoricProcessTime(OperationParamlvlTable.get(VM.VM_class),segment,"");
            if(test.mean!=0 || test.SD!=0){ //have data of operation and paramlvl table
                return test;
            }
            test= searchHistoricProcessTime(OperationParamlvlTable.get(VM.VM_class),segment,"operation");
            if(test.mean!=0 || test.SD!=0){ //have data of operation table
                return test;
            }
            //no data, do something! , give out non zero return
            return new retStat(1,0);

        }else{//doesn't even have machine's profile
            //what to do?
        }
        return new retStat(1,0);
    }

    }
