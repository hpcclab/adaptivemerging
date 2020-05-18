package ResourceManagement;

import Scheduler.GOPTaskScheduler_mergable;
import mainPackage.CVSE;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
//graph
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
//
class graph extends ApplicationFrame {
    public graph(final String title,XYSeries hitcount,XYSeries misscount) {
        super(title);
        XYSeriesCollection data = new XYSeriesCollection(hitcount);
        data.addSeries(misscount);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Hit Miss demo",
                "Finish tasks",
                "Task count",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }
}
public class DataUpdate {
    String Statpath="./resultstat";
    String FilenamePrefix;
    String filename="merge__SortalwaysmergeDeadline_test2000r_180000_10000_3000_s1.txt";
    FileWriter Freq;
    PrintWriter Freqwriter;
    XYSeries hitcount = new XYSeries("On-time");
    XYSeries misscount = new XYSeries("Miss");

    public DataUpdate(){
        FilenamePrefix = (CVSE.config.taskmerge) ? "merge" : "unmerge";
        FilenamePrefix += (!CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("None")) ? "_Sort" : "_Unsort";
        FilenamePrefix += "_"+CVSE.config.mergeaggressiveness+"merge";
        FilenamePrefix += (CVSE.config.mergeOverwriteQueuePolicy) ? "_"+CVSE.config.overwriteQueuePolicyHeuristic+"PositionFind" : "_inplace";
        FilenamePrefix += (!CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("None")) ? CVSE.config.batchqueuesortpolicy : "_nobatchqueuesort";
        FilenamePrefix +=(CVSE.config.sdmultiplier==1.0) ? "" : "SDmultiplyby"+CVSE.config.sdmultiplier;
        filename=FilenamePrefix+"_"+CVSE.config.profileRequestsBenchmark;
        try {
            Freq = new FileWriter(Statpath+"/freq/" +filename);
            Freqwriter = new PrintWriter(Freq);
        } catch (IOException e) {
            System.out.println("Stat printing error");
            e.printStackTrace();
        }
    }
    public void graphplot(){
        System.out.println("Call graphplot");
        /////////// Java plot
        final graph finalgraph=new graph("Hit/Miss tasks",hitcount,misscount);
        finalgraph.pack();
        RefineryUtilities.centerFrameOnScreen(finalgraph);
        finalgraph.setVisible(true);
        /*
        //brief graph
        String command[] = new String[]{"bash", "bash/plot.sh", Statpath+"/numbers/"+filename};
        //full graph
        //String command[] =...
        ////
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen
        pb.redirectError(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen
        try {
            Process p = pb.start();
            p.waitFor();

        } catch (Exception e) {
            System.out.println("Did not execute bashfile :(");
            e.printStackTrace();
        }

         */
    }
    //refuent stat printing, for plotting graph with more detail
    public void printfrequentstat(){
        System.out.println("Print frequent Stat");
        long avgActualSpentTime=0;
        long totalWorkDone=0,ntotalWorkDone=0;
        long totaldeadlinemiss=0, ntotaldeadlinemiss=0;
        for (int i = 0; i < CVSE.GTS.machineInterfaces.size(); i++) {
            MachineInterface vmi = CVSE.GTS.machineInterfaces.get(i);
            avgActualSpentTime += vmi.actualSpentTime;
            totalWorkDone += vmi.total_taskdone;
            ntotalWorkDone += vmi.total_requestdone;
            totaldeadlinemiss += vmi.total_taskmiss;
            ntotaldeadlinemiss += vmi.total_requestmiss;
        }
        hitcount.add(totalWorkDone,totalWorkDone-totaldeadlinemiss);
        misscount.add(totalWorkDone,totaldeadlinemiss);
        Freqwriter.println(totalWorkDone + " , " + ntotalWorkDone + " , " + totaldeadlinemiss + " , " + ntotaldeadlinemiss + " , " + avgActualSpentTime / CVSE.config.maxCR);
    }
    public void printstat(){
        //print stat!
        long avgActualSpentTime=0;
        long totalWorkDone=0,ntotalWorkDone=0;
        long totaldeadlinemiss=0, ntotaldeadlinemiss=0;
        int mergemiss=0;
        if(CVSE.RG.finished && CVSE.GTS.emptyQueue()){
            //file output

            try {
                FileWriter F1 = new FileWriter(Statpath+"/full/" +filename);
                FileWriter F2 = new FileWriter(Statpath+"/numbers/" +filename);
                PrintWriter Fullwriter = new PrintWriter(F1);
                PrintWriter numberwriter = new PrintWriter(F2);
                //to screen
                Fullwriter.println("File" + CVSE.config.profileRequestsBenchmark);
                if (!CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("None")) {
                    Fullwriter.println("Stat for Queuesort=" + CVSE.config.batchqueuesortpolicy + " mergable=" + CVSE.config.taskmerge);
                } else {
                    Fullwriter.println("Stat for Queuesort=" + CVSE.config.batchqueuesortpolicy + " mergable=" + CVSE.config.taskmerge);
                }
                for (int i = 0; i < CVSE.GTS.machineInterfaces.size(); i++) {
                    MachineInterface vmi = CVSE.GTS.machineInterfaces.get(i);
                    Fullwriter.println("Machine " + i + " time elapsed:" + vmi.elapsedTime + " time actually spent processing:" + vmi.actualSpentTime);
                    Fullwriter.println("completed: " + vmi.total_taskdone + "(" + vmi.total_requestdone + ") requests, missed " + vmi.total_taskmiss + "(" + vmi.total_requestmiss + ")");
                    if(vmi instanceof MachineInterface_SimLocal) {
                        MachineInterface_SimLocal vmi_t=(MachineInterface_SimLocal)vmi;
                        int mergemiss_t=vmi_t.mergeEffectedMiss;
                        Fullwriter.println("task miss because of merging: " + mergemiss_t);
                        mergemiss+=mergemiss_t;
                    }
                    avgActualSpentTime += vmi.actualSpentTime;
                    totalWorkDone += vmi.total_taskdone;
                    ntotalWorkDone += vmi.total_requestdone;
                    totaldeadlinemiss += vmi.total_taskmiss;
                    ntotaldeadlinemiss += vmi.total_requestmiss;
                }
                Fullwriter.println("total completed: " + totalWorkDone + "(" + ntotalWorkDone + ") missed " + totaldeadlinemiss + "(" + ntotaldeadlinemiss + ")" );
                if (CVSE.GTS instanceof GOPTaskScheduler_mergable) {
                    GOPTaskScheduler_mergable GTS = (GOPTaskScheduler_mergable) CVSE.GTS;
                    System.out.println("type A merged:" + GTS.MRG.merged_tasklvl_count);
                }

                Fullwriter.println("avgspentTime " + avgActualSpentTime / CVSE.config.maxCR);
                numberwriter.println(totalWorkDone + " , " + ntotalWorkDone + " , " + totaldeadlinemiss + " , " + ntotaldeadlinemiss + " , " + avgActualSpentTime / CVSE.config.maxCR+ " , " + mergemiss);

                Fullwriter.close();
                numberwriter.close();
                F1.close();
                F2.close();
                System.out.println("Benchmark finished");
                if (CVSE.GTS instanceof GOPTaskScheduler_mergable){
                    GOPTaskScheduler_mergable GTS= (GOPTaskScheduler_mergable) CVSE.GTS;
                    System.out.println("Probe count=" + GTS.MRG.probecounter); //check how it works after refactor
                }
                Freqwriter.close();
                Freq.close();
                //sleep(200);
                //System.exit(0);
            }catch(Exception e){
                System.out.println("printstat bug:"+e);
            }
        }else {
            //to screen
            System.out.println("File" + CVSE.config.profileRequestsBenchmark);
            if (!CVSE.config.batchqueuesortpolicy.equalsIgnoreCase("None")) {
                System.out.println("Stat for Queuesort=" + CVSE.config.batchqueuesortpolicy + " mergable=" + CVSE.config.taskmerge);
            } else {
                System.out.println("Stat for Queuesort=" + CVSE.config.batchqueuesortpolicy + " mergable=" + CVSE.config.taskmerge);
            }
            for (int i = 0; i < CVSE.GTS.machineInterfaces.size(); i++) {
                MachineInterface vmi = CVSE.GTS.machineInterfaces.get(i);
                System.out.println("Machine " + i + " time elapsed:" + vmi.elapsedTime + " time actually spent:" + vmi.actualSpentTime);
                System.out.println("completed: " + vmi.total_taskdone + "(" + vmi.total_requestdone + ") requests, missed " + vmi.total_taskmiss + "(" + vmi.total_requestmiss + ")");
                avgActualSpentTime += vmi.actualSpentTime;
                totalWorkDone += vmi.total_taskdone;
                ntotalWorkDone += vmi.total_requestdone;
                totaldeadlinemiss += vmi.total_taskmiss;
                ntotaldeadlinemiss += vmi.total_requestmiss;
            }
            System.out.println("total completed: " + totalWorkDone + "(" + ntotalWorkDone + ") missed " + totaldeadlinemiss + "(" + ntotaldeadlinemiss + ")");
            if (CVSE.GTS instanceof GOPTaskScheduler_mergable){
                GOPTaskScheduler_mergable GTS= (GOPTaskScheduler_mergable) CVSE.GTS;
                System.out.println("type A merged:" + GTS.MRG.merged_tasklvl_count);
            }
            System.out.println("avgspentTime " + avgActualSpentTime / CVSE.config.maxCR);
        }

    }
}
