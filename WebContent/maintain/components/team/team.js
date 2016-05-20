maintainApp.controller('TeamListCtrl', getCommonParams(makeListFn("team")));

maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope,
		entityService, $rootScope, $location,ctrlUtil) {
	ctrlUtil.makeUpdateFn("team", $scope, this)
	
	makeListFn("venue", {
		entityName : "team",
		bindName : "venue",
		sort : function(venue1, venue2) {
			return venue1.name.localeCompare(venue2.name);
		}
	})($scope, entityService);
	makeListFn("user")($scope, entityService);

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