// common imports
import java.io.IOException;

// hadoop types
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

// map-reduce
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class MP4 {

	// Estimate how many energy consumes each province

	public static class MP4Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// get the line and split it
			String line = value.toString();
			String[] fields = line.split(";");

			// delete all the rows that are not "Viviendas con consumo"
			if( !fields[1].contains("Viviendas con consumo") ) return;
			
			// if the second char is a whitespace, it is a province. We will not map it
			if( fields[0].charAt(2) == ' ' || fields[0].charAt(2) == 't' ) return;

			// delete the dots from the "Totales" column, will give problems to the parseInt
			fields[2] = fields[2].replace(".", "");

			// get the town name and the number of houses
			String town = fields[0];
			int kwh = 0;
			int num_houses = 0;
			if( !fields[2].contains("Total") ){
				 num_houses = Integer.parseInt(fields[2]);
			}

			// we need to calculate how many kwh are in each line
			String consumption = fields[1];

			if(consumption.contains("1.000 kwh")) {kwh = 875;} // 751-1000
			else if(consumption.contains("y 2.000 kwh")) {kwh = 1500;} // 1001-2000
			else if(consumption.contains("y 3.000 kwh")) {kwh = 2500;} // do sth
			else if(consumption.contains("y 4.000 kwh")) {kwh = 3500;} // do sth
			else if(consumption.contains("y 5.000 kwh")) {kwh = 4500;} // do sth
			else if(consumption.contains("y 6.000 kwh")) {kwh = 5500;} // do sth
			else if(consumption.contains("y 7.000 kwh")) {kwh = 6500;} // do sth
			else if(consumption.contains("y 8.000 kwh")) {kwh = 7500;} // do sth
			else if(consumption.contains("y 9.000 kwh")) {kwh = 8500;} // do sth
			else if(consumption.contains("y 10.000 kwh")) {kwh = 9500;} // do sth
			else if(consumption.contains("de 10.000 kwh")) {kwh = 12000;} 
			// this could be deleted, we cant estimate it well
			
			// concatenate the first two char to create the province code
			String province_code = new StringBuilder().append(town.charAt(0)).append(town.charAt(1)).toString();

			// we are only going to map the first two digits
			context.write(
				new Text(province_code),
				new IntWritable(num_houses * kwh) // this will calculate the total kwh
			);


		}
	}
	
	public static class MP4Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text town, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int total_consumption = 0;
			int value_int = 0;
			for( IntWritable value : values ) {
				value_int = value.get();
				total_consumption += value_int;
			}

			context.write(town, new IntWritable(total_consumption));
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Please give valid inputs");
			System.exit(-1);
		}
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Municipios con mayor porcentaje de viviendas vacias");
		
		job.setJarByClass(MP4.class);
		job.setMapperClass(MP4Mapper.class);
		job.setReducerClass(MP4Reducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
