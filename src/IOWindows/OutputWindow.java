package IOWindows;

import Repository.Video;
import Scheduler.GOPTaskScheduler_mergable;
import Streampkg.StreamGOP;
import mainPackage.CVSE;

import java.util.List;


public class OutputWindow {
    public OutputWindow(){

    }
    private Video video;
    public void ackCompletedVideo(List<StreamGOP> completedTasks){

        if(CVSE.GTS instanceof GOPTaskScheduler_mergable) {
            GOPTaskScheduler_mergable GTS= (GOPTaskScheduler_mergable) CVSE.GTS;
            if (CVSE.config.taskmerge) {
                for (StreamGOP g: completedTasks
                     ) {
                    //a copy of pending queue
                    GTS.MRG.removefromPendingQueue(g); //only one record here
                    // already deleted when dispatch
                    //GTS.MRG.removeStreamGOPfromTables(g);

                }
            }
        }
    }
    public void PlayVideo()
    {

    }
}
