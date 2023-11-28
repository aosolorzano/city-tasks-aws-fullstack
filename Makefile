
build-CityEventsFunction:
	echo "Building CityEventsFunction for 'dev' environment..."
	mvn -T 4C clean native:compile -Pnative -DskipTests -f ./apis/city-events-function/pom.xml -Ddependency-check.skip=true
	cp ./apis/city-events-function/target/native $(ARTIFACTS_DIR)
	cp ./apis/city-events-function/tools/shell/bootstrap $(ARTIFACTS_DIR)
	chmod 755 $(ARTIFACTS_DIR)/bootstrap
