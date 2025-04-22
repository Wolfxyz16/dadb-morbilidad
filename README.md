# Proyecto DADB

Proyecto en el que se analizan las tasas de Morbilidad Hospitalaria por 100.000 habitantes y estancias medias, según el sexo y el diagnóstico principal.

> [!IMPORTANT]
> FECHA DE ENTREGA, domingo 11 de mayo a las 23:59 😱😱
> FECHA DE PRESENTACIÓN, miercoles 7 de mayo

## Estructura del proyecto

Esta compuesto por dos ejercicios. Cada uno con su respectiva carpeta.

```bash
├── ejercicio1
│   ├── memoria.md
│   ├── poster.pdf
│   └── src
│       └── our_code
├── ejercicio2
│   ├── memoria.md
│   └── src
│       └── our_code
└── README.md
```

## Ejercicio 1, análisis de datos públicos con Hadoop MapReduce.

En este ejercicio, utilizaremos Apache Hadoop para analizar un conjunto de datos públicos disponible en el portal de datos abiertos del Gobierno de España. El objetivo es diseñar e implementar uno o varios algoritmos MapReduce para extraer estadísticas significativas y generar visualizaciones que aporten valor analítico.

### Tareas a realizar

1. Preparación de los datos

2. Implementación de algoritmos MapReduce, al menos 4 operaciones diferentes que ofrezcan diferentes análisis y estadísticos de valor.

3. Visualización de resultados, generar gráficos significativos.

### Recomendaciones

* Explorar bien el conjunto de datos antes de definir nuestros análisis

* Planificar la arquitectura de nuestra solución Hadoop considerando el volumen de datos

* Elegir visualizaciones que realmente aporten valor al análisis.

### Entregables

#### `ejercicio1/src`

* Scripts de preprocesamiento de datos
* Implementación de los algoritmos MapReduce
* Código para la generación de visualizaciones (opcional)

#### `ejercicio1/memoria.md`

* Descripción del conjunto de datos que hemos utilizado
* Metodología aplicada y justificación
* Descripción de los algoritmos MapReduce implementados
* Análisis detallado de los resultados obtenidos
* Conclusiones y posibles aplicaciones prácticas

#### `ejercicio1/poster.pdf`

* Breve descripción del conjunto de datos
* Metodología aplicada
* Principales resultados y visualizaciones
* Conclusiones más relevantes

---

## Ejercicio 2, ISS

La Estación Espacial Internacional (ISS) es una estación espacial habitada que orbita la Tierra a 400 km de altura. Es un proyecto internacional entre cinco agencias espaciales: NASA, Roscosmos, JAXA, ESA y CSA.

La ISS ha estado habitada de forma continuada desde el año 2000. Normalmente hay entre 3 y 7 astronautas a bordo realizando trabajos de investigación en microgravedad. La ISS realiza una vuelta completa a la Tierra cada 90 minutos. Eso quiere decir, que las y los astronautas a bordo del ISS visualizan en un día terrestre hasta 16 amaneceres y atardeceres.

Open Notify API es una API pública y abierta que permite obtener datos en tiempo real relacionados con la ISS y la presencia humana en el espacio.

Mediante este ejercicio, se solicita *calcular cuánto se ha movido (en km) la ISS por ventanas de 30 segundos*.

Para ello, se espera seguir la siguiente arquitectura:

![Esquema del ejercicio 2 del proyecto](img/esquema1.jpg) 

### Tareas

1. Desarrollar un script en Python que recoja la información de ubicación (latitud y longitud) de la ISS cada 2 segundos y almacene dicha información en un fichero log (`iss_logger.py`).

2. Desarrollo de un agente Apache Flume que recoja la información desde un fichero log y la envía a HDFS.

3. Por último, otro script de Python, en el que mediante la ayuda de Apache Spark Streaming, se recoja la información nueva desde HDFS, y cada 30 segundos calcule la distancia recorrida por la ISS en km.

> [!NOTE]
> Se debe desarrollar en `cloudera` que nos propociona ya toda la infraestructura necesaria. `python`, `flume`, `hdfs`, `yarn`, `spark streaming`.

### Recomendaciones

1. Orden para poner en marcha la arquitectura completa:

    1. Agente Apache Flume
    2. Script de Python que recoge desde Open Notify API la ubicación de la ISS cada 2 segundos
    3. Script de Apache streaming

```bash
spark-submit -master yarn-client script.py
```

2. Agente Apache Flume

[TODO]

### Entregables

1. Código completo con los programas Apache Flume y Apache Spark Streaming.

2. Memoria técnica (formato pdf) que incluya:

    * Descripción de los datos que se obtienen desde 
