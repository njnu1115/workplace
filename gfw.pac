function isMatchProxy(url, pattern) {
    try {
        return new RegExp(pattern.replace('.', '\.')).test(url);
    } catch (e) {
        return false;
    }
}

function FindProxyForURL(url, host) {
    var Proxy = "SOCKS 192.168.16.68:3080";
    var BAProxy ="HTTPS 192.168.16.68:58118";
    var list = ["onedrive.live.com","telegram","google","facebook","twitter","android","googleapis","googleusercontent","chrome","tumblr","t.co","youtu","blogspot","wordpress","wikipedia.org","wikileaks","dropbox","appspot","chromium","g.co","ggpht.com","gmail","goo.gl","gvt0.com","gvt1.com","picasaweb","akamai","blogger","clubhouse","instagram"];
    for(var i=0, l=list.length; i<l; i++) {
        if (isMatchProxy(url, list[i])) {
            return BAProxy;
        }
    }
    return 'DIRECT';
}
