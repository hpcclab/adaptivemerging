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
            #newlist[i].append((baseline[i][j]-oldlist[i][j])*100.0/baseline[i][j] )
            newlist[i].append((baseline[i][j]-oldlist[i][j]) )
            #print("newSeq")
    return newlist

##########start data section
#dump raw data here, so we can calculate both average and confidence interval


#Head=[1.0, 1.4, 1.7, 1.9, 2.0, 2.1, 2.3, 2.6, 3.0, 3.5, 4.0]

#Conservative
Cons_raw=[[314,293,275,510,324,47,333,111,230,161,454,86,408,604,112,407,158,316,133,82,608,694,250,249,99,302,85,243,300,375],[1262,631,1262,1275,1339,1211,1281,1059,1005,909,1331,1190,783,1358,1034,1043,1048,909,969,1188,1351,1320,1320,1088,1096,1321,1312,1163,1271,992],[1812,1839,1796,1847,1833,1779,1819,1773,1873,1628,1860,1771,1786,1827,1775,1794,1840,1797,1737,1759,1848,1799,1849,1656,1799,1827,1803,1868,1801,1768],[2217,2333,2282,2263,2224,2193,2270,2240,2283,2150,2313,2187,2286,2288,2274,2219,2270,2262,2210,2146,2267,2247,2213,2226,2223,2245,2294,2319,2214,2219]]

ConsP_raw=[[313,282,287,500,313,46,346,131,239,159,455,93,399,609,120,416,160,321,138,83,521,708,303,246,103,310,82,248,199,385],[1288,638,1281,1267,1338,1240,1283,891,964,913,1329,1256,765,1340,1069,1053,1096,918,951,1191,1353,1339,1249,873,1131,1317,1310,1232,1278,971],[1826,1870,1816,1912,1865,1834,1867,1744,1888,1630,1891,1823,1852,1862,1802,1804,1879,1814,1779,1760,1900,1820,1841,1690,1817,1850,1830,1891,1810,1786],[2274,2379,2329,2332,2298,2225,2295,2284,2314,2179,2375,2238,2341,2330,2216,2277,2363,2297,2269,2212,2362,2279,2305,2278,2303,2285,2358,2350,2245,2263]]


#Aggressive
Agg_raw=[[323,297,281,512,289,47,337,129,231,195,455,80,426,611,120,418,158,335,132,91,602,701,249,265,101,304,83,244,187,378],[1258,654,1266,1278,1338,1219,1283,1044,992,905,1323,1191,779,1329,1041,1043,1097,920,980,1192,1348,1320,1322,1077,1079,1314,1302,1242,1268,983],[1798,1833,1778,1820,1819,1761,1811,1764,1848,1612,1827,1781,1761,1787,1770,1779,1839,1776,1734,1732,1834,1797,1834,1638,1773,1780,1780,1872,1800,1770],[2206,2292,2253,2208,2179,2141,2227,2207,2243,2143,2256,2151,2270,2239,2246,2194,2278,2217,2178,2145,2242,2205,2181,2171,2170,2207,2214,2248,2175,2196]]

AggP_raw=[[311,295,283,511,324,49,331,131,228,199,452,89,409,603,116,393,158,314,125,83,588,692,244,227,104,311,81,243,283,384],[1256,653,1262,1268,1337,1212,1283,1035,961,904,1324,1180,790,1324,1048,1043,1064,901,981,1202,1344,1319,1318,1087,1098,1326,1306,1171,1270,982],[1803,1843,1777,1817,1795,1760,1817,1762,1850,1622,1820,1769,1763,1787,1771,1779,1834,1781,1735,1734,1837,1799,1835,1652,1769,1784,1793,1874,1800,1738],[2200,2291,2252,2204,2183,2144,2190,2207,2243,2144,2256,2143,2271,2240,2239,2195,2247,2215,2179,2147,2237,2204,2183,2173,2170,2209,2212,2246,2173,2199]]

#Adaptive
Adapt_raw=[[311,285,291,511,303,48,332,133,226,156,456,88,406,595,110,411,154,306,133,86,577,691,240,241,101,303,81,239,289,387],[1261,631,1266,1260,1339,1216,1273,1042,1003,912,1333,1190,783,1360,1033,1042,1111,908,980,1185,1347,1320,1319,1081,1057,1320,1308,1244,1264,983],[1812,1839,1791,1847,1829,1779,1815,1768,1873,1625,1850,1776,1787,1826,1827,1792,1844,1787,1734,1747,1856,1798,1853,1643,1798,1820,1794,1868,1799,1767],[2222,2323,2284,2260,2226,2195,2232,2226,2284,2158,2321,2193,2288,2276,2272,2192,2295,2260,2208,2135,2293,2231,2207,2222,2222,2222,2297,2290,2224,2211]]

AdaptP_raw=[[312,282,282,510,311,52,347,107,229,147,446,75,403,612,114,404,157,316,151,81,517,715,258,221,103,311,77,244,214,386],[1289,639,1275,1243,1341,1244,1275,925,1005,907,1329,1269,789,1332,1053,1052,1099,916,993,1204,1358,1339,1252,1086,1108,1302,1316,1233,1255,967],[1826,1871,1824,1896,1873,1833,1865,1758,1865,1640,1885,1838,1822,1856,1795,1822,1876,1805,1783,1753,1898,1820,1851,1660,1807,1849,1831,1891,1795,1791],[2287,2369,2317,2328,2309,2242,2319,2296,2322,2200,2398,2232,2342,2349,2342,2273,2353,2327,2272,2211,2369,2289,2313,2281,2295,2311,2387,2333,2260,2273]]


#Nomerge
Nomerge_raw=[[433,313,290,534,359,68,385,143,248,180,460,83,415,709,113,498,174,360,171,108,710,683,285,256,114,312,90,258,279,404],[1368,973,1363,1370,1425,1385,1383,1207,1169,1010,1410,1361,848,1432,1170,1134,1204,1007,1034,1313,1441,1432,1403,1197,1189,1413,1381,1383,1366,1067],[1915,1976,1915,1969,1971,1915,1945,1872,1961,1771,1953,1876,1901,1932,1929,1898,1947,1912,1911,1850,1974,1938,1954,1785,1913,1940,1911,1973,1921,1832],[2410,2496,2451,2444,2472,2396,2415,2440,2452,2375,2467,2326,2485,2445,2437,2377,2449,2414,2436,2360,2469,2445,2426,2393,2400,2440,2446,2440,2408,2418]]









#create array for mean, and confidence interval
Cons=[]
Agg=[]
Adapt=[]
ConsP=[]
AggP=[]
AdaptP=[]
#
Cons_ci=[]
Agg_ci=[]
Adapt_ci=[]
ConsP_ci=[]
AggP_ci=[]
AdaptP_ci=[]
#calculate CI and mean
column=len(Cons_raw)

######## normalize the data
Cons_raw=normalizeSubstract(Cons_raw,Nomerge_raw)
Agg_raw=normalizeSubstract(Agg_raw,Nomerge_raw)
Adapt_raw=normalizeSubstract(Adapt_raw,Nomerge_raw)

ConsP_raw=normalizeSubstract(ConsP_raw,Nomerge_raw)
AggP_raw=normalizeSubstract(AggP_raw,Nomerge_raw)
AdaptP_raw=normalizeSubstract(AdaptP_raw,Nomerge_raw)
#find mean and stdErr
for l in range(column):
    #format: mean array, CI array, raw data, how many trials in the raw data
    #don't forget to change number 30 to number of trials you actually test
    insMeanAndCI(Cons,Cons_ci,Cons_raw,180,l)
    insMeanAndCI(Agg,Agg_ci,Agg_raw,180,l)
    insMeanAndCI(Adapt,Adapt_ci,Adapt_raw,180,l)
    insMeanAndCI(ConsP,ConsP_ci,ConsP_raw,180,l)
    insMeanAndCI(AggP,AggP_ci,AggP_raw,180,l)
    insMeanAndCI(AdaptP,AdaptP_ci,AdaptP_raw,180,l)
    #insMeanAndCI(Nomerge,Nomerge_ci,Nomerge_raw,120,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =6 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Tasks)'
#ylabel='DMSP against non merging'
ylabel='DMSR against non merging'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1k','1.5k','2k','2.5k')
labels=['Conservative','Conservative-Pfind',"Adaptive","Adaptive-Pfind",'Aggressive','Aggressive-Pfind']
legendcolumn= 2 #number of column in the legend
data=[Cons,ConsP,Adapt,AdaptP,Agg,AggP]
yerrdata=[Cons_ci,ConsP_ci,Adapt_ci,AdaptP_ci,Agg_ci,AggP_ci]
axes.set_ylim([0,300]) #y axis scale
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


