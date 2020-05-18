package Simulator;

class requestprofile implements Comparable<requestprofile> {
    public int videoChoice;
    public String command;
    public String setting;
    public long appearTime;
    public long deadline;
    public int startgopnum;
    public int endgopnum;

    //recipe is at Video level, each presentation time of each GOPS are in repository video
    public requestprofile(int videoChoice, String command, String setting, long appearTime, long deadline,int startnum,int endnum) {
        this(videoChoice,command,setting,appearTime,deadline);
        this.startgopnum=startnum;
        this.endgopnum=endnum;

    }
    public requestprofile(int videoChoice, String command, String setting, long appearTime, long deadline) {
        this.videoChoice = videoChoice;
        this.command = command;
        this.setting = setting;
        this.appearTime = appearTime;
        this.deadline = deadline;
        this.startgopnum=endgopnum=-1; //if not set, then -1 on both
    }
    @Override // (compare by appearTime )
    public int compareTo(requestprofile requestprofile) {
        if (requestprofile != null) {
            if (this.appearTime > requestprofile.appearTime) {
                return 1;
            } else if (this.appearTime < requestprofile.appearTime) {
                return -1;
            }
            return 0;
        }
        return -1;
    }
}