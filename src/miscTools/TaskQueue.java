package miscTools;

import Scheduler.SystemConfig;
import Streampkg.StreamGOP;
import mainPackage.CVSE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

//this List can be used as priority queue by take removeHighestPrio, instead of removeFirst.

public class TaskQueue extends LinkedList<StreamGOP> {
    public TaskQueue(Collection<? extends StreamGOP> collection) {
        super(collection);
    }
    public TaskQueue() {
        super();
    }

    public Streampkg.StreamGOP pollHighestPrio() {
        Streampkg.StreamGOP highest = peekFirst();
        ListIterator<Streampkg.StreamGOP> it = listIterator(1);
        while (it.hasNext()) {
            Streampkg.StreamGOP t = it.next();
            if (t.compareTo(highest)>0) {
                highest = t;
            }
        }
        return highest;
    }

    public Streampkg.StreamGOP pollEDL(){
        Streampkg.StreamGOP earliest = peekFirst();
        ListIterator<Streampkg.StreamGOP> it = listIterator(1);
        while (it.hasNext()) {
            Streampkg.StreamGOP t = it.next();
            if (t.deadLine<earliest.deadLine) {
                earliest = t;
            }
        }
        return earliest;
    }
    public Streampkg.StreamGOP pollMaxUrgency(){ //Homogeneous Only
        Streampkg.StreamGOP earliest = peekFirst();
        long earliestvalue=earliest.deadLine-CVSE.TE.getHistoricProcessTimeLong(CVSE.GTS.machineInterfaces.get(0), earliest,2);
        ListIterator<Streampkg.StreamGOP> it = listIterator(1);
        while (it.hasNext()) {
            Streampkg.StreamGOP t = it.next();
            long checkvalue=t.deadLine- CVSE.TE.getHistoricProcessTimeLong(CVSE.GTS.machineInterfaces.get(0), t,2);
            if ( checkvalue<earliestvalue) {
                earliest = t;
                earliestvalue=checkvalue;
            }
        }
        return earliest;
    }
    public Streampkg.StreamGOP removeHighestPrio() {
        Streampkg.StreamGOP highest = pollHighestPrio();
        removeFirstOccurrence(highest);
        return highest;
    }
    public Streampkg.StreamGOP removeEDL(){
        Streampkg.StreamGOP earliest = pollEDL();
        removeFirstOccurrence(earliest);
        return earliest;
     }
    public Streampkg.StreamGOP removeMaxUrgency(){ //Homogeneous Only
        Streampkg.StreamGOP earliest = pollMaxUrgency();
        removeFirstOccurrence(earliest);
        return earliest;
    }
    public Streampkg.StreamGOP removeDefault() {
        if(CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("None")) { //not sorting batch queue
            //X= Batchqueue.poll();
            return remove();
        }else if(CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("Priority")) {
            return removeHighestPrio();
        }else if(CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("Deadline")) {
            return removeEDL();
        }else if(CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("Urgency")) { //maybe broken right now
            return removeMaxUrgency(); //Homogeneous Only
        }else{
            System.out.println("unrecognize batchqueue policy");
            return removeEDL();
        }
    }
}