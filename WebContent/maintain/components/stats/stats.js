maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$location", function($scope, entityService, $location){
	
	this.$routerOnActivate = function(next){
		var seasonId = next.params.seasonId
		
		$scope.rebuildStats = function(){entityService.command("rebuild-stats",null, {"seasonId":seasonId}, function(){$location.url("stats");})}	
		entityService.load("season",seasonId, function(season){
			$scope.season = season})
	}
}]);