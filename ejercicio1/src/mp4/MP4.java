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

public class MP4 {

	public static class MP4Mapper extends Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      // Separar las columnas
      String line = value.toString();
      String[] columnas = line.split(";");

      // Coger el valor del código postal si es que lo hay
      String codigo_postal = columnas[0].split(" ")[0];

      // Comprobar que es el valor del código postal
      try {
          int numero_provincia = Integer.parseInt(codigo_postal);
      } catch (NumberFormatException e) {
          return;
      } catch (IndexOutOfBoundsException e) {
          System.out.println("Error: la lista está vacía.");
      }

      // Comprobar si el código postal es válido
      if (numero_provincia / 1000 == 0) return;
      
      // Pasar del código postal del municipio al de la provincia
      numero_provincia = numero_provincia/1000
      
      // Añadir un 0 si es necesario (En realidad esto solo es para que quede bonito)
      String clave;
      if (numero_provincia < 10){
          clave = String.format("%02d", numero_provincia);
      } else{
          clave = String.valueOf(numero_provincia);
      }
      
      // Comprobar que estamos en un elemento que muestra el consumo eléctrico
      // Si estamos en el consumo eléctrico coger los varemos de electricidad
      String tipo = columnas.get(1);
      int electricidad_total;
      if(tipo.contains("Viviendas con cosumo entre")){
          String[] palabras_tipo = tipo.split(" ");
          int elec1 = palabras_tipo[4].replace(".", "");
          int elec2 = palabras_tipo[6].replace(".", "");
          
          
      } else return;

      //Conseguir el número de casas
      try {
          int num_casas = Integer.parseInt(columnas[2].replace(".", ""))
          } catch (NumberFormatException e) {
              return;
          }
      
      // Calcular electricidad total
      electricidad_total = num_casas*(elec1+elec2)/2
      context.write(new Text(clave), new IntWritable(electricidad_total));      
			
		}
	}
	
	public static class MP4Reducer extends Reducer<Text, Text, Text, DoubleWritable> {

		public void reduce(Text townName, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      int count = 0;
      //Contar el numero de casas con mismo código postal
      while (value.hasNext())
      {
          IntWritable i = value.next();
          count += i.get();
      }
      context.write(key, new IntWritable(count));
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
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
