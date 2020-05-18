package main

import (
    "os"
	"fmt"
	//"time"
    "log"
    "io/ioutil"
    "strings"
    //"strconv"
    //"math"
    "sort"
)

func chkComplete(inp string){
    expectedCount :=30
    count := make(map[string]int)  
    catlist := make(map[string][]string)  
    //var keys []string
    file, err := os.Open(inp)
	if err != nil {
		log.Fatalf("failed opening directory: %s", err)
	}
	defer file.Close()

    list,_ := file.Readdirnames(0) // 0 to read all files and folders
    sort.Strings(list)
	for _, name := range list {
        _, err := ioutil.ReadFile(inp+"/"+name)
        if err != nil {
            fmt.Println(err)
        }
        //store data to be summerize
        category :=strings.Split(name,"_s")[0]
        count[category]+=1
        catlist[category]=append(catlist[category],strings.Split(strings.Split(name,"_s")[1],".txt")[0] )
        
	}
    first:=0
    for cat, catcount := range count{
        sort.Strings(catlist[cat]) 
        if(catcount!=expectedCount){           
            fmt.Println(cat," has ",catcount)
            fmt.Println(catlist[cat])
        }else{
            if(first==0){
                first=1
                fmt.Println("completed list")
                fmt.Println(catlist[cat])
            }
        }
    }

}

func main() {

    folders := [1]string{"full/"}
    for i:=0; i<1; i+=1 {	
        chkComplete(folders[i])
    }

    //concat("finished/alwaysmerge_Unsort/numbers/");
	//fmt.Println("The time is", time.Now())
}

