#!/bin/sh

echo "downloading data from ine"

wget --output-document=./viviendas.csv https://www.ine.es/jaxi/files/tpx/csv_bdsc/59531.csv --progress=dot:binary --no-verbose --show-progress

echo "done!"
