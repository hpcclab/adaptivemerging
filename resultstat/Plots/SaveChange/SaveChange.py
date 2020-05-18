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
    #print(str(mean)+" "+str(CI)+" "+str(count)+" "+str(l)+"\n")
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

Cons_raw=[[314,293,275,510,324,47,333,111,230,161,454,86,408,604,112,407,158,316,133,82,608,694,250,249,99,302,85,243,300,375],[1262,631,1262,1275,1339,1211,1281,1059,1005,909,1331,1190,783,1358,1034,1043,1048,909,969,1188,1351,1320,1320,1088,1096,1321,1312,1163,1271,992],[1812,1839,1796,1847,1833,1779,1819,1773,1873,1628,1860,1771,1786,1827,1775,1794,1840,1797,1737,1759,1848,1799,1849,1656,1799,1827,1803,1868,1801,1768],[2217,2333,2282,2263,2224,2193,2270,2240,2283,2150,2313,2187,2286,2288,2274,2219,2270,2262,2210,2146,2267,2247,2213,2226,2223,2245,2294,2319,2214,2219]]

Agg_raw=[[323,297,281,512,289,47,337,129,231,195,455,80,426,611,120,418,158,335,132,91,602,701,249,265,101,304,83,244,187,378],[1258,654,1266,1278,1338,1219,1283,1044,992,905,1323,1191,779,1329,1041,1043,1097,920,980,1192,1348,1320,1322,1077,1079,1314,1302,1242,1268,983],[1798,1833,1778,1820,1819,1761,1811,1764,1848,1612,1827,1781,1761,1787,1770,1779,1839,1776,1734,1732,1834,1797,1834,1638,1773,1780,1780,1872,1800,1770],[2206,2292,2253,2208,2179,2141,2227,2207,2243,2143,2256,2151,2270,2239,2246,2194,2278,2217,2178,2145,2242,2205,2181,2171,2170,2207,2214,2248,2175,2196]]

Adapt_raw=[[311,285,291,511,303,48,332,133,226,156,456,88,406,595,110,411,154,306,133,86,577,691,240,241,101,303,81,239,289,387],[1261,631,1266,1260,1339,1216,1273,1042,1003,912,1333,1190,783,1360,1033,1042,1111,908,980,1185,1347,1320,1319,1081,1057,1320,1308,1244,1264,983],[1812,1839,1791,1847,1829,1779,1815,1768,1873,1625,1850,1776,1787,1826,1827,1792,1844,1787,1734,1747,1856,1798,1853,1643,1798,1820,1794,1868,1799,1767],[2222,2323,2284,2260,2226,2195,2232,2226,2284,2158,2321,2193,2288,2276,2272,2192,2295,2260,2208,2135,2293,2231,2207,2222,2222,2222,2297,2290,2224,2211]]

Nomerge_raw=[[433,313,290,534,359,68,385,143,248,180,460,83,415,709,113,498,174,360,171,108,710,683,285,256,114,312,90,258,279,404],[1368,973,1363,1370,1425,1385,1383,1207,1169,1010,1410,1361,848,1432,1170,1134,1204,1007,1034,1313,1441,1432,1403,1197,1189,1413,1381,1383,1366,1067],[1915,1976,1915,1969,1971,1915,1945,1872,1961,1771,1953,1876,1901,1932,1929,1898,1947,1912,1911,1850,1974,1938,1954,1785,1913,1940,1911,1973,1921,1832],[2410,2496,2451,2444,2472,2396,2415,2440,2452,2375,2467,2326,2485,2445,2437,2377,2449,2414,2436,2360,2469,2445,2426,2393,2400,2440,2446,2440,2408,2418]]

Cons5_raw=[[323,296,278,517,309,45,341,140,238,153,454,73,404,600,106,406,157,312,127,88,592,692,215,211,107,309,81,241,234,373],[1259,633,1267,1281,1336,1217,1283,1018,955,908,1331,1197,771,1360,1046,1042,1091,896,969,1196,1347,1320,1322,1080,1071,1314,1311,1139,1270,986],[1814,1837,1796,1849,1832,1778,1815,1772,1873,1622,1860,1814,1790,1827,1783,1795,1839,1796,1738,1746,1856,1382,1846,1664,1798,1818,1808,1866,1801,1764],[2217,2338,2284,2260,2228,2189,2269,2237,2285,2153,2313,2181,2279,2301,2276,2219,2273,2262,2218,2150,2266,2246,2207,2224,2220,2244,2296,2295,2208,2221]]

Agg5_raw=[[323,297,282,506,302,42,341,126,223,165,447,92,398,594,118,414,163,323,130,87,608,700,248,236,109,312,81,244,274,379],[1258,648,1265,1276,1339,1220,1287,1109,976,913,1323,1188,787,1327,1016,1045,1103,918,956,1193,1346,1320,1320,1082,1091,1325,1307,1142,1268,994],[1803,1827,1777,1817,1820,1759,1817,1766,1849,1611,1820,1785,1758,1785,1768,1781,1836,1779,1735,1726,1837,1794,1829,1647,1773,1783,1789,1848,1796,1742],[2205,2289,2252,2209,2180,2145,1790,2203,2243,2147,2254,2150,2271,2239,2246,2185,2277,2216,2179,2145,2237,2205,2193,2174,2170,2210,2219,2248,2175,2201]]

Adapt5_raw=[[326,295,280,518,301,45,329,133,229,164,452,76,412,598,118,405,159,326,128,86,590,690,214,224,102,315,80,239,286,390],[1262,643,1272,1273,1336,1223,1276,1011,1017,909,1332,1203,770,1361,1046,1043,1111,921,978,1183,1349,1320,1321,1077,1074,1325,1304,1167,1272,975],[1812,1836,1791,1853,1831,1777,1818,1763,1873,1622,1863,1775,1795,1825,1792,1794,1836,1799,1728,1754,1851,1800,1852,1651,1803,1817,1797,1872,1804,1735],[2215,2306,2284,2260,2224,2192,2238,2230,2282,2158,2323,2188,2280,2275,2281,2199,2297,2267,2211,2135,2297,2230,2213,2197,2218,2235,2298,2294,2229,2206]]


Nomerge5_raw=[[431,314,289,538,335,62,390,144,252,195,459,82,402,720,113,514,172,357,174,108,721,685,321,276,117,302,83,268,313,397],[1368,956,1348,1385,1425,1385,1387,1219,1053,1011,1411,1360,819,1428,1178,1135,1188,985,998,1321,1441,1432,1403,1183,1183,1414,1380,1394,1367,1072],[1918,1972,1918,1969,1969,1917,1945,1871,1961,1765,1952,1882,1904,1932,1930,1899,1946,1909,1912,1851,1974,1938,1953,1789,1915,1940,1913,1972,1924,1834],[2409,2496,2437,2463,2464,2395,2416,2440,2451,2372,2467,2329,2485,2445,2437,2379,2448,2414,2441,2360,2468,2444,2430,2395,2400,2442,2445,2438,2407,2420]]




#create array for mean, and confidence interval
Cons=[]
Agg=[]
Adapt=[]
Cons5=[]
Agg5=[]
Adapt5=[]
Cons10=[]
Agg10=[]
Adapt10=[]
#
Cons_ci=[]
Agg_ci=[]
Adapt_ci=[]
Cons5_ci=[]
Agg5_ci=[]
Adapt5_ci=[]
Cons10_ci=[]
Agg10_ci=[]
Adapt10_ci=[]
#calculate CI and mean
column=len(Cons_raw)

######## normalize the data
Cons_raw=normalizeSubstract(Cons_raw,Nomerge_raw)
Agg_raw=normalizeSubstract(Agg_raw,Nomerge_raw)
Adapt_raw=normalizeSubstract(Adapt_raw,Nomerge_raw)

Cons5_raw=normalizeSubstract(Cons5_raw,Nomerge5_raw)
Agg5_raw=normalizeSubstract(Agg5_raw,Nomerge5_raw)
Adapt5_raw=normalizeSubstract(Adapt5_raw,Nomerge5_raw)

#Cons10_raw=normalizeSubstract(Cons10_raw,Nomerge10_raw)
#Agg10_raw=normalizeSubstract(Agg10_raw,Nomerge10_raw)
#Adapt10_raw=normalizeSubstract(Adapt10_raw,Nomerge10_raw)
#find mean and stdErr
for l in range(column):
    #format: mean array, CI array, raw data, how many trials in the raw data
    #don't forget to change number 30 to number of trials you actually test
    insMeanAndCI(Cons,Cons_ci,Cons_raw,180,l)
    insMeanAndCI(Agg,Agg_ci,Agg_raw,180,l)
    insMeanAndCI(Adapt,Adapt_ci,Adapt_raw,180,l)
    insMeanAndCI(Cons5,Cons5_ci,Cons5_raw,180,l)
    insMeanAndCI(Agg5,Agg5_ci,Agg5_raw,180,l)
    insMeanAndCI(Adapt5,Adapt5_ci,Adapt5_raw,180,l)
    #insMeanAndCI(Cons10,Cons10_ci,Cons10_raw,180,l)
    #insMeanAndCI(Agg10,Agg10_ci,Agg10_raw,180,l)
    #insMeanAndCI(Adapt10,Adapt10_ci,Adapt10_raw,180,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =6 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Tasks)'
#ylabel='DMSP against non merging'
ylabel='Percentage of miss saving'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1.0k','1.5k','2.0k','2.5k')
labels=['Conservative','Conservative-hs',"Adaptive","Adaptive-hs",'Aggressive','Aggressive-hs']
legendcolumn= 2 #number of column in the legend
data=[Cons,Cons5,Adapt,Adapt5,Agg,Agg5]
yerrdata=[Cons_ci,Cons5_ci,Adapt_ci,Adapt5_ci,Agg_ci,Agg5_ci]

#data=[Cons,Cons5,Cons10,Adapt,Adapt5,Adapt10,Agg,Agg5,Agg10]
#yerrdata=[Cons_ci,Cons5_ci,Cons10_ci,Adapt_ci,Adapt5_ci,Adapt10_ci,Agg_ci,Agg5_ci,Agg10_ci]
axes.set_ylim([0,30]) #y axis scale
ticklabelsize=18
axislabelfontsize=16

############
#auto calculated values and some rarely change config, can also overwrite
axes.set_xlim([-0.5, len(xtick)-0.5]) #y axis
font = {'family' : 'DejaVu Sans',
        #'weight' : 'bold',
        'size'   : 16 }
bar_width =1.0/(n_groups+2) 
#edgecols=['royalblue','lightblue','mediumblue','forestgreen','limegreen','darkgreen','red','orange','pink'] #prepared 9 colors
edgecols=['royalblue','lightblue','forestgreen','limegreen','red','orange','mediumblue','pink','darkgreen'] #prepared


#hatch_arr=[".","x"]
#hatch_arr=["////","----","////////","ooo","**",".///",".\\\\\\","xxx","+++"] #prepared 9 hatch style
hatch_arr=["////","----","ooo","**",".\\\\\\","xxx","+++",".///","////////"] #prepared 9 hatch style

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
ax.legend(loc='upper center', prop={'size': 12},bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=legendcolumn)

plt.tight_layout()
plt.show()


