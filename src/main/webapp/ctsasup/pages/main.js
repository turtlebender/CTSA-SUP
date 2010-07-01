if(!window.ctsasup) window.ctsasup = {};

/*
  newBatch() - Create a new Batch
  .add(uri,callback,data,dataType) - Add work to the Batch
  .execute() - Do all work in the Batch (in one request if possible)
*/
window.ctsasup.newBatch = function() {
    return {
	'_todo': [],
	'_finally': null,
	'add': function(uri,cb,data,type) {
	    var me = this;
	    this._todo.push(function() {//This could look prettier
		    $.ajax({url:uri,dataType:type,data:data,success:
			    function(data) {
				if(cb) cb(data);
				if(me._todo.length != 0) return;
				if(me._finally) me._finally();
			    }
			})});
	},
	'execute': function(cb) {
	    this._finally = cb;
	    if(this._todo.length == 0 && cb) cb();
	    while(this._todo.length > 0) {
		this._todo.shift().call(this);
	    }
	}
    };
};

/*
  .resolve(base,path) - Basic URI resolver
*/
window.ctsasup.resolve = function(base,path) {
    if(!base || path.match(/^\w+:\/\//)) return path;
    var results = base.match(/^(.*?)([^\/]*)$/);
    base = results[1];
    if(path.match(/^\//)) {
	var results = base.match(/^(\w+:\/\/[^\/]+)/);
	if(!results) return path;
	return results[1]+path;
    }
    return base+path;
};

/*
  .get(uri,callback) - Fetch the text content of the requested URI and feed it to callback function
*/
window.ctsasup.get = function(uri,cb) {
    $.get(uri,function(data){if(cb)cb(data)});
};

/*
  .place(id,content) - Put the text content into the element with the specified ID
*/
window.ctsasup.place = function(id,data) {
    $("#"+id).html(data);
};

/*
  .contentify(object,[batch]) - Recursively seek out unfilled data requests in object and fill them.
  If the batch parameter is specified, add them to the batch instead of requesting them directly.
*/
window.ctsasup.contentify = function(o,batch) {
    if(!o || typeof(o) != "object") return;
    if(o.uri && !o.content) {
	(batch?batch.add:$.get)(o.uri,function(data) {
		o.content = data;
		if(o.callback) o.callback(data);
	    });
    }
    for(var k in o) { this.contentify(o[k]); }
};

/*
  .preload(preload,base) - Load/execute a preload object with the specified base URI
  .preload(preload,base,batch) - Load/execute a preload object with the specified base URI and Batch
 */
window.ctsasup.preload = function(preload,base,batch) {
    if(!preload) return;
    var me = this;
    if(!batch) {
	var batch = this.newBatch();
	window.ctsasup.preload(preload,base,batch);
	batch.execute(); return;
    }
    for(var id in preload) {
	(function() { //Again, scope is too big
	    var callback = preload[id].callback;
	    if(callback && typeof(callback) == "string") { callback = eval(callback); }
	    if(preload[id].content) {
		if(callback) callback(preload[id].content);
	    } else if(preload[id].uri) {
		batch.add(me.resolve(base,preload[id].uri),callback);
	    }
	})();
    }
}

/*
  .process(descriptor) - Set up the page according to the page descriptor object
  .process(uri) - Set up the page according to the page descriptor JSON at the specifed URI string
    base - Use base for relative URIs (optional)
*/
window.ctsasup.process = function(desc,base) {
    if(!desc) return;
    var me = this;
    if(typeof(desc) == "string") {
	$.getJSON(desc,function(data){me.process(data)});
	return;
    }
    if(typeof(desc) != "object") return;
    var batch = this.newBatch();
    if(desc.slotMappings) {
	for(var id in desc.slotMappings) {
	    if(desc.slotMappings[id].content) {
		this.processc(id,desc.slotMappings[id].content);
	    } else if(desc.slotMappings[id].uri) {
		(function() { //Function wrapper necessary because "id" is overwritten next loop
		    var myid = id;
		    var uri = me.resolve(base,desc.slotMappings[myid].uri);
		    batch.add(uri,function(data) {
			    desc.slotMappings[myid].content = data; //Why not save it for future use?
			    me.processc(myid,data,uri);
			},null,"json");
		})();
	    }
	}
    }
    batch.execute(function() {
	    me.preload(desc.preload,base);
	});
};

/*
  .processc(id,descriptor) - Set up the slot according to the component descriptor object
  .processc(id,uri) - Set up the slot according to the component descriptor JSON at the specifed URI string
    base - Use base for relative URIs (optional)
*/
window.ctsasup.processc = function(id,desc,base) {
    if(!desc) return;
    var me = this;
    if(typeof(desc) == "string") {
        $.getJSON(desc,function(data){me.processc(data)});
        return;
    }
    if(typeof(desc) != "object") return;
    var batch = this.newBatch();
    if(desc.content.content) {
	this.place(id,desc.content.content);
    } else if(desc.content.uri) {
	batch.add(this.resolve(base,desc.content.uri),function(data) {
		me.place(id,data);
	    });
    }
    batch.execute(function() {
	    me.preload(desc.preload,base);
	});
};

window.ctsasup._load = function() {
    if(this.load) this.load();
};

$(document).ready(function(){window.ctsasup._load()});
