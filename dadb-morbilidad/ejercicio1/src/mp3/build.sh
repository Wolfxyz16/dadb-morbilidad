# Make some clean up
rm -f bin/*
rm -f -r classes/*

# Build the path
if javac -classpath $(hadoop classpath) -d classes src/MP3.java; then
	jar -cvf bin/mp3.jar -C classes .
	echo "Succesfully build the jar file!"
	echo "It's time to run the ./run.sh"
fi
