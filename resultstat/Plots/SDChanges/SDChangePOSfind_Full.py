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

Cons_raw=[[313,282,287,500,313,46,346,131,239,159,455,93,399,609,120,416,160,321,138,83,521,708,303,246,103,310,82,248,199,385],[1288,638,1281,1267,1338,1240,1283,891,964,913,1329,1256,765,1340,1069,1053,1096,918,951,1191,1353,1339,1249,873,1131,1317,1310,1232,1278,971],[1826,1870,1816,1912,1865,1834,1867,1744,1888,1630,1891,1823,1852,1862,1802,1804,1879,1814,1779,1760,1900,1820,1841,1690,1817,1850,1830,1891,1810,1786],[2274,2379,2329,2332,2298,2225,2295,2284,2314,2179,2375,2238,2341,2330,2216,2277,2363,2297,2269,2212,2362,2279,2305,2278,2303,2285,2358,2350,2245,2263]]

Agg_raw=[[311,295,283,511,324,49,331,131,228,199,452,89,409,603,116,393,158,314,125,83,588,692,244,227,104,311,81,243,283,384],[1256,653,1262,1268,1337,1212,1283,1035,961,904,1324,1180,790,1324,1048,1043,1064,901,981,1202,1344,1319,1318,1087,1098,1326,1306,1171,1270,982],[1803,1843,1777,1817,1795,1760,1817,1762,1850,1622,1820,1769,1763,1787,1771,1779,1834,1781,1735,1734,1837,1799,1835,1652,1769,1784,1793,1874,1800,1738],[2200,2291,2252,2204,2183,2144,2190,2207,2243,2144,2256,2143,2271,2240,2239,2195,2247,2215,2179,2147,2237,2204,2183,2173,2170,2209,2212,2246,2173,2199]]

Adapt_raw=[[312,282,282,510,311,52,347,107,229,147,446,75,403,612,114,404,157,316,151,81,517,715,258,221,103,311,77,244,214,386],[1289,639,1275,1243,1341,1244,1275,925,1005,907,1329,1269,789,1332,1053,1052,1099,916,993,1204,1358,1339,1252,1086,1108,1302,1316,1233,1255,967],[1826,1871,1824,1896,1873,1833,1865,1758,1865,1640,1885,1838,1822,1856,1795,1822,1876,1805,1783,1753,1898,1820,1851,1660,1807,1849,1831,1891,1795,1791],[2287,2369,2317,2328,2309,2242,2319,2296,2322,2200,2398,2232,2342,2349,2342,2273,2353,2327,2272,2211,2369,2289,2313,2281,2295,2311,2387,2333,2260,2273]]

Nomerge_raw=[[433,313,290,534,359,68,385,143,248,180,460,83,415,709,113,498,174,360,171,108,710,683,285,256,114,312,90,258,279,404],[1368,973,1363,1370,1425,1385,1383,1207,1169,1010,1410,1361,848,1432,1170,1134,1204,1007,1034,1313,1441,1432,1403,1197,1189,1413,1381,1383,1366,1067],[1915,1976,1915,1969,1971,1915,1945,1872,1961,1771,1953,1876,1901,1932,1929,1898,1947,1912,1911,1850,1974,1938,1954,1785,1913,1940,1911,1973,1921,1832],[2410,2496,2451,2444,2472,2396,2415,2440,2452,2375,2467,2326,2485,2445,2437,2377,2449,2414,2436,2360,2469,2445,2426,2393,2400,2440,2446,2440,2408,2418]]

Cons5_raw=[[357,268,311,468,313,47,361,190,230,186,486,81,412,590,111,519,175,395,135,103,585,747,353,231,180,336,86,251,143,452],[1280,680,1272,1314,1341,1257,1232,925,1072,910,1332,1271,848,1340,1080,1060,1174,959,1007,1201,1358,1339,1199,1102,1131,1292,1334,1228,1259,1063],[1819,1862,1804,1892,1883,1838,1860,1763,1872,1655,1872,1811,1847,1854,1851,1826,1881,1822,1758,1749,1885,1836,1859,1717,1824,1845,1844,1895,1815,1798],[2278,2366,2318,2327,2303,2237,2286,2282,2295,2197,2374,2216,2345,2336,2330,2285,2349,2314,2255,2172,2327,2287,2305,2248,2304,2282,2362,2346,2234,2279]]

Cons10_raw=[[343,314,300,626,551,62,371,224,223,236,463,116,504,689,101,522,232,505,175,160,851,710,281,316,158,314,92,300,273,466],[1280,651,1291,1347,1348,1272,1305,1243,1293,921,1337,1266,795,1331,1122,1046,1232,1092,1023,1297,1360,1339,1312,1151,1158,1325,1317,1246,1267,1067],[1816,1882,1847,1898,1869,1808,1856,1789,1864,1684,1891,1829,1818,1869,1837,1817,1890,1859,1778,1752,1879,1842,1859,1721,1812,1861,1828,1898,1795,1780],[2279,2364,2301,2318,2276,2233,2289,2265,2294,2236,2343,2219,2342,2315,2345,2265,2357,2295,2276,2279,2333,2270,2292,2270,2282,2301,2307,2348,2255,2262]]

Agg5_raw=[[351,313,286,574,248,58,365,176,242,202,461,77,413,616,95,518,160,345,168,78,632,711,281,314,152,294,88,271,385,404],[1282,672,1293,1272,1341,1227,1272,1048,1044,906,1319,1184,902,1328,1062,1058,1128,940,1053,1220,1343,1322,1306,1074,1128,1325,1314,1247,1299,1053],[1802,1812,1800,1820,1819,1772,1806,1770,1848,1635,1828,1788,1813,1792,1791,1776,1821,1794,1728,1728,1820,1805,1846,1659,1765,1806,1796,1855,1798,1771],[2214,2312,2001,2217,2216,2127,1885,2192,2243,2117,2198,2117,2256,2224,2227,2200,2241,2222,2186,2161,2241,2204,2208,2195,2187,2214,2224,2246,2173,2205]]

Agg10_raw=[[359,314,382,543,453,111,524,241,219,328,557,108,519,688,101,535,210,409,340,550,865,677,356,395,223,356,87,240,258,540],[1286,909,1310,1292,1343,1216,1292,1285,1325,1093,1325,1271,858,1341,1053,1069,1252,1103,1201,1304,1359,1322,1302,1069,1237,1327,1314,1225,1253,1189],[1813,1844,1802,1834,1804,1753,1804,1794,1856,1714,1823,1810,1825,1806,1785,1794,1838,1792,1801,1737,1825,1816,1837,1654,1803,1824,1821,1849,1768,1787],[2222,2308,2227,2221,2205,2135,2180,2183,2213,2128,2245,2143,2266,2236,2247,2193,2266,2223,2186,2180,2259,2191,2218,2189,2120,2236,2219,2260,2167,2187]]

Adapt5_raw=[[330,290,196,490,307,47,349,106,206,189,457,119,460,590,130,441,168,337,145,110,610,700,293,270,144,313,79,268,302,403],[1297,683,1279,1253,1345,1258,1302,1063,1101,948,1337,1262,860,1338,1050,1062,1085,969,967,1197,1358,1339,1242,1126,1070,1282,1310,1227,1266,991],[1831,1870,1804,1899,1873,1764,1874,1773,1885,1684,1874,1799,1852,1856,1840,1810,1866,1820,1764,1805,1906,1820,1853,1716,1817,1839,1858,1897,1811,1781],[2292,2353,2331,2323,2324,2239,2312,1884,2322,2180,2387,2017,2343,2360,2343,2300,2375,2312,2255,2206,2369,2284,2302,2260,2297,2311,2384,2343,2266,2293]]

Adapt10_raw=[[410,278,208,537,619,66,450,137,280,219,503,162,506,808,383,598,245,418,177,260,754,769,332,286,181,403,86,272,506,448],[1289,939,1314,1348,1344,1267,1301,1149,1147,1132,1340,1283,1089,1366,1031,1104,932,1099,1098,1229,1371,1339,1316,1137,1249,1312,1324,1294,1266,1178],[1832,1879,1834,1916,1881,1826,1859,1791,1881,1744,1872,1811,1832,1852,1878,1821,1880,1829,1788,1757,1890,1839,1869,1688,1832,1853,1843,1913,1800,1828],[2300,2353,2335,2336,2295,2239,2324,2277,2315,2230,2362,2243,2345,2353,2340,2309,2340,2301,2280,2226,2365,2279,2318,2270,2304,2304,2379,2364,2273,2285]]


Nomerge5_raw=[[447,337,322,576,355,113,377,248,286,229,460,89,447,756,129,523,157,390,230,151,793,720,290,345,130,357,75,300,320,430],[1368,1063,1382,1421,1430,1385,1393,1235,1200,1079,1410,1361,836,1435,1120,1115,1272,1047,1029,1326,1441,1432,1406,1222,1335,1442,1378,1407,1360,1116],[1914,1977,1930,1969,1978,1912,1945,1863,1960,1799,1951,1876,1929,1930,1930,1897,1945,1907,1907,1862,1974,1938,1959,1814,1928,1948,1910,1973,1932,1840],[2409,2495,2455,2459,2473,2389,2413,2435,2454,2368,2465,2335,2484,2452,2440,2399,2454,2409,2446,2352,2470,2442,2430,2403,2403,2442,2443,2436,2404,2421]]


Nomerge10_raw=[[571,293,160,524,511,171,469,439,242,576,517,206,887,782,141,550,172,476,263,501,876,800,322,412,176,477,91,339,390,397],[1384,1316,1408,1406,1429,1402,1393,1335,1353,1187,1405,1361,1040,1438,1308,1151,1381,1026,1306,1345,1422,1432,1383,1252,1385,1442,1376,1369,1380,1281],[1933,1989,1921,1972,1972,1912,1945,1897,1951,1820,1950,1921,1957,1940,1949,1908,1952,1946,1925,1870,1974,1940,1951,1722,1918,1940,1923,1977,1926,1920],[2412,2487,2453,2466,2472,2409,2421,2421,2448,2373,2471,2361,2485,2444,2436,2392,2452,2447,2446,2346,2449,2448,2425,2421,2424,2460,2428,2455,2415,2428]]








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

Cons10_raw=normalizeSubstract(Cons10_raw,Nomerge10_raw)
Agg10_raw=normalizeSubstract(Agg10_raw,Nomerge10_raw)
Adapt10_raw=normalizeSubstract(Adapt10_raw,Nomerge10_raw)
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
    insMeanAndCI(Cons10,Cons10_ci,Cons10_raw,180,l)
    insMeanAndCI(Agg10,Agg10_ci,Agg10_raw,180,l)
    insMeanAndCI(Adapt10,Adapt10_ci,Adapt10_raw,180,l)
    #insMeanAndCI(Nomerge,Nomerge_ci,Nomerge_raw,120,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =9 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Tasks)'
#ylabel='DMSP against non merging'
ylabel='Percentage of miss saving'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1.0k','1.5k','2.0k','2.5k')
labels=['Cons-Pfind','Cons-Pfind-5SD','Cons-Pfind-10SD',"Adapt-Pfind","Adapt-Pfind-5SD","Adapt-Pfind-10SD",'Agg-Pfind','Agg-Pfind-5SD','Agg-Pfind-10SD']
legendcolumn= 2 #number of column in the legend
data=[Cons,Cons5,Cons10,Adapt,Adapt5,Adapt10,Agg,Agg5,Agg10]
yerrdata=[Cons_ci,Cons5_ci,Cons10_ci,Adapt_ci,Adapt5_ci,Adapt10_ci,Agg_ci,Agg5_ci,Agg10_ci]
axes.set_ylim([0,40]) #y axis scale
ticklabelsize=18
axislabelfontsize=16

############
#auto calculated values and some rarely change config, can also overwrite
axes.set_xlim([-0.5, len(xtick)-0.5]) #y axis
font = {'family' : 'DejaVu Sans',
        #'weight' : 'bold',
        'size'   : 16 }
bar_width =1.0/(n_groups+2) 
edgecols=['royalblue','lightblue','mediumblue','forestgreen','limegreen','darkgreen','red','orange','pink'] #prepared 9 colors
#hatch_arr=[".","x"]
hatch_arr=["////","----","////////","ooo","**",".///",".\\\\\\","xxx","+++"] #prepared 9 hatch style
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


