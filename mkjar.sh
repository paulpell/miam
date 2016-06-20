#/bin/bash

JAR=miam-bin.jar
JARSRC=miam-src.jar
BIN=bin
ENTRY_FILE=org/paulpell/miam/Snakesss.java

function msgNExit {
    echo $1
    exit $2
}

function compile {
    cd src || msgNExit 'no src/ dir!' 1
    javac -g -d "../${bin}" "${ENTRY_FILE}"
    cd ..
}


[ -d "${BIN}" ] || mkdir "${BIN}" || msgNExit 'cannot create bin dir' 2
echo compiling...
compile

[ 0 != $? ] && msgNExit 'bad compile'  3

echo 'creating jar itself'
echo Main-Class: org.paulpell.miam.Snakesss > Manifest.MF || msgNExit 'cannot create Manifest!'  4
jar cfm ${JAR} Manifest.MF org || msgNExit 'cannot create jar!'  5
jar uf ${JAR} images || msgNExit 'cannot update images!'  7
# use same jar and add src
echo 'creating src jar'
cp ${JAR} ${JARSRC} || msgNExit 'cannot copy jar file'  8
jar uf ${JARSRC} src

