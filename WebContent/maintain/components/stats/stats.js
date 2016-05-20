maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$location", function($scope, entityService, $location){
	
	this.$routerOnActivate = function(next){
		$scope.rebuildStats = function(){entityService.command("rebuild-stats",null, {"seasonId":next.params.seasonId}, function(){$location.url("/maintain/stats");})}	
		entityService.load("season",next.params.seasonId, function(season){
			$scope.season = season})
	}
}]);