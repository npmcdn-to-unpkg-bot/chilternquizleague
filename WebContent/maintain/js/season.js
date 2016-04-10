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
	function cleanEvent(){
		return {start:new Date(), end:new Date()}
	}
	$scope.event = cleanEvent()
	
	$scope.addEvent = function(event){$scope.season.calendar.push(event);$scope.event = cleanEvent()}
	$scope.setEvent = function(event){
		
		event.start = new Date(event.start)
		event.end = new Date(event.end)
		$scope.event = event;
		
	}
	$scope.ok = function(){$location.url("/maintain/seasons/"+seasonId)}
	$scope.removeEvent = function(event){$scope.season.calendar.splice($scope.season.calendar.indexOf(event),1)}
		
}));