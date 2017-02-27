#!/bin/bash

WD=`pwd`
PROXYADDRESS="PROXY 192.168.1.17:16808"
LOCALADDRESS="SOCKS5 127.0.0.1:1080; SOCKS 127.0.0.1:1080"
FINALRETURN="DIRECT"
if [[ `uname` == "Darwin" ]]
then
    SED=gsed
    cp $WD/pactemplate.js $WD/gfwlist.js
    $SED -i "s/PROXYADDRESS/$LOCALADDRESS/g" $WD/gfwlist.js
    $SED -i "s/FINALRETURN/$FINALRETURN/g" $WD/gfwlist.js
    ln -sf $WD/gfwlist.js ~/.ShadowsocksX/gfwlist.js
else
    SED=sed
fi

[ -e $WD/pactemplate.js  ] || exit 1

cp $WD/pactemplate.js $WD/a.pac
$SED -i "s/PROXYADDRESS/$PROXYADDRESS/g" $WD/a.pac
$SED -i "s/FINALRETURN/$FINALRETURN/g" $WD/a.pac
