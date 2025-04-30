from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import math
import json

def haversine(lat1, lon1, lat2, lon2):
    R = 6371.0  # Earth radius in km
    lat1, lon1, lat2, lon2 = map(math.radians, [float(lat1), float(lon1), float(lat2), float(lon2)])
    dlat = lat2 - lat1
    dlon = lon2 - lon1
    a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    return R * c

def calculate_distance(rdd):
    positions = rdd.collect()
    print("hola mundo")
    if len(positions) >= 2:
	first = str(positions[0]).strip()
	last = str(positions[-1]).strip()

	first = first.split(" - ")
	last = last.split(" - ")
	print("first: ", first)
	
	coordenadas_first = first[1].split(",")
	coordenadas_last = last[1].split(",")
	print("coords: ", coordenadas_first)
		
        lat1, lon1 = float(coordenadas_first[0]), float(coordenadas_first[1])
       	lat2, lon2 = float(coordenadas_last[0]), float(coordenadas_last[1])
	
        print("lat1: ",lat1)
        distance = haversine(lat1, lon1, lat2, lon2)
        print("Distance traveled in last 30 seconds: %.2f km" % distance)
        #return distance

    else:
    	print("no habia mas de dos entradas")
    #return 0

if __name__ == "__main__":
    sc = SparkContext(appName="ISS")
    ssc = StreamingContext(sc, batchDuration=30)  # 30 seconds batch
    
    # Monitor HDFS directory for new files
    lines = ssc.textFileStream("hdfs:///proyecto_ejercicio2/")
    
    # Process each RDD in the DStream
    lines.foreachRDD(calculate_distance)
    
    ssc.start()
    ssc.awaitTermination()
