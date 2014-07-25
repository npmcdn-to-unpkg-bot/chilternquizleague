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

}));