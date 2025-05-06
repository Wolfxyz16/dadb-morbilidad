# Make sure the output directory does not exit in hdfs and in local fs
hadoop fs -rm -f -r results
rm -rf results

# Run the jar
hadoop jar bin/mp4.jar MP4 viviendas.csv results

echo "jar executed, moving results to local file system..."

# Copy results to local
hadoop fs -copyToLocal results results

echo "Results are copied and ready to be checked"

# Delete results directory from hdfs
hadoop fs -rm -r -f results
