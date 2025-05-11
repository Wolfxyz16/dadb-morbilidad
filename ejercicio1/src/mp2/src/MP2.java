// common imports
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

// hadoop types
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;

// map-reduce
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class MP2 {

	/*
	 * Calculate the top 10 towns with the most number of occasional use housings
	 */

	public static class MP2Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		private TreeMap<Integer, String> tmap1;

		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			tmap1 = new TreeMap<Integer, String>();
		}

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// Split csv file line
			String line = value.toString();
			String[] fields = line.split(";");
			String town = fields[0];
			
			// Check if the line is "Vivienda de uso esporadico"
			if( !fields[1].equals("Viviendas de uso esporádico") ) return;
			if( fields[0].charAt(2) == ' ' || fields[0].charAt(2) == 't') return;

			// Now we remove the dots from the String that represents a number, 1.285 => 1285
			fields[2] = fields[2].replace(".", "");
			int houses = Integer.parseInt(fields[2]);

			tmap1.put(houses, town);

			// We must delete the first key if we get more than 10
			if( tmap1.size() > 10 ) tmap1.remove(tmap1.firstKey());
		}

		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// Map the top 10 local
			for( Map.Entry<Integer,String> entry : tmap1.entrySet() ) {
				int houses = entry.getKey();
				String town = entry.getValue();
				context.write(new Text(town), new IntWritable(houses));
			}
		}
	}
	
	public static class MP2Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	
		private TreeMap<Integer, String> tmap2;

		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			tmap2 = new TreeMap<Integer, String>();
		}

		@Override
		public void reduce(Text town, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			String townName = town.toString();
			int housesNumber = 0;
			
			for( IntWritable value : values ) {
				housesNumber = value.get();
			}

			tmap2.put(housesNumber, townName);

			// We must delete the first key if we get more than 10
			if( tmap2.size() > 10 ) tmap2.remove(tmap2.firstKey());

		}

		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// We finally get the top 10 towns and map them
			for( Map.Entry<Integer,String> entry : tmap2.entrySet() ) {
				int houses = entry.getKey();
				String town = entry.getValue();
				context.write(new Text(town), new IntWritable(houses));
			}
		}

	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Please give valid inputs");
			System.exit(-1);
		}

		System.out.println("Municipios con mayor cantidad de viviendas de uso ocasional");
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Municipios con mayor cantidad de viviendas de uso ocasional");
		
		job.setJarByClass(MP2.class);
		job.setMapperClass(MP2Mapper.class);
		job.setReducerClass(MP2Reducer.class);
		
		// Change if neccesary
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
