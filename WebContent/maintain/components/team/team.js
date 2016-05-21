maintainApp.controller('TeamListCtrl', getCommonParams(function($scope, ctrlUtil){
	ctrlUtil.makeListFn("team", $scope)
}));

maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope,ctrlUtil) {
	ctrlUtil.makeUpdateFn("team", $scope, this)
	
	ctrlUtil.makeListFn("venue", $scope);
	
	ctrlUtil.makeListFn("user", $scope);

	$scope.matchUsers = function(users,text){
		
		return users.filter(function(user){return user.name.indexOf(text) > -1})
		
	}
	
	$scope.userToAdd = null;
	$scope.addUser = function(user) {
		$scope.team.users.push(user);
	};
	$scope.removeUser = function(user) {
		removeFromListById($scope.team.users, user);
	};
	
	$scope.tinymceOptions = tinymceOptions;
}));