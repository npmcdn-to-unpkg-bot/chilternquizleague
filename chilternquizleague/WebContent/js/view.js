var VIEW_SERVICE_DEFN = [ "$http", function($http) {
	function loadFromServer(type, params, callback) {

		var paramString = "";
		for(name in params){
			
			paramString = paramString + name + "=" + params[name] + "&";
		}
		
		paramString = paramString.length > 0 ? ("?"+ paramString.slice(0,-1)):"";
		
		return doLoad(type, paramString, callback);
	}
	
	function doLoad(type, paramString, callback){
		
		return $http.get("/view/" + type + paramString, {
			"responseType" : "json"
		}).success(callback);
	}

	var service = {

		load : function(type, id, callback) {

			return doLoad(type, "/" + id, callback);
		},
		
		view : function(type, params, callback){
			
			return loadFromServer(type, params, callback);
		},
		
		list : function(type, callback){
			
			return doLoad(type, "", callback);
		}
		
		
		

	};
	return service;
} ];