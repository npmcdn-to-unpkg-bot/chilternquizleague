maintainApp.controller('SeasonListCtrl', getCommonParams(makeListFn("season")));

maintainApp.controller('SeasonDetailCtrl', getCommonParams(function($scope, entityService, $routeParams,
		$rootScope, $location) {
	var seasonId = $routeParams.seasonId;
	$scope.seasonId = seasonId;
	$scope.addCompType = {};
	makeUpdateFn("season")($scope, entityService, $routeParams, $rootScope,
			$location);
	makeListFn("competitionType")($scope, entityService);
	$scope.updateEndYear = function(startYear) {
		$scope.season.endYear = parseInt(startYear) + 1;
	};
	$scope.addCompetition = function(type) {
		entityService.put("season", $scope.season, "current");
		$location.url("/maintain/seasons/" + seasonId + "/" + type.name);
	};

}));