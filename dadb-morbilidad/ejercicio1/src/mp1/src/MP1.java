// common imports
import java.io.IOException;

// hadoop types
import org.apache.hadoop.io.DoubleWritable;
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

public class MP1 {

	/*
	 * Calculate the town with the highest percentage of empty housing
	 * e.g. (Dima_t, 855)
	 * e.g. (Dima_e, 117)
	 */

	public static class MP1Mapper extends Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String line = value.toString();
			String[] fields = line.split(";");

			// We get the first char of the second word
			// 't' or 'v' stands for totales and vacias
			char type = fields[1].split(" ")[1].charAt(0);
			
			// First we must check that "Consumo electrico" is "totales" or "vacias"
			if( type != 't' && type != 'v' ) return;

			// Now we remove the dots from the String that represents a number, 1.285 => 1285
			fields[2] = fields[2].replace(".", "");

			// 	We get:
			//		* first number that appears on the line (code)
			//		* town name
			//		* the number of houses (third field)
			String[] code_plus_town = fields[0].split(" ");
			String code = code_plus_town[0];
			String town = code_plus_town[1];
			int houses = Integer.parseInt(fields[2]);
			
			// Code must be bigger than two in order to be a town, not a "comunidad"
			if( code.length() > 2 ) {
				context.write(
						new Text(town),
						new Text(String.valueOf(houses) + '_' + type)
					);
			}
		}
	}
	
	public static class MP1Reducer extends Reducer<Text, Text, Text, DoubleWritable> {

		public void reduce(Text townName, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			int vacias = 0, totales = 1;
			
			for( Text value : values ) {
				// We split the values in number of houses and type (vacias or totales)
				String[] parts = value.toString().split("_");
				int numHouses = Integer.parseInt(parts[0]);
				char type = parts[1].charAt(0);
				
				// if the type match we store the houses number
				if( type == 'v' ) vacias = numHouses;
				else if ( type == 't' ) totales = numHouses;
			}
			
			context.write(townName, new DoubleWritable(Double.valueOf(vacias) / Double.valueOf(totales)));
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Please give valid inputs");
			System.exit(-1);
		}
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Municipios con mayor porcentaje de viviendas vacias");
		
		job.setJarByClass(MP1.class);
		job.setMapperClass(MP1Mapper.class);
		job.setReducerClass(MP1Reducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
