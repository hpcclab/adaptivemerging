import os
import csv
from scipy import stats
import math

cmdlist=["RESOLUTION","FRAMERATE","BITRATE","CODEC"]
parameterlist=[]

######### stat fn
def getMeanAndStd(raw,i):
    #print(len(raw))
    #print("i="+str(i))
    n, min_max, mean, var, skew, kurt = stats.describe(raw)
    std=math.sqrt(var)
    #R = stats.norm.interval(0.95,loc=mean,scale=std/math.sqrt(i)) #definition's way
    #diff=mean-R[0]
    return mean,std,min_max

def normalizepname(astr):
    if(astr.count("BIT")>0):
        return astr.replace("BIT","")

#reading one file
def readExeData(path,data,operationlist,indexvideo):
    f= open(path) # rt option is default
    index=0
    savedvalue=0
    operation=""
    setcount=0
    GOPname=[]
    parameterlist=[]
    datacount=0
    firstdatacount=0
    indexvideo-=1
    for line in f:
        #print(line)
        if(len(line.strip())==0): #empty line
            pass 
        elif(line.count("user") == 1): #time value
            lsplit=line.split("\t")
            #print(lsplit[1])
            rsplit=lsplit[1].split("m")
            sec=(float(rsplit[0])*60+float(rsplit[1][:-2])*1000) #remove s\n from second split
            savedvalue=sec
            #print(sec)  
        elif(line.count("real") == 1): #ignore real time, it is highly effected by other tasks
            pass
        elif(line.count("GOP") == 1): #found GOP name, save the record
            lines=line.split()
            if(setcount==0):
                if(parameterlist.count(lines[2])==0): #new parameter
                    parameterlist.append(lines[2])
                if(GOPname.count(lines[1])==0): #new gop
                    GOPname.append(lines[1])
            #print("record "+str(savedvalue)+" seconds for "+lines[1]+" "+operation+" -> "+lines[2])

            index2=parameterlist.index(lines[2]) 
            index4=GOPname.index(lines[1])
            #print("i1="+str(index)+" i2="+str(index2)+" i4="+str(index3))

            if(len(data) <= index): #operation
                #print("newlist 1st layer "+"i1="+str(index)+" i2="+str(index2)+" i4="+str(index3))
                data.append([])
            if( len(data[index]) <= index2 ): #parameter
                #print("newlist 2nd layer  "+"i1="+str(index)+" i2="+str(index2)+" i4="+str(index3))
                data[index].append([])  
            if(len(data[index][index2]) <= indexvideo): #Video
                data[index][index2].append([])  
            if( len(data[index][index2][indexvideo]) <= index4 ): #GOP
                #print("newlist 3rd layer  "+"i1="+str(index)+" i2="+str(index2)+" i4="+str(index3)+"...")
                data[index][index2][indexvideo].append([])  

            data[index][index2][indexvideo][index4].append(savedvalue) #actually save the value
            datacount+=1
            #print(wholedata)
            #break
        elif(line.count("CONVERSION") == 1 or line.count("COVERSION") == 1): #first line banner, get the operation and parameter name
            lines=line.split()
            operation=lines[2]
            if(operationlist.count(operation)>0):
                pass
            else:
                print("Unknown operation found "+operation) # then add to cmd list?
                operationlist.append(operation)
                data.append([])
            index=operationlist.index(operation)
            #parameterlist[operationlist.index(operation)].append(normalizepname(lines[4])) #learn from GOP record instead
            #parameterlist[operationlist.index(operation)].append(normalizepname(lines[6]))
        elif(line.count("sys") == 1): #ignore sys
            pass
        elif(line.count("--------------------------------") == 1): #line splitter
            if(setcount==0):
                firstdatacount=datacount
            if(datacount!=firstdatacount):
                print(path+" data count inconsistency found: "+str(datacount)+" expect "+str(firstdatacount)+" data in loop "+str(setcount+1))
            datacount=0
            setcount+=1 #shut down list creation after the first set read. So that any if there is an inconsistency in data number, it maybe catched
        else:
            print("file "+path+" Unknown line: "+line)
    f.close()

#write profile to timeEstimator's file
def writeExeData(path,data,operationlist):
    f= open(path,"w") # rt option is default
    for j in range(len(data)): # 4 operation
        for k in range(len(data[j])): # 2 parameters
            for l in range(len(data[j][k])): # Video
                for m in range(len(data[j][k][l])): # GOP
                    #for adata in data[j][k][l][m]: #trial, no need to loop through them
                        #pass
                    mean,std,min_max=getMeanAndStd(data[j][k][l][m],len(data[j][k][l][m]))
                    # now, we have std, mean, min max. Then what to do?
                    # use gopname as video_id and gop number together
                    f.write(cmdlist[j]+","+str(k)+","+str(l+1)+"_"+str(m)+","+str(mean)+','+str(std)+'\n')

    f.close()
#create repository for CVSE project
def createRepository(path,data):
    fcount=0
    for vcount in range(len(data[0][0])):
        if not os.path.exists(path+str(vcount+1)):
            os.mkdir(path+str(vcount+1))
        else:
            print(str(vcount+1)+" exist")
        f= open(path+str(vcount+1)+"/list.txt","wt")
        f.write("l\n")
        start=0
        #print(fcount)
        #print(video[4])
        for index in range(len(data[0][0][vcount])-1):
            f.write(str(index+1) +" "+str(start)+"\n")
            start=start+2000 #2 seconds   
            #if index== int(video[3])-1:
                #lastlength=2*int(video[3])-int(video[2])
                #print(str(index)+" "+ str(lastlength))
            #else:
                #print(str(index)+" "+ "2")
        f.close()
    fcount=fcount+1


wholedata=[ [] for i in range(len(cmdlist))] #[OPERATION][PARAMETER] then [Video][GOP][TRIAL] later
outprofile="NchameleonVM.txt"
numfile=100 #actually 100 but can set to low number for quick test

for i in range(1,numfile+1): #reading part
    readExeData("./VMbenchmarkingCC/"+str(i)+"/bitrateDuration.txt",wholedata,cmdlist,i)
    readExeData("./VMbenchmarkingCC/"+str(i)+"/codecDuration.txt",wholedata,cmdlist,i)
    readExeData("./VMbenchmarkingCC/"+str(i)+"/framerateDuration.txt",wholedata,cmdlist,i)
    readExeData("./VMbenchmarkingCC/"+str(i)+"/resolutionDuration.txt",wholedata,cmdlist,i)


writeExeData(outprofile,wholedata,cmdlist) #find mean and sd then save to timeestimator table
createRepository("./videoData/",wholedata) #create repository for CVSE project
#print("wholedata="+str(wholedata))
#print(parameterlist)
#for cindex in range(len(cmdlist)):
#    wholedata.append([])
#    for pindex in range(len(paramlist[cindex])):
#        wholedata[cindex].append([])

