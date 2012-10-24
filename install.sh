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
curl -s -O http://cloud.github.com/downloads/genttool/gent/groovy-all-1.8.8-ivy.pack.gz
$JAVA_HOME/bin/unpack200 -r groovy-all-1.8.8-ivy.pack.gz groovy-all-ivy.jar

##############################################################
# 2. Installing Gent Groovy Script
##############################################################
curl -s -O https://raw.github.com/genttool/gent/master/gent.groovy

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
