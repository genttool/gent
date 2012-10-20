#!/bin/sh

##############################################################
# 1. Installing Groovy and Ivy jar
##############################################################
GENT=$HOME/.gent
cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        GENT=`cygpath "$USERPROFILE/.gent"`
        ;;
esac

mkdir -p $GENT
cd $GENT
curl -s -O http://cloud.github.com/downloads/chanwit/gent/groovy-all-188-ivy.pack.gz
unpack200 -r groovy-all-188-ivy.pack.gz groovy-all-ivy.jar

##############################################################
# 2. Installing Gent Groovy Script
##############################################################
curl -s -O https://raw.github.com/chanwit/gent/master/gent.groovy

##############################################################
# 3. Installing Gent Shell Script into ~/bin
##############################################################
BIN=$HOME/bin
mkdir -p $BIN

echo "#!/bin/sh

GENT=\$HOME/.gent
cygwin=false;
case \"\`uname\`\" in
    CYGWIN*)
        cygwin=true
        GENT=\`cygpath -w \"\$USERPROFILE/.gent\"\`
        ;;
esac
if \$cygwin ; then
    java -jar \"\$GENT\groovy-all-ivy.jar\" \"\$GENT\\gent.groovy\" \"\$@\"
else
    java -jar \"\$GENT/groovy-all-ivy.jar\" \"\$GENT/gent.groovy\" \"\$@\"
fi" > $BIN/gent

chmod a+x $BIN/gent

###
### $ gent init (resolve all required jars for the first time)
###

###
### $ gent create-app aaaa --template=chanwit/aaaa
###