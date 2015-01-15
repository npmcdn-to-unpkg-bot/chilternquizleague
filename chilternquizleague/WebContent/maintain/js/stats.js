maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$location","$routeParams", function($scope, entityService, $location, $routeParams){
	
	$scope.rebuildStats = function(){entityService.command("rebuild-stats", {"seasonId":$routeParams.seasonId});	$location.url("/maintain/stats");};

	
}]);