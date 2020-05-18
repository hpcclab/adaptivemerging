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
def normalizeQuick(oldlist):
    newlist=[]
    sequence=0
    for i in range(len(oldlist)):
        newlist.append([])
        for j in range(len(oldlist[i])):
            newlist[i].append(oldlist[i][j]/1000.0)
            #print("newSeq")
    return newlist
##########start data section
#dump raw data here, so we can calculate both average and confidence interval


#Head=[1.0, 1.4, 1.7, 1.9, 2.0, 2.1, 2.3, 2.6, 3.0, 3.5, 4.0]

#Conservative
Cons_raw=[[3115.528000,2985.224000,2943.552000,3055.104000,2979.144000,3069.424000,3258.192000,2978.664000,3000.336000,2992.856000,2780.224000,2948.232000,3408.712000,3055.768000,2776.016000,3555.000000,2905.352000,3205.328000,3216.792000,2899.456000,3320.312000,3119.072000,3036.232000,3109.160000,3048.912000,3201.880000,2888.344000,2910.584000,3018.032000,2945.944000],[4359.776000,4282.616000,4249.384000,4436.456000,4621.328000,4293.168000,4577.440000,4359.840000,4215.824000,4507.912000,4180.112000,3970.792000,4546.904000,4390.400000,4110.008000,4536.464000,4590.608000,4564.144000,4634.696000,4789.368000,4392.392000,4486.912000,4516.864000,4900.624000,4430.064000,4506.512000,4600.600000,4359.744000,4421.424000,4285.608000],[6559.928000,5949.320000,5600.760000,6250.688000,6239.432000,6450.472000,6640.736000,5839.176000,6339.152000,6063.168000,6003.256000,5997.200000,6420.712000,6518.784000,6021.624000,6802.864000,6407.416000,6454.888000,6582.776000,6065.848000,6358.864000,6309.152000,6563.952000,6532.272000,6345.920000,6913.728000,6700.728000,6342.400000,5887.016000,5823.312000],[7943.856000,7779.952000,7681.568000,7854.536000,7788.624000,7695.368000,8396.184000,7659.208000,7414.544000,7577.064000,7275.632000,7611.080000,8215.408000,7767.416000,7833.464000,8032.944000,7859.712000,7844.944000,7860.288000,7619.896000,7876.056000,8006.208000,7747.176000,8447.240000,7761.568000,8522.176000,8174.176000,7758.544000,7325.416000,7594.440000]]

#Aggressive
Agg_raw=[[3117.624000,2989.272000,2955.176000,3056.976000,2963.488000,3066.928000,3266.496000,2974.752000,2995.672000,2982.904000,2751.040000,2959.880000,3415.280000,3066.304000,2791.920000,3557.080000,2912.576000,3205.096000,3199.120000,2903.648000,3318.576000,3132.464000,3045.192000,3116.120000,3059.736000,3195.696000,2885.352000,2912.072000,3011.664000,2947.640000],[4354.576000,4278.408000,4252.272000,4480.512000,4607.768000,4243.736000,4585.768000,4362.952000,4206.336000,4524.392000,4191.880000,3976.800000,4540.568000,4130.272000,4116.344000,4527.648000,4572.744000,4665.416000,4640.984000,4762.696000,4394.912000,4502.288000,4515.296000,4918.880000,4425.160000,4513.680000,4587.104000,4300.144000,4417.112000,4281.912000],[6546.096000,5964.072000,5540.240000,6095.312000,6146.224000,6399.144000,6581.720000,5849.264000,6264.112000,5960.152000,5972.240000,5850.176000,6378.328000,6497.056000,6031.128000,6786.168000,6390.368000,6263.016000,6653.464000,6005.256000,6354.752000,6336.880000,6534.760000,6511.184000,6285.136000,6638.312000,6670.880000,6299.184000,5909.672000,5822.720000],[7866.752000,7742.520000,7688.720000,7632.288000,7614.296000,7615.440000,8352.832000,7638.056000,7286.888000,7484.848000,7254.384000,7540.728000,8175.168000,7648.976000,7989.872000,8035.880000,7877.568000,7668.536000,7894.432000,7645.944000,7763.096000,7970.680000,7666.872000,8163.640000,7679.952000,8452.288000,8046.672000,7618.048000,7279.696000,7517.112000]]

#Adaptive
Adapt_raw=[[3109.648000,3011.936000,2953.992000,3067.272000,2967.016000,3070.168000,3259.264000,2982.112000,2988.248000,2978.208000,2779.176000,2961.336000,3397.688000,3044.664000,2772.976000,3557.144000,2895.512000,3193.648000,3189.712000,2890.736000,3316.792000,3118.880000,3044.672000,3128.152000,3050.296000,3182.800000,2873.056000,2922.400000,3020.496000,2958.536000],[4357.072000,4271.368000,4258.192000,4443.296000,4616.472000,4307.328000,4559.664000,4364.792000,4213.392000,4516.880000,4180.824000,3969.208000,4532.984000,4416.184000,4106.752000,4530.880000,4584.880000,4577.288000,4652.936000,4767.376000,4411.648000,4467.304000,4523.048000,4895.136000,4435.888000,4505.384000,4607.800000,4296.120000,4406.376000,4284.480000],[6575.104000,5949.848000,5579.752000,6287.816000,6224.312000,6434.056000,6624.120000,5841.968000,6347.248000,6042.480000,6045.400000,5865.544000,6428.344000,6564.328000,6025.120000,6797.048000,6396.136000,6331.048000,6571.600000,6063.880000,6370.328000,6327.040000,6553.024000,6503.624000,6379.744000,6903.216000,6632.176000,6334.648000,5887.856000,5832.296000],[7939.272000,7781.368000,7684.752000,7868.008000,7783.704000,7710.216000,8323.952000,7588.832000,7402.664000,7559.848000,7319.376000,7662.080000,8235.736000,7766.120000,7835.912000,7985.728000,7893.016000,7924.784000,7972.208000,7620.376000,7863.688000,7949.456000,7730.928000,7371.896000,7743.784000,7084.624000,8168.376000,7756.936000,7339.536000,7583.320000]]

#Nomerge
Nomerge_raw=[[3379.672000,3027.432000,3047.752000,3082.896000,3042.848000,3255.808000,3374.320000,3080.744000,3110.264000,3251.856000,2854.720000,3053.512000,3476.976000,3133.360000,2982.888000,3882.616000,2937.784000,3321.400000,3290.072000,2970.584000,3571.592000,3356.016000,3100.136000,3180.896000,3084.248000,3228.320000,3035.608000,3079.000000,3086.264000,2994.328000],[4812.976000,4523.872000,4578.120000,4707.320000,4865.832000,4592.784000,4768.520000,4548.552000,4357.000000,4729.640000,4421.456000,4447.720000,4784.576000,4879.936000,4688.840000,4973.112000,4720.544000,4965.224000,5029.872000,4897.448000,4801.264000,4854.952000,4803.240000,5238.520000,4638.288000,4921.256000,4877.720000,4606.904000,4698.856000,4558.328000],[6985.928000,6276.760000,5995.504000,6619.816000,6645.480000,6776.480000,6826.768000,6138.056000,6472.456000,6313.848000,6298.880000,6200.144000,6738.848000,7010.912000,6722.352000,7261.200000,6560.680000,6666.536000,7120.288000,6305.328000,6939.632000,6750.312000,6854.080000,6987.416000,6654.584000,7324.856000,6976.512000,6790.128000,6299.424000,6197.088000],[8579.688000,8244.400000,8322.136000,8409.320000,8373.000000,8187.400000,8741.152000,8209.560000,7807.952000,8105.528000,7838.696000,8047.840000,8700.720000,8432.336000,8573.448000,8743.232000,8290.816000,8587.712000,8679.328000,8283.088000,8593.680000,8588.648000,8270.752000,8945.480000,8400.160000,9101.480000,8716.152000,8297.456000,7842.736000,8094.192000]]


Agg5_raw=[[3127.896000,2999.472000,2957.648000,3046.344000,2971.728000,3072.432000,3257.544000,2974.336000,3003.856000,2986.544000,2748.928000,2958.288000,3409.328000,3055.736000,2783.216000,3545.840000,2914.328000,3199.368000,3189.960000,2891.920000,3316.872000,3120.856000,3036.520000,3109.816000,3064.848000,3189.448000,2876.928000,2925.344000,3005.512000,2936.648000],[4341.120000,4278.232000,4240.328000,4471.552000,4613.040000,4234.056000,4572.824000,4360.816000,4218.616000,4524.368000,4178.872000,3989.880000,4540.536000,4112.720000,4106.632000,4533.144000,4579.792000,4648.448000,4643.632000,4773.080000,4400.536000,4481.328000,4515.624000,4903.048000,4442.000000,4509.664000,4598.936000,4360.008000,4427.720000,4287.104000],[6548.176000,5962.608000,5553.840000,6081.328000,6152.256000,6361.376000,6605.304000,5827.480000,6248.568000,5964.376000,5966.136000,5864.200000,6383.408000,6497.464000,6016.744000,6804.200000,6388.864000,6276.432000,6641.304000,5991.640000,6368.256000,6334.328000,6514.952000,6502.760000,6285.704000,6673.432000,6672.848000,6088.720000,5899.208000,5813.136000],[7848.992000,7745.720000,7699.096000,7644.376000,7627.056000,7639.976000,6892.448000,7623.048000,7281.376000,7488.256000,7239.432000,7534.392000,8177.376000,7667.664000,7970.880000,8012.400000,7894.424000,7675.176000,7889.168000,7638.824000,7740.960000,7968.248000,7674.704000,8193.088000,7667.984000,8397.952000,8065.960000,7609.184000,7278.880000,7541.096000]]

Adapt5_raw=[[3124.712000,2983.872000,2945.960000,3061.504000,2967.472000,3068.376000,3257.392000,2980.016000,2993.464000,2994.328000,2767.608000,2962.672000,3403.424000,3041.008000,2783.208000,3555.448000,2901.008000,3205.824000,3176.600000,2899.832000,3323.864000,3117.328000,3046.336000,3107.856000,3044.624000,3188.072000,2880.416000,2917.376000,3020.416000,2968.440000],[4353.840000,4284.448000,4247.000000,4441.776000,4620.504000,4288.312000,4570.064000,4357.392000,4220.080000,4518.752000,4176.536000,3983.856000,4540.168000,4403.472000,4111.160000,4531.816000,4577.216000,4659.800000,4643.712000,4768.312000,4390.928000,4480.160000,4520.408000,4902.656000,4434.616000,4514.184000,4588.968000,4370.280000,4425.024000,4273.704000],[6574.072000,5955.360000,5582.824000,6216.136000,6246.856000,6423.776000,6639.376000,5837.176000,6344.072000,6032.088000,6023.080000,6005.928000,6422.824000,6586.136000,6014.168000,6798.968000,6390.264000,6447.920000,6590.976000,6071.120000,6377.664000,6321.288000,6555.616000,6508.664000,6355.192000,6881.008000,6669.696000,6335.824000,5918.864000,5798.496000],[7946.608000,7772.328000,7706.408000,7856.832000,7786.496000,7705.768000,8343.840000,7616.912000,7399.464000,7571.216000,7325.976000,7647.544000,8203.896000,7686.616000,7940.936000,8005.512000,7893.592000,7971.752000,7953.208000,7621.632000,7872.392000,7949.648000,7728.512000,8218.016000,7747.288000,8499.768000,8168.480000,7740.712000,7325.512000,7543.368000]]


Cons5_raw=[[3122.752000,2979.176000,2949.040000,3043.264000,2971.328000,3080.728000,3264.152000,2988.832000,2981.544000,2994.680000,2790.760000,2950.528000,3400.712000,3055.328000,2762.968000,3553.288000,2902.712000,3194.504000,3186.608000,2909.984000,3320.648000,3121.752000,3036.616000,3103.072000,3062.528000,3199.368000,2884.672000,2908.376000,3018.520000,2950.112000],[4344.344000,4275.256000,4248.704000,4512.080000,4604.344000,4311.216000,4576.640000,4364.048000,4208.672000,4499.216000,4176.016000,3978.296000,4536.880000,4414.408000,4113.440000,4534.384000,4577.680000,4562.704000,4649.536000,4775.872000,4392.400000,4495.288000,4531.560000,4900.096000,4447.192000,4496.432000,4600.632000,4358.984000,4431.952000,4278.752000],[6550.624000,5949.392000,5589.032000,6260.976000,6242.720000,6439.344000,6627.232000,5846.712000,6317.472000,6023.112000,6034.048000,6013.328000,6433.688000,6524.472000,6013.400000,6792.472000,6392.664000,6462.768000,6584.248000,6057.424000,6383.824000,4905.688000,6556.856000,6516.768000,6351.144000,6896.816000,6701.424000,6337.336000,5899.560000,5829.880000],[7943.664000,7789.584000,7696.912000,7856.904000,7784.736000,7707.480000,8379.928000,7640.112000,7415.672000,7596.992000,7292.624000,7587.080000,8201.600000,7847.632000,7845.248000,8011.312000,7848.928000,7839.664000,7949.616000,7644.128000,7876.128000,8006.568000,7708.032000,8458.768000,7744.448000,8495.504000,8203.584000,7725.680000,7323.480000,7595.288000]]







#create array for mean, and confidence interval
Cons=[]
Agg=[]
Adapt=[]
Nomerge=[]
Cons5=[]
Agg5=[]
Adapt5=[]

#
Cons_ci=[]
Agg_ci=[]
Adapt_ci=[]
Nomerge_ci=[]
Cons5_ci=[]
Agg5_ci=[]
Adapt5_ci=[]
#calculate CI and mean
column=len(Cons_raw)

######## normalize the data
Cons_raw=normalizeQuick(Cons_raw)
Agg_raw=normalizeQuick(Agg_raw)
Adapt_raw=normalizeQuick(Adapt_raw)
Nomerge_raw=normalizeQuick(Nomerge_raw)

Cons5_raw=normalizeQuick(Cons5_raw)
Agg5_raw=normalizeQuick(Agg5_raw)
Adapt5_raw=normalizeQuick(Adapt5_raw)
#find mean and stdErr
for l in range(column):
    #format: mean array, CI array, raw data, how many trials in the raw data
    #don't forget to change number 30 to number of trials you actually test
    insMeanAndCI(Cons,Cons_ci,Cons_raw,30,l)
    insMeanAndCI(Agg,Agg_ci,Agg_raw,30,l)
    insMeanAndCI(Adapt,Adapt_ci,Adapt_raw,30,l)
    insMeanAndCI(Cons5,Cons5_ci,Cons5_raw,30,l)
    insMeanAndCI(Agg5,Agg5_ci,Agg5_raw,30,l)
    insMeanAndCI(Adapt5,Adapt5_ci,Adapt5_raw,30,l)
    insMeanAndCI(Nomerge,Nomerge_ci,Nomerge_raw,30,l)
###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameters section
n_groups =7 # number of different data to plot, can change here without removing data
xlabel='Oversubscription Level (#Tasks)'
ylabel='Total Makespan (x1000)'
n_point = column # number of x ticks to use, must match number of xtick and number of data point
xtick=('1k','1.5k','2k','2.5k')
labels=['Conservative',"Adaptive",'Aggressive','No-merge','Conservative-LS',"Adaptive-LS",'Aggressive-LS']
legendcolumn= 2 #number of column in the legend
data=[Cons,Adapt,Agg,Nomerge,Cons5,Adapt5,Agg5]
yerrdata=[Cons_ci,Adapt_ci,Agg_ci,Nomerge_ci,Cons5_ci,Adapt5_ci,Agg5_ci]
axes.set_ylim([0,12]) #y axis scale
ticklabelsize=18
axislabelfontsize=16

############
#auto calculated values and some rarely change config, can also overwrite
axes.set_xlim([-0.5, len(xtick)-0.5]) #y axis
font = {'family' : 'DejaVu Sans',
        #'weight' : 'bold',
        'size'   : 16 }
bar_width =1.0/(n_groups+2) 
#edgecols=['royalblue','forestgreen','red','mediumblue','orange','pink','limegreen','lightblue','darkgreen'] #prepared 9 colors
edgecols=['royalblue','lightblue','forestgreen','limegreen','red','orange','mediumblue','pink','darkgreen'] #prepared
#hatch_arr=[".","x"]
#hatch_arr=["////","ooo",".\\\\\\","----","**","xxx","+++",".///","////////"] #prepared 9 hatch style
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


