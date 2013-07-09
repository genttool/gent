#!/bin/sh

#############################################################################
#
#   The GENT tool (c) 2012-2013 Chanwit Kaewkasi and contributors
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
#############################################################################

#############################################################################
# 1. Installing Groovy and Ivy jar
#############################################################################
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
"$JAVA_HOME/bin/unpack200" -r groovy-all-1.8.8-ivy.pack.gz groovy-all-ivy.jar

#############################################################################
# 2. Installing Gent Groovy Script
#############################################################################
curl -s -O https://raw.github.com/genttool/gent/master/gent.groovy

#############################################################################
# 3. Installing Gent Shell Script into ~/bin
#############################################################################
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
    java -jar \"\$GENT\groovy-all-ivy.jar\" \$GENT_OPTS \"\$GENT\\gent.groovy\" \"\$@\"
else
    java -jar \"\$GENT/groovy-all-ivy.jar\" \$GENT_OPTS \"\$GENT/gent.groovy\" \"\$@\"
fi" > $BIN/gent

chmod a+x $BIN/gent

export GENT_OPTS="-Dgroovy.grape.report.downloads=true"
$BIN/gent init