package Scheduler;

import Streampkg.Stream;
import Streampkg.StreamGOP;
import mainPackage.CVSE;

public class AdmissionControl {
    private double utilityBased_Prioritization(double c,int segment_number){
        //page 5 of the paper
        return Math.pow(1/Math.E,c*segment_number);
    }


    public void AssignSegmentPriority(StreamGOP segment)
    {
        segment.setPriority(0);
    }
    public void AssignSegmentPriority(StreamGOP segment,int priority)
    {
        segment.setPriority(priority);
    }
    public void AssignStreamPriority(Stream stream){
        for (StreamGOP x :stream.streamGOPs){
            double newPriority;
           // System.out.println("adc segment"+x.segment);
            int segment_number=Integer.parseInt(x.segment);

            //how we actually assign priority?
            newPriority=utilityBased_Prioritization(CVSE.config.c_const_for_utilitybased,segment_number);
            //
            x.setPriority(newPriority);
        }

    }
    private static void GetVideoSplitterSegmentInfo(StreamGOP segment)
    {

    }

    private static void GetVideoMergerSegmentInfo(StreamGOP segment)
    {

    }

}