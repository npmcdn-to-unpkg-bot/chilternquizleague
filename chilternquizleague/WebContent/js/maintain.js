(function() {

	var maintainApp = angular.module('maintainApp', [ 'ngRoute']);

	maintainApp.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/venues', {
			templateUrl : 'venue/venue-list.html',
			controller : 'VenueListCtrl'
		}).when('/venues/:venueId', {
			templateUrl : 'venue/venue-detail.html',
			controller : 'VenueDetailCtrl'
		}).when('/teams', {
			templateUrl : 'team/team-list.html',
			controller : 'TeamListCtrl'
		}).when('/teams/:teamId', {
			templateUrl : 'team/team-detail.html',
			controller : 'TeamDetailCtrl'
		}).when('/users', {
			templateUrl : 'user/user-list.html',
			controller : 'UserListCtrl'
		}).when('/users/:userId', {
			templateUrl : 'user/user-detail.html',
			controller : 'UserDetailCtrl'
		}).when('/seasons', {
			templateUrl : 'season/season-list.html',
			controller : 'SeasonListCtrl'
		}).when('/seasons/:userId', {
			templateUrl : 'season/season-detail.html',
			controller : 'SeasonDetailCtrl'
		}).otherwise({
			redirectTo : ''
		});
	} ]);

	function makeUpdateFn(typeName) {

		return function($scope, $http) {

			$scope.master = {};

			$http.get(
					"jaxrs/" + typeName + "/"
							+ document.location.hash.split("/").pop(), {
						"responseType" : "json"
					}).success(function(ret) {
				$scope.master = ret;

				$scope.reset();
			});

			$scope.update = function(entity) {

				$scope.master = angular.copy(entity);
				$http.post("jaxrs/" + typeName, $scope.master);
				document.location = "#" + typeName + "s";
			};

			$scope.reset = function() {
				$scope[typeName] = angular.copy($scope.master);
			};

		};

	}

	function makeListFn(typeName) {

		return function($scope, $http) {

			$http.get("jaxrs/" + typeName + "-list", {
				"responseType" : "json"
			}).success(function(ret) {
				$scope[typeName + "s"] = ret;
			});
		};
	}

	function syncToListItem($scope, entity, collection, propName) {

		if (entity[propName] && collection) {

			for (index in collection) {

				if (entity[propName].id == collection[index].id) {

					entity[propName] = collection[index];
					break;
				}
			}
		}

	}

	function removeFromListById(collection, entity) {

		for (index in collection) {
			if (collection[index].id == entity.id) {
				collection.splice(index, 1);
				break;
			}
		}
	}

	maintainApp.controller('VenueListCtrl', [ '$scope', '$http',
			makeListFn("venue") ]);

	maintainApp.controller('VenueDetailCtrl', [ '$scope', '$http',
			makeUpdateFn("venue") ]);

	maintainApp.controller('TeamListCtrl', [ '$scope', '$http',
			makeListFn("team") ]);

	maintainApp.controller('TeamDetailCtrl', [ '$scope', '$http',
			function($scope, $http) {
				makeUpdateFn("team")($scope, $http);
				makeListFn("venue")($scope, $http);
				makeListFn("user")($scope, $http);
				$scope.$watch("team", function(team) {
					syncToListItem($scope, team, $scope.venues, "venue");
				});
				$scope.$watch("venues", function(venues) {
					syncToListItem($scope, $scope.team, venues, "venue");
				});

				$scope.userToAdd = {};
				$scope.addUser = function(user) {
					$scope.team.users.push(user);
				};
				$scope.removeUser = function(user) {
					removeFromListById($scope.team.users, user);
				};
			} ]);

	maintainApp.controller('UserListCtrl', [ '$scope', '$http',
			makeListFn("user") ]);

	maintainApp.controller('UserDetailCtrl', [ '$scope', '$http',
			makeUpdateFn("user") ]);

	maintainApp.controller('SeasonListCtrl', [ '$scope', '$http',
			makeListFn("season") ]);

	maintainApp.controller('SeasonDetailCtrl', [ '$scope', '$http',function($scope,$http){
			$scope.addCompType={};
			makeUpdateFn("season")($scope, $http);
			makeListFn("competition-type")($scope, $http);
			$scope.updateEndYear=function(startYear){$scope.season.endYear = parseInt(startYear) + 1;};
	}]);
})();