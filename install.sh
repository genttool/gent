#!/bin/sh

GENT=$HOME/.gent
cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        GENT=`cygpath "$USERPROFILE/.gent"`
        ;;
esac

mkdir -p $GENT/.gent
cd $GENT/.gent
curl -O http://cloud.github.com/downloads/chanwit/gent/groovy-all-ivy.pack.gz
unpack200 -r groovy-all-ivy.pack.gz groovy-all-ivy.jar

### if(~/bin does not exists) mkdir ~/bin
### create ~/bin/gent
###
### java -Dgroovy.grape.report.downloads=true -jar groovy-all-ivy.jar gent.groovy "$@"
### unix:   $HOME
### cygwin: $USERPROFILE
###

###
### $ gent init (resolve all required jars for the first time)
###

###
### $ gent create-app aaaa --template=chanwit/aaaa
###