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

public class MP3 {

	public static class MP3Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			// Suma de viviendas vacias por provincia
			// Se devolverá el nombre del municipio y el número de viviendas vacias ordenado de mayor a menor
			String line = value.toString();
			String[] fields = line.split(";");

			// We get the first char of the second word
			// 'v' stands for vacias
			char type = fields[1].split(" ")[1].charAt(0);
			
			// First we must check that "Consumo electrico" is "vacias"
			if(  type != 'v' ) return;

			// Now we remove the dots from the String that represents a number, 1.285 => 1285
			fields[2] = fields[2].replace(".", "");

			// We get the code (postal code) and the number of houses
			String code = fields[0].split(" ")[0];
			int houses = Integer.parseInt(fields[2]);
			
			// Code must be bigger than two in order to be a town, not a "comunidad"
			if( code.length() > 2 ) {
				String provinceCode = code.substring(0, 2);
				context.write(new Text(provinceCode), new IntWritable(houses));
			}
		}
	}
	
	public static class MP3Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text provinceCode, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

			int totalVacias = 0;

			for (IntWritable value : values) {
				totalVacias += value.get();
			}

			// Emit the town code and the total number of empty houses
			context.write(provinceCode, new IntWritable(totalVacias));
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Please give valid inputs");
			System.exit(-1);
		}
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Viviendas vacias por provincia");
		
		job.setJarByClass(MP3.class);
		job.setMapperClass(MP3Mapper.class);
		job.setReducerClass(MP3Reducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
