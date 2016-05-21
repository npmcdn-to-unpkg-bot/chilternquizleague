maintainApp.controller("StatsDetailCtrl", ["$scope","entityService","$rootRouter", function($scope, entityService, $rootRouter){
	
	this.$routerOnActivate = function(next){
		var seasonId = next.params.seasonId
		
		$scope.rebuildStats = function(){entityService.command("rebuild-stats",null, {"seasonId":seasonId}, function(){$rootRouter.navigate(["Root", "Stats"]);})}	
		entityService.load("season",seasonId, function(season){
			$scope.season = season})
	}
}]);