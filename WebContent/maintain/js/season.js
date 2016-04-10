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
		$location.url("/maintain/seasons/" + seasonId + "/competition/" + type.name);
	};
	$scope.removeCompetition = function(competition){
		for(compType in $scope.season.competitions){
			if(compType == competition.type){
				delete $scope.season.competitions[compType]
			}
		}
	}
	
}));

maintainApp.controller('SeasonCalendarCtrl', getCommonParams(function($scope, entityService, $routeParams,
		$rootScope, $location) {
	var seasonId = $routeParams.seasonId;
	$scope.seasonId = seasonId;
	makeListFn("venue")($scope, entityService);
		
}));