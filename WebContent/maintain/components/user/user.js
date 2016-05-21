maintainApp.controller('UserListCtrl', getCommonParams(function($scope, ctrlUtil){
	
	ctrlUtil.makeListFn("user",$scope)
}));

maintainApp.controller('UserDetailCtrl', getCommonParams(function($scope, ctrlUtil){
	
		ctrlUtil.makeUpdateFn("user",$scope,this)
}));
