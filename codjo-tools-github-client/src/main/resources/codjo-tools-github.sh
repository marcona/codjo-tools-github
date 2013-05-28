#!/bin/bash
# ######################################################################
#
# Codjo tools github : sh to launch the excellent codjo-tools-github
# --------------------
#
#   See https://github.com/marcona/codjo-tools-github.git
# 
# .bash_profile sample declaration :
#	function gh() {
#	      . codjo-tools-github.sh $1 'XXXXXXXGITHUB_PASSWORD' 'XXXXXXXPASSWORD' $2
#	}
#
#	# set PATH so it includes user's private bin if it exists
#	if [ -d "$HOME/bin" ] ; then
#	    PATH="$HOME/bin:$PATH"
#	fi
#
#
# ######################################################################


#_CODJO_TOOLS_GITHUB_ROOT=~/dev/tools/codjo-tools-github
_CODJO_TOOLS_GITHUB_ROOT=XXXXXXX

_GITHUB_METHOD=$1
_GITHUB_USER_NAME=$2
_GITHUB_PASSWORD=$3
_GITHTUB_REPO=$4

#You must have your java home set !
java -jar $_CODJO_TOOLS_GITHUB_ROOT/codjo-tools-github.jar $_GITHUB_METHOD $_GITHUB_USER_NAME $_GITHUB_PASSWORD $_GITHTUB_REPO


