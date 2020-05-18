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
            newlist[i].append((baseline[i][j]-oldlist[i][j])*100.0/baseline[i][j] )
            #newlist[i].append((baseline[i][j]-oldlist[i][j]) )
            #print("newSeq")
    return newlist

##########start data section
#dump raw data here, so we can calculate both average and confidence interval


#Head=[1.0, 1.4, 1.7, 1.9, 2.0, 2.1, 2.3, 2.6, 3.0, 3.5, 4.0]

#Conservative
Cons_raw=[[239,255,71,327,91,26,189,63,129,392,195,43,320,755,50,664,98,349,55,37,649,421,420,79,94,279,14,57,71,269],[1189,770,1104,1131,1035,1231,1148,1101,823,804,1258,1264,657,1344,971,1021,840,1085,614,1126,1321,1287,999,891,815,804,1269,1070,1147,756],[1726,1859,1762,1792,1618,1715,1815,1582,1751,1594,1747,1748,1591,1860,1621,1747,1641,1736,1633,1693,1830,1769,1703,1673,1672,1751,1742,1861,1771,1486],[2167,2339,2216,2195,2124,2022,2291,2216,2198,2020,2222,2054,2176,2317,2122,2134,2197,2236,2079,2060,2220,2227,2130,2092,2109,1964,2184,2262,2234,2162]]

#Aggressive
Agg_raw=[[399,173,52,332,94,27,202,69,62,401,191,40,298,761,42,665,93,223,60,50,638,452,245,76,104,297,15,62,74,275],[1190,951,1106,1118,1039,1238,1136,1121,813,799,1241,1261,720,1351,967,1018,837,923,712,1248,1313,1289,976,922,801,839,1271,1072,1155,794],[1716,1856,1746,1762,1630,1707,1802,1603,1745,1593,1745,1726,1590,1857,1600,1658,1619,1715,1614,1701,1771,1769,1689,1623,1659,1764,1742,1830,1764,1444],[2135,2312,2183,2142,2074,2011,2257,2156,2159,1979,2149,2028,2134,2277,2099,2065,2172,2206,2083,2054,2195,2199,2114,2077,2087,2158,2134,2234,2210,2117]]

#Adaptive
Adapt_raw=[[257,228,79,333,102,27,188,63,61,375,191,53,298,762,49,668,102,234,67,27,651,437,255,72,98,279,14,43,77,294],[1190,772,1104,1114,1035,1238,1138,1101,773,801,1254,1262,720,1350,970,1023,884,1066,693,1145,1308,1284,926,885,824,1029,1273,1082,1158,714],[1727,1853,1762,1788,1618,1717,1804,1598,1749,1595,1749,1772,1594,1866,1599,1694,1627,1736,1613,1694,1823,1749,1704,1658,1672,1738,1745,1862,1773,1475],[2135,2312,2207,2155,2128,2012,2290,2204,2159,2021,2206,2028,2176,2324,2163,2126,2211,2222,2079,2063,2210,2220,2130,2105,2113,2158,2220,2261,2235,2130]]

#Nomerge
Nomerge_raw=[[515,288,64,339,73,32,282,55,70,473,199,4,311,771,48,702,102,266,120,52,889,456,459,71,98,285,14,96,99,269],[1298,838,1185,1348,1142,1324,1147,1172,861,965,1340,1251,1016,1458,1081,1079,937,1323,904,1328,1399,1418,978,1037,909,998,1366,1143,1236,873],[1842,1962,1879,1928,1763,1828,1941,1701,1846,1741,1862,1847,1718,1959,1841,1843,1754,1834,1781,1826,1925,1927,1847,1747,1766,1899,1870,1958,1892,1576],[2321,2535,2404,2399,2375,2289,2437,2400,2423,2280,2352,2252,2299,2470,2302,2292,2402,2386,2322,2304,2407,2442,2329,2261,2310,2362,2377,2409,2423,2362]]









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
xlabel='Oversubscription Level (#Tasks)'
#ylabel='DMSP against non merging'
ylabel='DMSR against non merging'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1k','1.5k','2k','2.5k')
labels=['Conservative',"Adaptive",'Aggressive']
legendcolumn= 2 #number of column in the legend
data=[Cons,Adapt,Agg]
yerrdata=[Cons_ci,Adapt_ci,Agg_ci]
axes.set_ylim([-50,25]) #y axis scale
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


