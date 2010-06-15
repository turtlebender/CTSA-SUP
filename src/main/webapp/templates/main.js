if(!window.ctsasup) window.ctsasup = {};
window.ctsasup.json = function(url,cb) {
    var id = "callback"+Math.random();
    var script = document.createElement('script');
    script.type = "text/javascript";
    window[id] = function(data) {
	document.body.removeChild(script);
	delete window[id];
	if(cb) cb(data);
    };
    script.src = url+(url.indexOf("?")<0?"?":"&")+"callback="+escape("window['"+id+"']");
    document.body.appendChild(script);
};
window.ctsasup.fload = function(path,cb) {
    this.json("http://localhost/load.php?file="+escape(path),cb);
};
window.ctsasup.place = function(map) {
    var me = this;
    for(var id in map) {
	(function() {
	    var elem = document.getElementById(id); if(!elem) return;
	    me.fload(map[id],function(data){elem.innerHTML=data['body']});
	})();
    }
};
window.ctsasup._load = function() {
    if(this.load) this.load();
};

window.addEventListener('load',function(){window.ctsasup._load()},false);
