package Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;



class Sortbyid implements Comparator<Video>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Video a, Video b)
    {
        return a.id - b.id;
    }
}
public class Video{

    public ArrayList<RepositoryGOP> repositoryGOPs= new ArrayList<RepositoryGOP>();;

    private int totalSegments = 0;
    public int id;
    public String name;

    public Video(){
    }
    public Video(String path){
        //set name
        String pathsplit[];
        if(File.separatorChar=='\\'){
         pathsplit=path.split("\\\\");
        }else{
        pathsplit=path.split(File.separator);
    }
        name=pathsplit[pathsplit.length-1];
        File listfile = new File(path + "list.txt");
        try{
            id=Integer.parseInt(name);
        }catch(Exception e){
            System.out.println("video id "+id +"is not an integer");
            id=0;
        }
        if(listfile.exists()){
            //list mode
            try {
               // System.out.print("list mode\n\n");
                //System.out.println("look for path:"+path);
                Scanner scanner=new Scanner(listfile);
                String[] sp=scanner.nextLine().split("\\s+");
                //System.out.println("split size="+sp.length);
                if(sp[0].equalsIgnoreCase("g")){ //auto generate mode
                    //System.out.println("g mode");
                    int n=Integer.parseInt(sp[1]);
                    for(int i=0;i<n;i++){
                        String GOPname=i+".ts";
                        RepositoryGOP repositoryGop = new RepositoryGOP(path+GOPname,i*500);
                        this.addGOP(repositoryGop);
                    }
                }else if(sp[0].equalsIgnoreCase("l")){ //list mode
                    //System.out.println("l mode");
                    while(scanner.hasNext()){ // didn't test
                        String aLine[]=scanner.nextLine().split("\\s+");
                        if(aLine.length==2) {
                            //System.out.println("correct format");
                            String GOPname =aLine[0];
                            int presentationTime=Integer.parseInt(aLine[1]);
                            RepositoryGOP repositoryGop = new RepositoryGOP(path + GOPname,presentationTime);
                            this.addGOP(repositoryGop);
                        }else{
                            System.out.println("repository list mode incorrect format");
                        }
                    }
                }else{
                    System.out.println("uncrecognize a mode in list.txt");
                }
            } catch (FileNotFoundException e) {
                System.out.println("videorepository can not find list.txt or read fail");
                //e.printStackTrace();
            }


        }else{
            System.out.print("directory scan\n\n");
            //scan mode
            File[]  files= new File(path).listFiles();
            if (files != null) {
                //System.out.println(files.length);
                for (int i = 0; i < files.length; i++) {
                    //System.out.println(i+" "+files[i].getName() +" "+files[i].getPath()); //DEBUG
                    if (!files[i].isDirectory()) {
                        String fileName = files[i].getName();
                        //check if extension is not m3u8
                        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                        if (!extension.equalsIgnoreCase("m3u8") && !extension.equalsIgnoreCase("txt")) {
                            RepositoryGOP repositoryGop = new RepositoryGOP(files[i].getPath());
                            this.addGOP(repositoryGop);
                        }
                    }
                }
            }
        }

    }
    public int getTotalSegments()
    {
        return totalSegments;
    }

    public void setTotalSegments(int totalSegments)
    {
        if(totalSegments >= 0)
        {
            this.totalSegments = totalSegments;
        }
        else
        {
            this.totalSegments = -1;
        }

    }

    public void addGOP(RepositoryGOP repositoryGop){
        repositoryGOPs.add(repositoryGop);
        totalSegments++;
    }

}