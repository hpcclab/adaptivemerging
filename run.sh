for f in BenchmarkInput/*
do
if [ -d $f ]; then
    echo "ignore " $f
else
    java -jar CVSS.jar run $f simconfig_nomerge.properties
    java -jar CVSS.jar run $f simconfig_adaptive.properties
    java -jar CVSS.jar run $f simconfig_aggressivemerge.properties
    java -jar CVSS.jar run $f simconfig_consideratemerge.properties
    java -jar CVSS.jar run $f simconfig_positionfind.properties
    java -jar CVSS.jar run $f simconfig_positionfindAgg.properties
    java -jar CVSS.jar run $f simconfig_positionfindCons.properties
    java -jar CVSS.jar run $f simconfigd_nomerge.properties
    java -jar CVSS.jar run $f simconfigd_adaptive.properties
    java -jar CVSS.jar run $f simconfigd_aggressivemerge.properties
    java -jar CVSS.jar run $f simconfigd_consideratemerge.properties
    java -jar CVSS.jar run $f simconfigu_nomerge.properties
    java -jar CVSS.jar run $f simconfigu_adaptive.properties
    java -jar CVSS.jar run $f simconfigu_aggressivemerge.properties
    java -jar CVSS.jar run $f simconfigu_consideratemerge.properties

fi
done
