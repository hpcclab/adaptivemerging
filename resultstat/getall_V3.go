package main

import (
    "os"
	"fmt"
	//"time"
    "log"
    "io/ioutil"
    "strings"
    "strconv"
    "math"
    "sort"
)
//concat 50 files into 1 files, and also to a summery.txt
func concat(inp string){
    count := make(map[string]int)  
    meanExeT := make(map[string]float64)
    var keys []string
    eachExeT := make(map[string][]float64)  
    meanMiss := make(map[string]int)  
    meanMisspercent := make(map[string]float64)  
    eachMiss := make(map[string][]int32) 
    eachMisspercent := make(map[string][]float64) 
    SDexet := make(map[string]float64)
    meanDone := make(map[string]int) 
    inducedMiss := make(map[string]int) 
    file, err := os.Open(inp)
    splitS := strings.Split(inp,"/")
    fName :=splitS[1]
	if err != nil {
		log.Fatalf("failed opening directory: %s", err)
	}
	defer file.Close()

    fileO, err2 := os.Create("sum/"+fName+".txt")
	if err2 != nil {
		log.Fatalf("failed opening directory: %s", err2)
	}
    defer fileO.Close()
	list,_ := file.Readdirnames(0) // 0 to read all files and folders
	for _, name := range list {
        b, err := ioutil.ReadFile(inp+"/"+name)
        if err != nil {
            fmt.Println(err)
        }
        str := string(b)
		fmt.Fprint(fileO,name +" , "+ str)
        //store data to be summerize
        category :=strings.Split(name,"_s")[0]
        var strsplit[] string =strings.Split(str," , ")
        //fmt.Println(strsplit[4])
        //ExeT
        valz,_ :=strconv.Atoi( strings.Split(strsplit[4],"\n")[0] )        
        var val float64=float64(valz)/1000.0*8 //convert unit, /8 but *8 back to make it total exe time
        //fmt.Println(val)
        meanExeT[category]+=val    
        eachExeT[category]= append(eachExeT[category], float64(val)) 
        //mean miss
        val2,_ :=strconv.Atoi(strsplit[1])
        val4,_ :=strconv.Atoi(strsplit[3])
        val6,_ :=strconv.Atoi(strings.Split(strsplit[5],"\n")[0])
        meanMiss[category]+=val4    
        eachMiss[category]= append(eachMiss[category], int32(val4)) 
        var thismiss=float64(val4)/float64(val2)*100
        meanMisspercent[category]+=thismiss
        eachMisspercent[category]= append(eachMisspercent[category], thismiss) 
        meanDone[category]+=val2 
        inducedMiss[category]+=val6 

        count[category]+=1
	}
    // summerize
    file3, err3 := os.Create("sum/summerize_"+fName+".txt")
	if err3 != nil {
		log.Fatalf("failed opening directory: %s", err3)
	}
    defer file3.Close()

    for k,_ :=range meanExeT{
        keys=append(keys,k)

    }
    sort.Strings(keys)

//print ext_all, and calc mean
    fmt.Fprint(file3,"ExeT_All=[")
    var firstlv1=0
    for _,k := range keys {
    fmt.Println(k)
        meanExeT[k]  = meanExeT[k]/float64(count[k])
        if(firstlv1!=0){
            fmt.Fprint(file3,",[")
        }else{
            fmt.Fprint(file3,"[")
            firstlv1=1
        }
        var firstlv2 =0
        for _,j := range eachExeT[k]{
            SDexet[k] +=math.Pow(j-float64(meanExeT[k]),2)/float64(count[k])
            if firstlv2!=0{
                fmt.Fprintf(file3,",%6f",j)
            }else{
                fmt.Fprintf(file3,"%6f",j)
                firstlv2=1;
            }
        }
        fmt.Fprint(file3,"]")
    }
    fmt.Fprintln(file3,"]")
//print miss_percent_all, and calc miss
    fmt.Fprint(file3,"miss_All=[")
    firstlv1=0
    for _,k := range keys {
    fmt.Println(k)
        meanMiss[k]  = meanMiss[k]/count[k]
        meanMisspercent[k]  = meanMisspercent[k]/float64(count[k])        
        if(firstlv1!=0){
            fmt.Fprint(file3,",[")
        }else{
            fmt.Fprint(file3,"[")
            firstlv1=1
        }
        var firstlv2 =0
        for _,j := range eachMiss[k]{
            if firstlv2!=0{
                fmt.Fprintf(file3,",%d",j)
            }else{
                fmt.Fprintf(file3,"%d",j)
                firstlv2=1;
            }
        }
        fmt.Fprint(file3,"]")
    }
    fmt.Fprintln(file3,"]")

    //exeT
    fmt.Fprint(file3,"ExeT=(")
    for _,k := range keys {
        fmt.Fprint(file3,meanExeT[k])
        fmt.Fprint(file3,",")
    }
    fmt.Fprintln(file3,")")

    //miss
    fmt.Fprint(file3,"miss=(")
    for _,k := range keys {
        fmt.Fprint(file3,meanMiss[k])
        fmt.Fprint(file3,",")
    }
    fmt.Fprintln(file3,")")

    //misspercent
    fmt.Fprint(file3,"mean_misspercent=(")
    for _,k := range keys {
        fmt.Fprint(file3,meanMisspercent[k])
        fmt.Fprint(file3,",")
    }
    fmt.Fprintln(file3,")")

    //missInduced
    fmt.Fprint(file3,"mean_missinduced=(")
    for _,k := range keys {
        fmt.Fprint(file3,inducedMiss[k])
        fmt.Fprint(file3,",")
    }
    fmt.Fprintln(file3,")")
}
// error rate = SD/ sqrt(n) -> n=10 here

func main() {

    folders := [11]string{"2020JansetFormat/Inplace_adaptive","2020JansetFormat/Inplace_aggressive","2020JansetFormat/Inplace_conservative","2020JansetFormat/Inplace_unmerge","2020JansetFormat/pos_adaptive","2020JansetFormat/pos_aggressive","2020JansetFormat/pos_conservative","2020JansetFormat/dlsort_adaptive","2020JansetFormat/dlsort_aggressive","2020JansetFormat/dlsort_conservative","2020JansetFormat/dlsort_nomerge"}
    for i:=0; i<11; i+=1 {	
        concat(folders[i])
    }

    //concat("finished/alwaysmerge_Unsort/numbers/");
	//fmt.Println("The time is", time.Now())
}

