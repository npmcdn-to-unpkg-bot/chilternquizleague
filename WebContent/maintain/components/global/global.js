maintainApp.controller('GlobalDetailCtrl', getCommonParams(function($scope,
		ctrlUtil) {
	
	ctrlUtil.makeUpdateFn("global", $scope, this,function(ret, $rootRouter) {
		$rootRouter.navigate(["Root"]);
	})
	ctrlUtil.makeListFn("season",$scope);
	ctrlUtil.makeListFn("text",$scope);
	ctrlUtil.makeListFn("user",$scope);
	
	$scope.addAlias=function(global){
		
		global.emailAliases.push({alias:null,user:null});
	};
	
	$scope.tinymceOptions = tinymceOptions;

}));