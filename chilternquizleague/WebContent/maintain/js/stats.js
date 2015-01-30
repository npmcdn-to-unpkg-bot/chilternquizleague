maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$location","$stateParams", function($scope, entityService, $location, $routeParams){
	
	$scope.rebuildStats = function(){entityService.command("rebuild-stats", {"seasonId":$routeParams.seasonId}, function(){$location.url("/maintain/stats");})}	

	
}]);