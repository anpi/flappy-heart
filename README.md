To install Tween engine (dependency):
1. Install Maven (e.g. on OS X with Homebrew `brew install mvn`)
1. Download and extract https://code.google.com/p/java-universal-tween-engine/downloads/detail?name=tween-engine-api-6.3.3.zip
2. `mvn install:install-file -Dfile=tween-engine-api.jar -DgroupId=aurelienribon -DartifactId=tweenengine -Dversion=6.3.3 -Dpackaging=jar`
3. `mvn install:install-file -Dfile=tween-engine-api-sources.jar -DgroupId=aurelienribon -DartifactId=tweenengine -Dversion=6.3.3 -Dpackaging=jar -Dclassifier=sources`

