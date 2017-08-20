function isMatchProxy(url, pattern) {
    try {
        return new RegExp(pattern.replace('.', '\.')).test(url);
    } catch (e) {
        return false;
    }
}

function FindProxyForURL(url, host) {
    var Proxy = 'SOCKS5 192.168.1.101:3080;';
    var list = ["onedrive.live.com","telegram","google","facebook","twitter","android","chrome","tumblr","t.co","youtu","blogspot","wordpress","wikipedia.org","wikileaks","dropbox","appspot","chromium","g.co","ggpht.com","gmail","goo.gl","gvt0.com","gvt1.com","picasaweb","akamai","blogger"];
    for(var i=0, l=list.length; i<l; i++) {
        if (isMatchProxy(url, list[i])) {
            return Proxy;
        }
    }
    return 'DIRECT';
}
