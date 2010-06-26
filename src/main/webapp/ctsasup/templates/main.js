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
    //Overwrite as needed
    //this.json("http://localhost/load.php?file="+escape(path),cb);
    $.get(path,function(data){if(cb)cb(data)});
};
window.ctsasup._place = function(id,data) {
    $("#"+id).html(data);
};
window.ctsasup.place = function(map) {
    var me = this;
    for(var id in map) {
	(function() {
	    var myid = id;
	    me.fload(map[myid],function(data){me._place(myid,data)});
	})();
    }
};
window.ctsasup._load = function() {
    if(this.preload) this.preload();
    if(this.load) this.load();
};
$(document).ready(function(){window.ctsasup._load()});
