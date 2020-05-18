import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import math
def getMeanAndCI(ontimes,i):
    n, min_max, mean, var, skew, kurt = stats.describe(ontimes)
    std=math.sqrt(var)
    R = stats.norm.interval(0.95,loc=mean,scale=std/math.sqrt(i)) #definition's way
    #R = stats.norm.interval(0.05,loc=mean,scale=std) #dr.Amini's dropbox file way
    diff=mean-R[0]
    print("mean="+str(mean))
    return mean,diff
    #ci=diff/int(i)*100 #dr.Amini's dropbox file way
    #return ci #dr.Amini's dropbox file way

def insMeanAndCI(mean,CI,raw,count,l):
    cin=getMeanAndCI(raw[l],count)
    mean.append(cin[0])
    CI.append(cin[1])

#function specific to each graph, if it need some normalize    
def normalizeSubstract(oldlist,baseline):
    newlist=[]
    sequence=0
    for i in range(len(baseline)):
        newlist.append([])
        mean=sum(baseline[i])/len(baseline[i])
        for j in range(len(baseline[i])):
            newlist[i].append((baseline[i][j]-oldlist[i][j])*100.0/mean )
            #newlist[i].append((baseline[i][j]-oldlist[i][j]) )
            #print("newSeq")
    return newlist

##########start data section
#dump raw data here, so we can calculate both average and confidence interval


#Head=[1.0, 1.4, 1.7, 1.9, 2.0, 2.1, 2.3, 2.6, 3.0, 3.5, 4.0]

#Conservative
Cons_raw=[[283,221,135,369,134,19,298,32,35,500,470,97,295,756,105,697,109,391,131,72,623,506,291,435,170,294,40,74,70,357],[1227,884,1212,1317,1277,1226,1254,1183,818,865,1343,1285,718,1378,1027,1028,886,1026,777,1199,1331,1297,1147,1146,867,1032,1282,1161,1188,734],[1746,1886,1768,1866,1724,1759,1869,1690,1797,1629,1878,1783,1540,1886,1745,1808,1707,1770,1662,1741,1814,1775,1809,1715,1710,1810,1813,1871,1824,1472],[2196,2316,2264,2252,2194,2131,2301,2236,2227,2175,2305,2125,2254,2329,2216,2164,2203,2261,2148,2085,2247,2271,2224,2145,2186,2235,2235,2290,2245,2254]]

#Aggressive
Agg_raw=[[282,219,212,361,465,20,287,37,84,525,446,97,272,762,108,697,110,399,131,82,631,509,231,430,169,279,38,80,76,277],[1201,799,1214,1319,1279,1220,1250,1235,783,862,1334,1285,864,1381,1024,1025,892,1020,779,1182,1336,1301,1106,1149,1011,1077,1297,1150,1202,842],[1726,1885,1765,1840,1730,1753,1857,1685,1784,1626,1867,1789,1542,1871,1707,1798,1710,1772,1657,1715,1811,1782,1799,1682,1696,1811,1746,1875,1799,1547],[2161,2303,2228,2190,2156,2058,2272,2199,2191,2145,2253,2100,2241,2317,2198,2116,2199,2317,2116,2086,2201,2243,2220,2119,2143,2218,2167,2247,2223,2244]]

#Adaptive
Adapt_raw=[[281,218,136,366,136,24,271,35,89,507,455,6,281,760,103,698,108,395,108,89,543,482,284,425,177,320,38,77,99,264],[1197,732,1197,1317,1264,1223,1248,1221,805,864,1343,1280,864,1390,1025,1029,893,1028,779,1182,1330,1291,1149,1149,1039,1037,1285,1161,1191,727],[1726,1886,1773,1844,1705,1772,1861,1706,1770,1631,1879,1803,1550,1886,1762,1794,1709,1775,1650,1745,1824,1786,1808,1716,1707,1815,1814,1866,1828,1458],[2197,2337,2269,2252,2198,2131,2304,2244,2221,2191,2331,2132,2254,2317,2230,2170,2213,2253,2148,2097,2263,2264,2233,2123,2185,2247,2167,2288,2251,2259]]

#Nomerge
Nomerge_raw=[[485,328,256,386,114,57,363,169,94,512,463,99,310,793,116,735,110,394,170,148,873,486,274,461,188,297,38,89,78,440],[1273,1014,1291,1352,1333,1292,1325,1216,862,960,1442,1364,784,1465,1144,1129,1031,1181,900,1340,1414,1437,1377,1019,976,1251,1388,1356,1287,800],[1868,2010,1888,1954,1744,1873,2005,1798,1888,1748,1977,1888,1613,2001,1814,1866,1795,1922,1810,1853,1972,1929,1884,1807,1811,1945,1928,1977,1972,1586],[2360,2514,2458,2443,2445,2272,2485,2440,2391,2391,2496,2280,2447,2522,2389,2336,2365,2404,2363,2324,2453,2485,2385,2315,2335,2429,2423,2445,2498,2415]]









#create array for mean, and confidence interval
Cons=[]
Agg=[]
Adapt=[]
#
Cons_ci=[]
Agg_ci=[]
Adapt_ci=[]
#calculate CI and mean
column=len(Cons_raw)

######## normalize the data
Cons_raw=normalizeSubstract(Cons_raw,Nomerge_raw)
Agg_raw=normalizeSubstract(Agg_raw,Nomerge_raw)
Adapt_raw=normalizeSubstract(Adapt_raw,Nomerge_raw)

#find mean and stdErr
for l in range(column):
    #format: mean array, CI array, raw data, how many trials in the raw data
    #don't forget to change number 30 to number of trials you actually test
    insMeanAndCI(Cons,Cons_ci,Cons_raw,180,l)
    insMeanAndCI(Agg,Agg_ci,Agg_raw,180,l)
    insMeanAndCI(Adapt,Adapt_ci,Adapt_raw,180,l)
    #insMeanAndCI(Nomerge,Nomerge_ci,Nomerge_raw,120,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =3 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Service Requests)'
#ylabel='DMSP against non merging'
ylabel='Miss Rate Reduction (%)'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1k','1.5k','2k','2.5k')
labels=['Conservative',"Adaptive",'Aggressive']
legendcolumn= 2 #number of column in the legend
data=[Cons,Adapt,Agg]
yerrdata=[Cons_ci,Adapt_ci,Agg_ci]
axes.set_ylim([0,30]) #y axis scale
ticklabelsize=18
axislabelfontsize=18

############
#auto calculated values and some rarely change config, can also overwrite
axes.set_xlim([-0.5, len(xtick)-0.5]) #y axis
font = {'family' : 'DejaVu Sans',
        #'weight' : 'bold',
        'size'   : 16 }
bar_width =1.0/(n_groups+2) 
edgecols=['royalblue','forestgreen','red','mediumblue','orange','pink','limegreen','lightblue','darkgreen'] #prepared 9 colors
#hatch_arr=[".","x"]
hatch_arr=["////","ooo",".\\\\\\","----","**","xxx","+++",".///","////////"] #prepared 9 hatch style
opacity = 1 #chart opacity
offsetindex=(n_groups-1)/2.0


############
#plot section
plt.rc('font', **font)
index = np.arange(n_point)
print("data"+str(data))
print("yerrdata"+str(yerrdata))
for i in range(0,n_groups):
    #draw internal hatch, and labels
    plt.bar(index - (offsetindex-i)*bar_width, data[i], bar_width,
                     alpha=opacity,                 
                     hatch=hatch_arr[i],
                    #color=edgecols[i],
                	 color='white',
		     edgecolor=edgecols[i],
             label=labels[i],
		     lw=1.0,
		     zorder = 0)
    #draw black liner and error bar
    plt.bar(index - (offsetindex-i)*bar_width, data[i], bar_width, yerr =
		    yerrdata[i],                              
                    color='none',
		    error_kw=dict(ecolor='black',capsize=3),
                    edgecolor='k',
		    zorder = 1,
		    lw=1.0)

plt.tick_params(axis='both', which='major', labelsize=ticklabelsize)
plt.tick_params(axis='both', which='minor', labelsize=ticklabelsize)
plt.xlabel(xlabel,fontsize=axislabelfontsize)
plt.ylabel(ylabel,fontsize=axislabelfontsize)
#plt.title('Execution time (deadline sorted batch queue)') #generally, we add title in latex
ax.set_xticks(index)
ax.set_xticklabels(xtick)
ax.legend(loc='upper center', prop={'size': 16},bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=legendcolumn)

plt.tight_layout()
plt.show()


