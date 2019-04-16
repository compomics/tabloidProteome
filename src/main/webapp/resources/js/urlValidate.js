function InvalidURLException() {
	this.message = "An attempt was made to open a webpage of foreign domain. No allowed.";
	this.toString = function() {
		return this.message
	};
}

function validateURL(surl) {
	var url = parseURL(surl);
	var urlHostname = url.hostname.trim();

	if (urlHostname == '') {
		return true;
	} else {
		if (urlHostname.toUpperCase() == location.hostname.trim().toUpperCase()) {
			return true;
		} else
			return false;
	}
}

function parseURL(url) {
	var a = document.createElement('a');
	a.href = url;
	return {
		source : url,
		protocol : a.protocol.replace(':', ''),
		hostname : a.hostname,
		host : a.host,
		port : a.port,
		query : a.search,
		params : (function() {
			var ret = {}, seg = a.search.replace(/^\?/, '').split('&'), len = seg.length, i = 0, s;
			for (; i < len; i++) {
				if (!seg[i]) {
					continue;
				}
				s = seg[i].split('=');
				ret[s[0]] = s[1];
			}
			return ret;
		})(),
		file : (a.pathname.match(/\/([^\/?#]+)$/i) || [ , '' ])[1],
		hash : a.hash.replace('#', ''),
		path : a.pathname.replace(/^([^\/])/, '/$1'),
		relative : (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [ , '' ])[1],
		segments : a.pathname.replace(/^\//, '').split('/')
	};
}