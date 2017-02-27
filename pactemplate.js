function suffix(s1, s2) {
    return s1.indexOf(s2, s1.length - s2.length) !== -1
};

function FindProxyForURL(url, host) {
    var PROXY = "PROXYADDRESS";
    var DEFAULT = "DIRECT";
    if (suffix(host, ".cn")) return DEFAULT;
    if (isInNet(host, "91.108.4.0", "255.255.252.0") ||
        isInNet(host, "91.108.56.0", "255.255.252.0") ||
        isInNet(host, "149.154.160.0", "255.255.240.0") ||
        isInNet(host, "203.104.208.0", "255.255.252.0") ||
        isInNet(host, "125.6.128.0", "255.255.192.0")
    ) {
      return PROXY;
    } else if (host.match(/\d$/)) return DEFAULT;

    var a2 = ["azure.com", "task-notes.com", "bintray.io", "bintray.com", "mktoresp.com", "oktopost.com", "scdn.co", "alphassl.com", "singlehop.com", "vpsto.com", "shadowsocks.com", "blogblog.com", "blogspot.jp", "jetbrains.com", "sstatic.net", "feedburner.com", "roodo.com", "livedoor.com", "blog.jp", "slack.com", "optimizely.com", "serverfault.com", "nginx.org", "stackoverflow.com", "akamaihd.net", "cython.org", "pypy.org", "docopt.org", "gravatar.com", "quantserve.com", "scorecardresearch.com", "adzerk.net", "githubusercontent.com", "cdninstagram.com", "gvt1.com", "docker.io", "bit.ly", "noahapps.jp", "sega-mj.com", "7ina.moe","elastic.co", "amazonaws.com", "uptimerobot.com", "linode.com", "digitalocean.com", "ehentai.org", "ehgt.org", "dropboxusercontent.com", "dropbox.com", "dropboxstatic.com", "tdesktop.com", "cloudflare.com", "mongodb.org", "apache.org", "maven.org", "google.co.id", "149.154.175", "h1g.jp", "duckduckgo.com", "wikiwiki.jp", "felixc.at", "telegram.org", "telegram.me", "dreamhost.com", "db.tt", "xtube.com", "bitbucket.com", "bitbucket.org", "nicovideo.jp", "pastebin.com", "slideshare.net", "slidesharecdn.com", "adobe.com", "playstation.net", "sonyentertainmentnetwork.com", "nintendo.co.jp", "sourceforge.net", "doubleclick.net", "ggpht.com", "ytimg.com", "yahoo.co.jp", "dmm.com", "dmm.co.jp", "shadowsocks.org", "lvv2.com", "yam.com", "tenhou.net", "mjv.jp", "google.com.sg", "deviantart.com", "tumblr.com", "txmblr.com", "cedexis.com", "cedexis-test.com", "google-analytics.com", "steampowered.com", "steamcommunity.com", "llnwd.net", "gstatic.com", "googleusercontent.com", "github.com", "he.net", "akamaitechnologies.com", "digicert.com", "instagram.com", "googleapis.com", "1e100.net", "twimg.com", "fdb713.com", "google.co.jp", "google.com.hk", "mobypicture.com", "fastly.net", "blogger.com", "googlevideo.com", "youtube-nocookie.com", "puppetlabs.com", "launchpad.net", "npmjs.org", "bukkit.org", "debian.org", "xda-developers.com", "frankfang.com", "fc2.com", "marc.info", "favstar.fm", "ask.fm", "hidemyass.com", "bbc.co.uk", "bbci.co.uk", "bbc.com", "imrworldwide.com", "revsci.net", "oracle.com", "4sqi.net", "foursquare.com", "4sq.com", "google.com", "mail.google.com", "nytimes.com", "evernote.com", "google.com.hk", "wikipedia.org", "zh.wikipedia.org", "googlecode.com", "googlepages.com", "googlevideo.com", "mstatic.com", "gc.apple.com", "akamai.net", "youtube.com", "twitter.com", "t.co", "mac.com", "facebook.net", "facebook.com", "blogspot.com", "wordpress.com", "pandora.com", "heqinglian.net", "t66y.com", "cl.ufree.org", "sha7.info", "1024.inc.gs", "c1521.biz.tm", "nextmedia.com", "tcno.net", "global.hkepc.com", "www.radioaustralia.net.au", "markmail.org", "hkheadline.com", "picturedip.com", "nemesis2.qx.net", "blog.qooza.hk", "youversion.com", "yfrog.com", "cloudfront.net", "igfw.net", "gmail.com", "pinporn.com", "sex.com", "appledaily.com", "sharpdaily.com.hk", "asana.com", "fbcdn.net", "radiotime.com", "flickr.com","icu-project.org","spring.io","youtu.be"];
    for (i = 0; i < a2.length; i++) {
        if (suffix(host, a2[i])) {
            return PROXY;
        }
    }
    return "FINALRETURN";
}
