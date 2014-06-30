var VIEW_SERVICE_DEFN = [ "$http", function($http) {
	function loadFromServer(type, params, callback) {

		var paramString = "";
		for(name in params){
			
			paramString = paramString + name + "=" + params[name] + "&";
		}
		
		paramString = paramString.length > 0 ? ("?"+ paramString.slice(0,-1)):"";
		
		return $http.get("/view/" + type + "/" + paramString, {
			"responseType" : "json"
		}).success(callback);
	}

	var service = {

		load : function(type, callback, params) {

			return loadFromServer(type, params, callback);
		},

	};
	return service;
} ];