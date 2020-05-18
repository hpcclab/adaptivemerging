package Streampkg;

import Scheduler.GOPTaskScheduler;
import Scheduler.SystemConfig;
import mainPackage.CVSE;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by vaughanv21 on 2/1/2019.
 */
public class StreamManager {

    public void InitializeStream(int num){

    }

    public void RemoveProcessedStreams() throws IOException {
        File[] directories = new File("streams").listFiles(File::isDirectory);

        for (int i=0;i<directories.length;i++){
            removeFolder(directories[i].getPath());
        }
    }

    private void removeFolder(String name) throws IOException {
        Path directory = Paths.get(name);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file); // this will work because it's always a File
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); //this will work because Files in the directory are already deleted
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void CreateDirectory(Settings userRequest){
        File dir = new File(userRequest.outputDir());
        if (dir.exists()){
            return;
        }
        System.out.println("User Request Output Directory: " + userRequest.outputDir());

        String absPath = "/home/pi/Documents/VHPCC/workspace/CVSS_impl";

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        String[] command = {"bash", CVSE.config.path + "/bash/createDir.sh", userRequest.outputDir(), CVSE.config.path, userRequest.videoname};

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen
            pb.redirectError(ProcessBuilder.Redirect.INHERIT); //debug,make output from bash to screen

            pb.start();
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e){

        }
    }

    public void InitializeStream(int videoIndex, Settings userRequest,  GOPTaskScheduler GTS){
        System.out.println("before stream");
        // create Stream from Video, there are 3 constructor for Stream, two for making from only certain segment (not all)

        //Settings newRequest = new Settings(userRequest.videoname, userRequest.resHeight, userRequest.resWidth);

        Stream ST=new Stream(CVSE.VR.videos.get(videoIndex),userRequest); //admission control can work in constructor, or later?

        CreateDirectory(userRequest);

        //Admission Control assign Priority of each segments
        for(int i=0;i<ST.streamGOPs.size();i++){
            File dir = new File(ST.streamGOPs.get(i).outputFile());
            if (dir.exists()){
                ST.streamGOPs.remove(i);
                i--;
            }
        }
        CVSE.AC.AssignStreamPriority(ST);

        System.out.println("Number of Stream GOPs in stream: " + ST.streamGOPs.size());
        //Scheduler
        System.out.println("before stream");
        GTS.addStream(ST);
        System.out.println("after stream");
    }
}

