package ResourceManagement;

import TranscodingVM.TranscodingVM;

public class machineinfo {
    String type;
    String identification;
    TranscodingVM TVM;
    public machineinfo(String type, String identification, TranscodingVM TVM) {
        this.type = type;
        this.identification = identification;
        this.TVM = TVM;
    }
    public machineinfo(String type, String identification) {
        this.type = type;
        this.identification = identification;
    }
}