maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$location","$stateParams", function($scope, entityService, $location, $routeParams){
	
	$scope.rebuildStats = function(){entityService.command("rebuild-stats",null, {"seasonId":$routeParams.seasonId}, function(){$location.url("/maintain/stats");})}	
	entityService.load("season",$routeParams.seasonId, function(season){
		$scope.season = season})
	
}]);