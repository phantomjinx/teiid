#!/bin/bash

#############
#
# Script to resolve the plugin's lib directory dependencies
# and generate the SQLParser sources using javacc
#
# Note. This has to build both the target platform and the
#       spi plugin in order to satisfy the dependencies of
#       this plugin (even though we are not actually 
#       compiling it!)
#
#############

PLUGIN_HOME=$PWD
GIT_HOME=`cd "../../.."; pwd`
SCRIPTS_HOME="$GIT_HOME/scripts"
MVN="$SCRIPTS_HOME/maven-wrapper.sh"

# Build the target platform and install it
echo "=== Installing target platform ==="
cd "$GIT_HOME/target-platform"
$MVN install

# Build the spi dependency and install it
echo "=== Installing the spi plugin dependency ==="
cd "$GIT_HOME/plugins/org.teiid.designer.spi"
$MVN install

cd "$PLUGIN_HOME"
$MVN process-sources -Pgenerate-javacc
