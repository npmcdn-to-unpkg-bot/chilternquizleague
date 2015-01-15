maintainApp.controller('GlobalDetailCtrl', getCommonParams(function($scope,
		entityService, $routeParams, $rootScope, $location) {
	makeUpdateFnWithCallback("global", function(ret, $location) {
		$location.url("/maintain.html");
	})($scope, entityService, $routeParams, $rootScope, $location);
	makeListFn("season", {
		bindName : "currentSeason",
		entityName : "global"
	})($scope, entityService);

	makeListFn("text", {
		bindName : "globalText",
		entityName : "global"
	})($scope, entityService);
	
	entityService.loadList("user",function(users){$scope.users = users;});

	
	$scope.addAlias=function(global){
		
		global.emailAliases.push({alias:"change me",user:null});
	};
	
	$scope.tinymceOptions = tinymceOptions;

}));