var ENTITY_SERVICE_DEFN = [
		"$http",'$rootScope',
		function($http,$rootScope) {
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
					callback ? callback(ret) : null;
				};
			}

			function callbackWrapper(callback){
				
				return function(ret){
					$rootScope.$broadcast("progress", false);
					callback ? callback(ret.data) : null;
					return ret.data
				}
			}
			
			function loadFromServer(type, id, callback) {
				$rootScope.$broadcast("progress", true);
				
				return $http.get("/entity/" + type + "/" + id, {
					"responseType" : "json"
				}).then(callbackWrapper(callback));
			}

			function saveToServer(type, entity, callback) {
				$rootScope.$broadcast("progress", true);
				return $http.post("/entity/" + type, entity)
					.then(callbackWrapper(callback));

			}

			var service = {

				load : function(type, id, callback) {

					return  loadFromServer(type, id, callback);
				},

				save : function(type, entity, callback) {
					return saveToServer(type, entity, callback);
				},

				put : function(type, entity, id) {
					return cache.add(type, entity, id);
				},

				remove : function(type, id) {
					return cache.remove(type, id);
				},

				loadList : function(type, callback) {
					$rootScope.$broadcast("progress", true);
					return $http.get("/entity/" + type + "-list", {
						"responseType" : "json"
					}).then(callbackWrapper(callback));
				},
				
				command: function(command,content, params, callback){
					$rootScope.$broadcast("progress", true);
					return $http.post("/entity/" + command,content,{"params":params}).then(callbackWrapper(callback));
				},
				
				upload : function(file, fileName, callback){
					
					$rootScope.$broadcast("progress", true);
					var r  = new FileReader();
					
					r.onload = function() {
					    $http({
					        method: 'POST', 
					        url:"/entity/upload",
					        headers: {'Content-Type': file.type}, 
					        data: new Uint8Array(r.result), 
					        params : {name:fileName},
					        transformRequest: []
					    }).success(callbackWrapper(callback))
					};
					r.readAsArrayBuffer(file);
					
				}
			};
			return service;
		} ];