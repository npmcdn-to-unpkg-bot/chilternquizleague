maintainApp.controller('TeamListCtrl', getCommonParams(makeListFn("team")));

maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope,
		entityService, $routeParams, $rootScope, $location) {
	makeUpdateFn("team")($scope, entityService, $routeParams, $rootScope,
			$location);
	makeListFn("venue", {
		entityName : "team",
		bindName : "venue",
		sort : function(venue1, venue2) {
			return venue1.name.localeCompare(venue2.name);
		}
	})($scope, entityService);
	makeListFn("user")($scope, entityService);

	$scope.userToAdd = {};
	$scope.addUser = function(user) {
		$scope.team.users.push(user);
	};
	$scope.removeUser = function(user) {
		removeFromListById($scope.team.users, user);
	};
}));