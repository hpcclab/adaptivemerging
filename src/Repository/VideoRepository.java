package Repository;

import Scheduler.SystemConfig;
import mainPackage.CVSE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class VideoRepository {
    public ArrayList<Video> videos;
    private List<RepositoryGOP> pretranscodedSegments = new ArrayList<RepositoryGOP>();
    public VideoRepository(){
        videos = new ArrayList<Video>();
    }


    public void SendVideoToSplitter(String videoName)
    {

    }
    //add both real and profile video
    public void addAllKnownVideos(){
        for(int j = 0; j< CVSE.config.repository.size(); j++) {
            File[] directories = new File(CVSE.config.repository.get(j)).listFiles(File::isDirectory);
            Arrays.sort(directories);
            for (int i = 0; i < directories.length; i++) {
                //System.out.println(directories[i].getPath() + File.separatorChar);
                videos.add(new Video(directories[i].getPath() + File.separatorChar));
            }
        }
        videos.sort(new Sortbyid());
        //validation of the order
//        for(int j=0;j<videos.size();j++) {
//            System.out.println(videos.get(j).name);
//
//        }
        //System.out.println("added "+ videos.size()+" known videos");
    }

    //only add real video
    public void addAllRealVideos(){
        for(int j = 0; j< CVSE.config.repository.size(); j++) {
            if(CVSE.config.repository.get(j).equals("repositoryvideos/realVideo")){
                File[] directories = new File(CVSE.config.repository.get(j)).listFiles(File::isDirectory);
                for (int i = 0; i < directories.length; i++) {
                    Video v = new Video(directories[i].getPath() + File.separatorChar);
                    videos.add(new Video(directories[i].getPath() + File.separatorChar));
                    //System.out.println("Video number " + i + " GOP count: " + v.repositoryGOPs.size());
                }

            }
        }
    }
}