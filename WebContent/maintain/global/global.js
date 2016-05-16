maintainApp.controller('GlobalDetailCtrl', getCommonParams(function($scope,
		entityService,  $rootScope, $location) {
	makeUpdateFnWithCallback("global", function(ret, $location) {
		$location.url("/maintain");
	})($scope, entityService, $rootScope, $location, this);
	makeListFn("season", {
		bindName : "currentSeason",
		entityName : "global"
	})($scope, entityService);

	makeListFn("text", {
		bindName : "globalText",
		entityName : "global"
	})($scope, entityService);
	
	entityService.loadList("user",function(users){$scope.users = users;});

	
	$scope.addAlias=function(global){
		
		global.emailAliases.push({alias:null,user:null});
	};
	
	$scope.tinymceOptions = tinymceOptions;

}));