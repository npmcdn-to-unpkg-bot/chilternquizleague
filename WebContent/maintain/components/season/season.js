maintainApp.controller('SeasonListCtrl', getCommonParams(function($scope, ctrlList){
		ctrlList.makeListFn("season", $scope)
}));

maintainApp.controller('SeasonDetailCtrl', ["$scope","ctrlUtil", "$rootRouter", function($scope, ctrlUtil, $rootRouter) {

	$scope.addCompType = {};
	ctrlUtil.bindToParent("season", $scope,this)
	ctrlUtil.makeFormFns("season",$scope,this)
	ctrlUtil.makeListFn("competitionType",$scope);
	
	function makeTypeComponentName(type){
		
		return type.substring(0,1) + type.toLowerCase().substring(1) + "Detail"
		
	}
	
  $scope.updateEndYear = function(startYear) {
		$scope.season.endYear = parseInt(startYear) + 1;
	};
	$scope.addCompetition = function(type) {
		$rootRouter.navigate(["Root", "Season",{seasonId:$scope.season.id}, "Competition", {competitionId:"new"},makeTypeComponentName(type.name)]);
	};
	$scope.removeCompetition = function(competition){
		for(compType in $scope.season.competitions){
			if(compType == competition.type){
				delete $scope.season.competitions[compType]
			}
		}
	}

}]);

maintainApp.controller('SeasonCtrl', getCommonParams(function($scope, ctrlUtil){
	
	ctrlUtil.makeUpdateFn("season", $scope, this)
	ctrlUtil.addWatchFn($scope,this)
}));

maintainApp.controller('SeasonCalendarCtrl', ["$rootRouter"].concat(getCommonParams(function($rootRouter,$scope,ctrlUtil) {
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
	$scope.ok = function(season){$rootRouter.navigate(["Root", "Season", {seasonId:season.id} ])}
	$scope.removeEvent = function(event){$scope.season.calendar.splice($scope.season.calendar.indexOf(event),1)}
		
})));