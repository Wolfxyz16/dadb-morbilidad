//Importing libraries
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
public class CSMapper extends MapReduceBase implements
    Mapper<LongWritable,Text, Text, IntWritable>
{
// Map function
    public void map(LongWritable key, Text value,
        OutputCollector<Text,IntWritable> output, Reporter rep) throws
        IOException
    {
        String line = value.toString();
        ArrayList<String> columnas = line.split(";");
        String codigo_postal = columnas.get(0).split(" ").get(0);
        try {
            int numero_provincia = Integer.parseInt(codigo_postal);
        } catch (NumberFormatException e) {
            int numero_provincia = 0;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: la lista está vacía.");
        }

        if (numero_provincia / 1000 == 0){
            numero_provincia = 999
        } else{
            numero_provincia = numero_provincia/1000
        }
        String clave = String.format("%02d", numero);
        String tipo = columnas.get(1);
        int valor;
        if (tipo.equalsIgnoreCase("Viviendas vacías")){
            try{
                valor = Integer.parseInt(columnas.get(2));
            } catch (NumberFormatException e) {
                valor = 0;
            } catch (IndexOutOfBoundsException e) {
                valor = 0;
            }
            
        } else{
            valor = 0;
        }
        output.collect(new Text(clave), new IntWritable(valor));      
    }
}