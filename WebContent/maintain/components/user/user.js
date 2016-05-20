maintainApp.controller('UserListCtrl', getCommonParams(makeListFn("user")));

maintainApp.controller('UserDetailCtrl', ["$scope", "ctrlUtil", function($scope, ctrlUtil){
	
		ctrlUtil.makeUpdateFn("user",$scope,this)
}]);
