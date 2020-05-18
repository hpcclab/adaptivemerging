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
        for j in range(len(baseline[i])):
            newlist[i].append(baseline[i][j]-oldlist[i][j])
            #print("newSeq")
    return newlist

##########start data section
#dump raw data here, so we can calculate both average and confidence interval


#Head=[1.0, 1.4, 1.7, 1.9, 2.0, 2.1, 2.3, 2.6, 3.0, 3.5, 4.0]

#Conservative
Cons_raw=[[378,222,119,310,344,39,195,73,36,482,185,89,376,764,37,627,109,330,121,32,760,558,485,72,162,286,12,26,414,300],[1180,947,1180,1227,1047,1260,1161,1140,803,794,1259,1249,973,1371,994,1000,828,1267,977,1238,1309,1306,1002,932,798,1109,1134,1093,1174,771],[1734,1879,1762,1807,1692,1721,1872,1612,1771,1601,1743,1796,1617,1859,1698,1734,1677,1767,1611,1704,1849,1774,1692,1675,1670,1808,1754,1855,1794,1451],[2181,2337,2218,2199,2107,2092,1092,2227,2232,2025,2232,2116,2158,2337,2171,2110,2171,2223,2119,2054,2224,2225,2168,2090,2106,2187,2196,2265,2243,2150]]

#Aggressive
Agg_raw=[[381,213,85,321,132,62,218,176,121,377,184,3,217,772,51,663,93,139,132,45,608,581,173,116,149,273,11,141,101,316],[1203,630,1118,1121,1023,1249,1153,1136,669,798,1250,1206,870,1374,966,1001,851,1015,738,1228,1345,1277,997,1025,783,1065,1297,991,1165,771],[1714,1876,1751,1771,1603,1603,1816,1565,1791,1589,1744,1755,1400,1825,1644,1738,1676,1755,1604,1688,1810,1757,1679,1632,1659,1751,1728,1855,1777,1446],[2136,2315,2165,2138,2106,2005,2260,2171,2172,2015,2164,2053,2102,2239,2130,2074,2193,2205,2074,2048,2187,2200,2103,2059,2069,2141,2132,2233,2182,2151]]

#Adaptive
Adapt_raw=[[357,174,147,324,48,25,207,46,101,446,153,111,356,739,91,656,96,371,96,45,398,607,453,160,23,268,23,35,172,333],[1174,970,1109,1123,1054,1235,1159,1155,851,806,1250,1243,771,1371,939,1002,866,989,778,1189,1301,1257,840,946,795,987,1272,944,1144,731],[1711,1879,1749,1807,1587,1712,1827,1550,1744,1600,1769,1752,1605,1863,1693,1771,1643,1758,1623,1701,1836,1736,1730,1652,1674,1781,1753,1871,1760,1627],[2190,2334,2211,2193,2131,2051,2296,2215,2201,2021,2189,2124,2115,2309,2189,2118,2206,2237,2146,2059,2247,2254,2137,2114,2138,2174,2191,2278,2279,2163]]

#Nomerge
Nomerge_raw=[[432,209,147,344,400,26,431,29,204,502,305,119,375,787,49,704,98,381,191,94,897,667,55,415,101,300,30,31,329,403],[1317,859,1193,1367,1115,1330,1207,1195,867,965,1335,1269,764,1471,1211,1102,918,1103,842,1331,1381,1418,1280,1047,934,1148,1341,1146,1262,799],[1839,1949,1883,1932,1841,1814,1991,1694,1840,1750,1863,1856,1703,1978,1876,1842,1784,1833,1764,1836,1931,1922,1847,1705,1770,1904,1883,1959,1897,1548],[2373,2533,2404,2390,2384,2294,2431,2413,2391,2229,2364,2266,2289,2463,2323,2299,2406,2390,2341,2290,2419,2448,2314,2275,2301,2409,2361,2443,2451,2305]]









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
    insMeanAndCI(Cons,Cons_ci,Cons_raw,30,l)
    insMeanAndCI(Agg,Agg_ci,Agg_raw,30,l)
    insMeanAndCI(Adapt,Adapt_ci,Adapt_raw,30,l)
    #insMeanAndCI(Nomerge,Nomerge_ci,Nomerge_raw,30,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =3 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Tasks)'
ylabel='DMR saving against non merging'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1k','1.5k','2k','2.5k')
labels=['Conservative',"Adaptive",'Aggressive']
legendcolumn= 2 #number of column in the legend
data=[Cons,Adapt,Agg]
yerrdata=[Cons_ci,Adapt_ci,Agg_ci]
axes.set_ylim([0,600]) #y axis scale
ticklabelsize=18
axislabelfontsize=16

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
ax.legend(loc='upper center', prop={'size': 10},bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=legendcolumn)

plt.tight_layout()
plt.show()


