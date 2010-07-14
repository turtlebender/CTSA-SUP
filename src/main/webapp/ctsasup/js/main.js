if (!window.ctsasup) window.ctsasup = {};

/*
  .fixer(object,base) - Apply the .resolve function to every .src and .href in the DOM object with base
 */
window.ctsasup.fixer = function(o,base) {
    if(!o || !base) return o;
    if(!o.getAttribute || !o.setAttribute) return o;
    if(o.getAttribute("src")) o.setAttribute("src",this.resolve(base,o.getAttribute("src")));
    if(o.getAttribute("href")) o.setAttribute("href",this.resolve(base,o.getAttribute("href")));
    var child = o.firstChild;
    while(child) {
	this.fixer(child,base);
	child = child.nextSibling;
    }
    return o;
};

/*
 .newBatch() - Create a new Batch
 .add(uri,callback,data,dataType) - Add work to the Batch
 .execute() - Do all work in the Batch (in one request if possible)
 */
window.ctsasup.newBatch = function() {
    if (window.osapi && window.osapi.newBatch) {
        return window.ctsasup.newBatch_osapi();
    }
    //These lines are commented so we default to jquery instead of jsonproxy
    //} else if(window.location.protocol == "file:") {
    return window.ctsasup.newBatch_jquery();
    //return window.ctsasup.newBatch_jsonproxy();
};

/*
 .newBatch_jquery() - Create a new AJAX/jQuery (direct) Batch
 */
window.ctsasup.newBatch_jquery = function() {
    return {
        '_todo': [],
        '_finally': null,
        'add': function(uri, cb, data, type) {
            var me = this;
            this._todo.push(function() {//This could look prettier
                $.ajax({url:uri,dataType:type,data:data,success:
                        function(data) {
                            if (cb) cb(data);
                            if (me._todo.length != 0) return;
                            if (me._finally) me._finally();
                        }
                })
            });
        },
        'execute': function(cb) {
            this._finally = cb;
            if (this._todo.length == 0 && cb) cb();
            while (this._todo.length > 0) {
                this._todo.shift().call(this);
            }
        }
    };
};

/*
 .newBatch_jsonproxy() - Create a new JSONP (proxy) Batch
 */
window.ctsasup.newBatch_jsonproxy = function() {
    return {
        '_todo': [],
        '_finally': null,
        'add': function(uri, cb, data, type) {
            var me = this;
            this._todo.push(function() {//This could look prettier
                $.ajax({url:"http://ci.azich.org/proxy.php?uri=" + escape(window.ctsasup.resolve("" + window.location, uri)),dataType:"jsonp",data:data,success:
                        function(data) {
                            if (!data || !data.content) return;
                            data = data.content;
                            if (cb) cb(type == "json" ? $.parseJSON(data) : data);
                            if (me._todo.length != 0) return;
                            if (me._finally) me._finally();
                        }
                })
            });
        },
        'execute': function(cb) {
            this._finally = cb;
            if (this._todo.length == 0 && cb) cb();
            while (this._todo.length > 0) {
                this._todo.shift().call(this);
            }
        }
    };
};

/*
 .newBatch_osapi() - Create a new OpenSocial Batch
 */
window.ctsasup.newBatch_osapi = function() {
    return {
        '_batch': osapi.newBatch(),
        '_order': [],
        '_params': {},
        'add': function(uri, cb, data, type) { //data is currently ignored
            var id = "batch" + Math.random();
            this._order.push(id);
            this._order.push(id);
            this._params[id] = {cb:cb,data:data,type:type};
            this._batch.add(id, uri);
        },
        'execute': function(cb) {
            this._batch.execute(function(result) {
                for (var i = 0; i < this._order.length; i++) {
                    var id = this._order[i];
                    var params = this._params[id];
                    if (params.cb) params.cb(params.type == "json" || params.type == "jsonp" ? $.parseJSON(result[id]) : result[id]);
                }
                if (cb) cb();
            });
        }
    };
}

/*
 .resolve(base,path) - Basic URI resolver
 */
window.ctsasup.resolve = function(base, path) {
    if (!base || path.match(/^\w+:\/\//)) return path;
    var results = base.match(/^(.*?)([^\/]*)$/);
    base = results[1];
    if (path.match(/^\//)) {
        var results = base.match(/^(\w+:\/\/[^\/]+)/);
        if (!results) return path;
        return results[1] + path;
    }
    return base + path;
};

/*
 .get(uri,callback) - Fetch the text content of the requested URI and feed it to callback function
 */
window.ctsasup.get = function(uri, cb) {
    $.get(uri, function(data) {
        if (cb)cb(data)
    });
};

/*
 .place(id,content) - Put the text content into the element with the specified ID
 .place(id,content,base) - Apply .fixer to the content before placing it
 */
window.ctsasup.place = function(id, data, base) {
    if(base) {
	var obj = document.createElement('DIV');
	obj.innerHTML = data;
	this.fixer(obj,base);
	data = obj.innerHTML;
    }
    $("#" + id).html(data);
};

/*
 .contentify(object,[batch]) - Recursively seek out unfilled data requests in object and fill them.
 If the batch parameter is specified, add them to the batch instead of requesting them directly.
 */
window.ctsasup.contentify = function(o, batch) {
    if (!o || typeof(o) != "object") return;
    if (o.uri && !o.content) {
        (batch ? batch.add : $.get)(o.uri, function(data) {
            o.content = data;
            if (o.callback) o.callback(data);
        }, null, o.dataType);
    }
    for (var k in o) {
        this.contentify(o[k]);
    }
};

/*
 .preload(preload,base) - Load/execute a preload object with the specified base URI
 .preload(preload,base,batch) - Load/execute a preload object with the specified base URI and Batch
 */
window.ctsasup.preload = function(preload, base, batch) {
    if (!preload) return;
    var me = this;
    if (!batch) {
        var batch = this.newBatch();
        window.ctsasup.preload(preload, base, batch);
        batch.execute();
        return;
    }
    for (var id in preload) {
        (function() { //Again, scope is too big
            var callback = preload[id].callback;
            if (callback && typeof(callback) == "string") {
                callback = eval(callback);
            }
            if (preload[id].content) {
                if (callback) callback(preload[id].content);
            } else if (preload[id].uri) {
                batch.add(me.resolve(base, preload[id].uri), callback, null, preload[id].dataType);
            }
        })();
    }
}

/*
 .process(descriptor) - Set up the page according to the page descriptor object
 .process(uri) - Set up the page according to the page descriptor JSON at the specifed URI string
 base - Use base for relative URIs (optional)
 */
window.ctsasup.process = function(desc, base) {
    if (!desc) return;
    var me = this;
    if (typeof(desc) == "string") {
        $.ajax({
            url: desc,
	    dataType: "json",
            beforeSend: function(xhr) {
		xhr.setRequestHeader("Accept", "application/org.globus.cs.webdef.page+json");
            },
            success: function(data){
		    me.process(data, base);
            }
        });
        return;
    }
    if (typeof(desc) != "object") return;
    var batch = this.newBatch();
    if (desc.slotMappings) {
        for (var id in desc.slotMappings) {
            if (desc.slotMappings[id].content) {
                this.processc(id, desc.slotMappings[id].content);
            } else if (desc.slotMappings[id].uri) {
                (function() { //Function wrapper necessary because "id" is overwritten next loop
                    var myid = id;
                    var uri = me.resolve(base, desc.slotMappings[myid].uri);
                    batch.add(uri, function(data) {
                        desc.slotMappings[myid].content = data; //Why not save it for future use?
                        me.processc(myid, data, uri);
                    }, null, "json");
                })();
            }
        }
    }
    batch.execute(function() {
        me.preload(desc.preload, base);
    });
};

/*
 .processc(id,descriptor) - Set up the slot according to the component descriptor object
 .processc(id,uri) - Set up the slot according to the component descriptor JSON at the specifed URI string
 base - Use base for relative URIs (optional)
 */
window.ctsasup.processc = function(id, desc, base) {
    if (!desc) return;
    var me = this;
    if (typeof(desc) == "string") {
        $.getJSON(desc, function(data) {
            me.processc(data)
        });
        return;
    }
    if (typeof(desc) != "object") return;
    var batch = this.newBatch();
    if (desc.content.content) {
        this.place(id, desc.content.content, base);
    } else if (desc.content.uri) {
        batch.add(this.resolve(base, desc.content.uri), function(data) {
		me.place(id, data, base);
        }, null, desc.content.dataType);
    }
    batch.execute(function() {
        me.preload(desc.preload, base);
    });
};

window.ctsasup._load = function() {
    if (this.load) this.load();
};

$.ajaxSetup({"beforeSend":function(xhr) {
    if (xhr.overrideMimeType)xhr.overrideMimeType("application/data")
}});
$(document).ready(function() {
    window.ctsasup._load()
});
