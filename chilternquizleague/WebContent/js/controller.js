;

var mainApp = angular.module('mainApp', ["ngRoute","ngAnimate"]).factory(
		'viewService',
		[
				"$http",
				function($http) {
					function loadFromServer(type, params, callback, isArray) {
						
						var paramString = "";
						for (name in params) {

							paramString = paramString + name + "=" + params[name] + "&";
						}

						paramString = paramString.length > 0 ? ("?" + paramString.slice(0,
								-1)) : "";

						return doLoad(type, paramString, callback, isArray);
					}

					function doLoad(type, paramString, callback, isArray) {
						
						var retval = isArray ? [] : {};
						
						function callbackWrapper(item){
							
							callback && item ? callback(item):null;
							angular.copy(item,retval);

						}
						
						$http.get("/view/" + type + paramString, {
							"responseType" : "json"
						}).success(callbackWrapper);
						
						return retval;
					}
					
					var textCache = {};

					var service = {

						load : function(type, id, callback) {

							return doLoad(type, "/" + id, callback);
						},

						view : function(type, params, callback) {

							var isArray = false;
							
							if(params){
								isArray = params.isArray;
								delete params.isArray;
							}

							return loadFromServer(type, params, callback,isArray);
						},

						list : function(type, callback) {

							return doLoad(type + "-list", "", callback, true);
						},

						post : function(type, payload, callback) {
							return $http.post("/view/" + type, payload).success(callback);
						},
						
						text : function(name,global) {
							var textHolder = textCache[name] = textCache.hasOwnProperty(name) ? textCache[name] : {};
							
							if(!textHolder.text){
								
								this.view("text",{id:global.textId, name:name}, function(text){
									textHolder.text = text;
								});
							}
							
							return textHolder;
						}
					};
					return service;
				} ]);

mainApp.config([ '$routeProvider','$locationProvider', function($routeProvider, $locationProvider) {
	$routeProvider.otherwise({
			templateUrl : '/indexContents.html'}
		);
	

	$locationProvider.html5Mode(true);
}]);

mainApp.filter("htmlify", ["$sce", function($sce){return function(text){
	
	return text ?  $sce.trustAsHtml(text) : "";
};}]);

mainApp.filter('afterNow', function() {
	return function(input) {
		var now = new Date().getTime();
		var ret = [];
		for (idx in input) {
			if (input[idx].start >= now) {
				ret.push(input[idx]);
			}
		}

		return ret;
	};
});

mainApp.controller('MainController', [ '$scope', '$interval', 'viewService',
		function($scope, $interval, viewService) {

			$scope.global = viewService.view("globaldata");

		} ]);
