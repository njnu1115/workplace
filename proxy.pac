function FindProxyForURL(url, host) {
    if (shExpMatch(host, "*.bmwgroup.net")) {
        return "SOCKS5 127.0.0.1:3080";
    } else if (isPlainHostName(host)) {
        return "DIRECT";
    } else {
        return "DIRECT;PROXY 127.0.0.1:3128";
    }

    return "DIRECT";
}
