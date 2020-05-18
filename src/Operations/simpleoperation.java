package Operations;

public class simpleoperation {
    public String operationname;
    public String batchfile;
    public simpleoperation(String name, String bfile){
        operationname=name;
        batchfile=bfile;
    }
    // mode=0 is for simulation, mode=1 is for real
    public String toCMD(String input, String output, String parameter,int mode){
        if(mode==0){ //for sim mode, do nothing to convert the parameter to command
            return parameter;
        }else{
            //ProcessBuilder pb = new ProcessBuilder();
            //call "./bash/"+batchfile in the String (set in GOPTaskScheduler)

        }
        return "";
        // can be extend to more complex stuff
        //for example FFmpeg -i input -r parameter -o output
    }
}
