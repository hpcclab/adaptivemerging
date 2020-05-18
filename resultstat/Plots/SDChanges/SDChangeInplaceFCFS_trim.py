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

Cons_raw=[[314,293,275,510,324,47,333,111,230,161,454,86,408,604,112,407,158,316,133,82,608,694,250,249,99,302,85,243,300,375],[2217,2333,2282,2263,2224,2193,2270,2240,2283,2150,2313,2187,2286,2288,2274,2219,2270,2262,2210,2146,2267,2247,2213,2226,2223,2245,2294,2319,2214,2219]]

Agg_raw=[[323,297,281,512,289,47,337,129,231,195,455,80,426,611,120,418,158,335,132,91,602,701,249,265,101,304,83,244,187,378],[2206,2292,2253,2208,2179,2141,2227,2207,2243,2143,2256,2151,2270,2239,2246,2194,2278,2217,2178,2145,2242,2205,2181,2171,2170,2207,2214,2248,2175,2196]]

Adapt_raw=[[311,285,291,511,303,48,332,133,226,156,456,88,406,595,110,411,154,306,133,86,577,691,240,241,101,303,81,239,289,387],[2222,2323,2284,2260,2226,2195,2232,2226,2284,2158,2321,2193,2288,2276,2272,2192,2295,2260,2208,2135,2293,2231,2207,2222,2222,2222,2297,2290,2224,2211]]

Nomerge_raw=[[433,313,290,534,359,68,385,143,248,180,460,83,415,709,113,498,174,360,171,108,710,683,285,256,114,312,90,258,279,404],[2410,2496,2451,2444,2472,2396,2415,2440,2452,2375,2467,2326,2485,2445,2437,2377,2449,2414,2436,2360,2469,2445,2426,2393,2400,2440,2446,2440,2408,2418]]

Cons5_raw=[[343,313,274,510,346,42,326,137,266,204,489,131,410,591,109,478,166,354,190,94,597,740,311,242,135,395,78,272,359,388],[2243,2330,2285,2288,2225,2189,2272,2247,2263,2152,2336,2176,2284,2297,2297,2235,2295,2262,2203,2174,2291,2238,2239,2227,2218,2250,2281,2309,2202,2232]]

Cons10_raw=[[413,339,295,596,536,73,427,187,301,148,440,348,545,656,113,462,240,345,199,218,547,754,243,270,243,363,92,264,264,538],[2268,2306,2280,2272,2248,2207,2267,2251,2277,2181,2311,2205,2280,2281,2286,2225,2301,2269,2231,2204,2319,2242,2254,2250,2308,2247,2301,2272,2244,2226]]

Agg5_raw=[[368,298,307,556,469,40,340,175,241,146,442,119,463,641,138,513,168,330,137,138,584,635,316,255,106,370,83,269,411,367],[2207,2299,2256,2209,2220,2160,2191,2205,2252,2121,2263,2147,2247,2218,2247,2180,2246,2223,2175,2143,2236,2203,2198,2179,2199,2221,2220,2262,2182,2199]]

Agg10_raw=[[345,329,349,602,684,81,458,249,276,315,414,99,486,737,232,519,212,386,197,240,653,757,474,338,206,405,80,321,310,537],[2205,2303,2248,2224,2205,2142,2218,2199,2247,2139,2236,2166,2263,2220,2229,2189,2254,2210,2188,2205,2251,2208,2217,2193,2176,2238,2221,1717,2149,2226]]

Adapt5_raw=[[328,331,297,518,447,74,310,131,237,294,443,74,423,609,110,513,175,326,134,84,647,670,304,294,142,342,87,214,305,393],[2233,2322,2287,2259,2238,2173,2256,2212,2258,2136,2289,2180,2295,2283,2277,2234,2278,2260,2203,2139,2300,2239,2203,2205,2217,2272,2280,2306,2215,2216]]

Adapt10_raw=[[332,329,324,547,868,88,410,210,257,397,476,85,388,611,124,608,219,393,213,268,784,709,267,352,128,318,134,245,441,430],[2233,2317,2261,2104,2235,2174,2249,2230,2279,2154,2291,2203,2277,2284,2294,2234,2293,2275,2210,2177,2284,2248,2242,2203,2250,2248,2296,2311,2184,2216]]


Nomerge5_raw=[[447,337,322,576,355,113,377,248,286,229,460,89,447,756,129,523,157,390,230,151,793,720,290,345,130,357,75,300,320,430],[2409,2495,2455,2459,2473,2389,2413,2435,2454,2368,2465,2335,2484,2452,2440,2399,2454,2409,2446,2352,2470,2442,2430,2403,2403,2442,2443,2436,2404,2421]]


Nomerge10_raw=[[571,293,160,524,511,171,469,439,242,576,517,206,887,782,141,550,172,476,263,501,876,800,322,412,176,477,91,339,390,397],[2412,2487,2453,2466,2472,2409,2421,2421,2448,2373,2471,2361,2485,2444,2436,2392,2452,2447,2446,2346,2449,2448,2425,2421,2424,2460,2428,2455,2415,2428]]








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
xlabel='Oversubscription Level (#Service Requests)'
#ylabel='DMSP against non merging'
ylabel='Miss Rate Reduction (%)'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1.0k','2.5k')
labels=['Conservative','Conservative-5SD','Conservative-10SD',"Adaptive","Adaptive-5SD","Adaptive-10SD",'Aggressive','Aggressive-5SD','Aggressive-10SD']
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


