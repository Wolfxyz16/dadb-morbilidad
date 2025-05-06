//Importing libraries
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class CSMapper extends MapReduceBase implements Mapper<LongWritable,Text, Text, IntWritable> {
  public void map(LongWritable key, Text value, OutputCollector<Text,IntWritable> output, Reporter rep) throws IOException {
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
      output.collect(new Text(clave), new IntWritable(electricidad_total));      
  }
}
