for f in BenchmarkInput/*
do
if [ -d $f ]; then
    echo "ignore " $f
else
    java -jar CVSS.jar run $f simconfigu_nomerge.properties
    java -jar CVSS.jar run $f simconfigu_adaptive.properties
    java -jar CVSS.jar run $f simconfigu_aggressivemerge.properties
    java -jar CVSS.jar run $f simconfigu_consideratemerge.properties
fi
done
