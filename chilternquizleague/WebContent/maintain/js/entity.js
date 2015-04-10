var ENTITY_SERVICE_DEFN = [
		"$http",
		function($http) {
			var cacheHolder = {};
			function makeEntryKey(type, id) {
				return type + (id ? id : "new");
			}
			var cache = {
				add : function(type, entity, id) {
					cacheHolder[makeEntryKey(type, id ? id : entity.id)] = entity;
					return entity;
				},

				remove : function(type, id) {

					var ret = cache.get(type, id);
					cacheHolder[makeEntryKey(type, id)] = null;
					return ret;
				},

				flush : function() {
					cacheHolder = {};
				},

				get : function(type, id) {

					var key = makeEntryKey(type, id);
					return cacheHolder.hasOwnProperty(key) ? cacheHolder[key]
							: null;
				}

			};

			function cacheCallbackFactory(type, callback, id) {
				return function(ret) {
					cache.add(type, ret, id ? id : ret.id);
					callback ? callback(ret) : null;
				};
			}

			function loadFromServer(type, id, callback) {

				$http.get("/entity/" + type + "/" + id, {
					"responseType" : "json"
				}).success(callback).error(cache.flush);
			}

			function saveToServer(type, entity, callback) {
				$http.post("/entity/" + type, entity).success(callback).error(
						cache.flush);

			}

			var service = {

				load : function(type, id, callback) {
					var entity = cache.get(type, id);

					entity ? (callback ? callback(entity) : null)
							: loadFromServer(type, id, cacheCallbackFactory(
									type, callback, id));
				},

				save : function(type, entity, callback) {
					saveToServer(type, entity, cacheCallbackFactory(type,
							callback));
				},

				put : function(type, entity, id) {
					return cache.add(type, entity, id);
				},

				remove : function(type, id) {
					return cache.remove(type, id);
				},

				loadList : function(type, callback) {
					$http.get("/entity/" + type + "-list", {
						"responseType" : "json"
					}).success(callback).error(cache.flush);
				},
				
				command: function(command,content, params, callback){
					$http.post("/entity/" + command,content,{"params":params}).success(callback);
				},
				
				upload : function(file, fileName, callback){
					
					var r  = new FileReader();
					
					r.onload = function() {
					    $http({
					        method: 'POST', 
					        url:"/entity/upload",
					        headers: {'Content-Type': file.type}, 
					        data: new Uint8Array(r.result), 
					        params : {name:fileName},
					        transformRequest: []
					    }).success(callback)
					};
					r.readAsArrayBuffer(file);
					
				}
			};
			return service;
		} ];