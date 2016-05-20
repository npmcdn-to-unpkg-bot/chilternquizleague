maintainApp.controller('SeasonListCtrl', getCommonParams(makeListFn("season")));

maintainApp.controller('SeasonDetailCtrl', getCommonParams(function($scope, entityService,
		$rootScope, $location, ctrlUtil) {

	$scope.addCompType = {};
	ctrlUtil.bindToParent("season", $scope,this)
	ctrlUtil.makeFormFns("season",$scope,this)
	ctrlUtil.makeListFn("competitionType",$scope);
	
  $scope.updateEndYear = function(startYear) {
		$scope.season.endYear = parseInt(startYear) + 1;
	};
	$scope.addCompetition = function(type) {
		$location.url("seasons/" + $scope.seasonId + "/competition/new/" + type.name);
	};
	$scope.removeCompetition = function(competition){
		for(compType in $scope.season.competitions){
			if(compType == competition.type){
				delete $scope.season.competitions[compType]
			}
		}
	}

}));

maintainApp.controller('SeasonCtrl', getCommonParams(function($scope, entityService,	$rootScope, $location, ctrlUtil){
	
	ctrlUtil.makeUpdateFn("season", $scope, this)
	ctrlUtil.addWatchFn($scope,this)
}));

maintainApp.controller('SeasonCalendarCtrl', getCommonParams(function($scope, entityService, 
		$rootScope, $location, ctrlUtil) {
	ctrlUtil.bindToParent("season", $scope,this)
	ctrlUtil.makeListFn("venue",$scope);
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
	$scope.ok = function(season){$location.url("seasons/" + season.id)}
	$scope.removeEvent = function(event){$scope.season.calendar.splice($scope.season.calendar.indexOf(event),1)}
		
}));