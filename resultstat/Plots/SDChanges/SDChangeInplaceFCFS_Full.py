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

Cons5_raw=[[343,313,274,510,346,42,326,137,266,204,489,131,410,591,109,478,166,354,190,94,597,740,311,242,135,395,78,272,359,388],[1270,668,1291,1332,1336,1200,1300,1247,1065,1001,1333,1262,824,1353,1054,1047,1075,917,1023,1247,1196,1321,1293,1081,1214,1330,1312,1258,1245,1063],[1811,1841,1802,1847,1830,1792,1823,1771,1873,1682,1845,1796,1766,1819,1819,1794,1834,1803,1763,1747,1861,1816,1838,1648,1805,1819,1805,1871,1819,1741],[2243,2330,2285,2288,2225,2189,2272,2247,2263,2152,2336,2176,2284,2297,2297,2235,2295,2262,2203,2174,2291,2238,2239,2227,2218,2250,2281,2309,2202,2232]]

Cons10_raw=[[413,339,295,596,536,73,427,187,301,148,440,348,545,656,113,462,240,345,199,218,547,754,243,270,243,363,92,264,264,538],[1254,1212,1310,1315,1353,1258,1298,1266,1188,920,1328,1215,1088,1343,1145,1081,1219,1110,1069,1267,1356,1308,1289,1117,1157,1343,1321,1310,1250,1091],[1820,1845,1787,1852,1830,1791,1815,1772,1847,1708,1849,1775,1823,1843,1786,1786,1867,1813,1741,1739,1863,1809,1841,1730,1821,1854,1814,1878,1049,1822],[2268,2306,2280,2272,2248,2207,2267,2251,2277,2181,2311,2205,2280,2281,2286,2225,2301,2269,2231,2204,2319,2242,2254,2250,2308,2247,2301,2272,2244,2226]]

Agg5_raw=[[368,298,307,556,469,40,340,175,241,146,442,119,463,641,138,513,168,330,137,138,584,635,316,255,106,370,83,269,411,367],[1254,772,1259,1325,1346,1248,1237,1199,1042,688,1323,1257,800,1329,1073,1047,1089,918,1032,1187,1351,1321,1324,1103,1173,1331,1314,1270,1277,1034],[1807,1841,1777,1827,1813,1767,1800,1774,1849,1615,1830,1790,1798,1817,1801,1766,1852,1797,1745,1734,1849,1806,1841,1664,1774,1779,1806,1861,1813,1778],[2207,2299,2256,2209,2220,2160,2191,2205,2252,2121,2263,2147,2247,2218,2247,2180,2246,2223,2175,2143,2236,2203,2198,2179,2199,2221,2220,2262,2182,2199]]

Agg10_raw=[[345,329,349,602,684,81,458,249,276,315,414,99,486,737,232,519,212,386,197,240,653,757,474,338,206,405,80,321,310,537],[1271,823,1287,1311,1344,1263,1278,1278,1333,786,1333,1241,982,1333,1082,1090,1311,976,1050,1197,1360,1325,1309,1143,1242,1330,1285,1303,1272,1130],[1797,1848,1797,1826,1810,1767,1810,1763,1843,1694,1826,1773,1747,1824,1804,1777,1825,1815,1742,1740,1833,1792,1829,1630,1771,1738,1819,1868,1796,1785],[2205,2303,2248,2224,2205,2142,2218,2199,2247,2139,2236,2166,2263,2220,2229,2189,2254,2210,2188,2205,2251,2208,2217,2193,2176,2238,2221,1717,2149,2226]]

Adapt5_raw=[[328,331,297,518,447,74,310,131,237,294,443,74,423,609,110,513,175,326,134,84,647,670,304,294,142,342,87,214,305,393],[1270,779,1313,1326,1342,1238,1297,994,1090,909,1329,1166,845,1357,1014,1037,1104,918,1053,1235,1349,1322,1321,1133,1145,1329,1310,1251,1288,1165],[1811,1866,1802,1852,1841,1781,1812,1794,1849,1678,1869,1802,1796,1826,1827,1796,1846,1784,1755,1745,1844,1803,1860,1662,1810,1792,1803,1856,1805,1765],[2233,2322,2287,2259,2238,2173,2256,2212,2258,2136,2289,2180,2295,2283,2277,2234,2278,2260,2203,2139,2300,2239,2203,2205,2217,2272,2280,2306,2215,2216]]

Adapt10_raw=[[332,329,324,547,868,88,410,210,257,397,476,85,388,611,124,608,219,393,213,268,784,709,267,352,128,318,134,245,441,430],[1289,694,1299,1299,1349,1213,1258,1268,1199,1130,1321,1190,907,1342,1044,1094,1109,959,1180,1172,1354,1323,1303,1097,1264,1340,1324,1290,1289,1074],[1806,1851,1807,1845,1841,1828,1826,1750,1867,1682,1842,1795,1825,1858,1828,1794,1859,1821,1773,1749,1846,1821,1842,1738,1805,1839,1827,1867,1803,1801],[2233,2317,2261,2104,2235,2174,2249,2230,2279,2154,2291,2203,2277,2284,2294,2234,2293,2275,2210,2177,2284,2248,2242,2203,2250,2248,2296,2311,2184,2216]]


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
labels=['Conservative','Conservative-5SD','Conservative-10SD',"Adaptive","Adaptive-5SD","Adaptive-10SD",'Aggressive','Aggressive-5SD','Aggressive-10SD']
legendcolumn= 2 #number of column in the legend
data=[Cons,Cons5,Cons10,Adapt,Adapt5,Adapt10,Agg,Agg5,Agg10]
yerrdata=[Cons_ci,Cons5_ci,Cons10_ci,Adapt_ci,Adapt5_ci,Adapt10_ci,Agg_ci,Agg5_ci,Agg10_ci]
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


