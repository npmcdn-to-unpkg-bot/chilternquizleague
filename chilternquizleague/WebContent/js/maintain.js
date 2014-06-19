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
		}).when('/seasons/:seasonId', {
			templateUrl : 'season/season-detail.html',
			controller : 'SeasonDetailCtrl'
		}).otherwise({
			redirectTo : ''
		});
	} ]);

	function makeUpdateFn(typeName) {

		return function($scope, $http, $routeParams) {

			$scope.master = {};

			$http.get(
					"jaxrs/" + typeName + "/" + $routeParams[typeName + "Id"]
							, {
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

	
	function getCommonParams(constructorFn){
		
		return ['$scope', '$http','$routeParams', constructorFn];
	}
	
	
	maintainApp.controller('VenueListCtrl', getCommonParams(
			makeListFn("venue")));

	maintainApp.controller('VenueDetailCtrl', getCommonParams(
			makeUpdateFn("venue") ));

	maintainApp.controller('TeamListCtrl', getCommonParams(
			makeListFn("team") ));

	maintainApp.controller('TeamDetailCtrl',getCommonParams(
			function($scope, $http, $routeParams) {
				makeUpdateFn("team")($scope, $http, $routeParams);
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
			} ));

	maintainApp.controller('UserListCtrl', getCommonParams(
			makeListFn("user") ));

	maintainApp.controller('UserDetailCtrl', getCommonParams(
			makeUpdateFn("user") ));

	maintainApp.controller('SeasonListCtrl', getCommonParams(
			makeListFn("season") ));

	maintainApp.controller('SeasonDetailCtrl', getCommonParams(function($scope,$http){
			makeUpdateFn("season")($scope, $http, $routeParams);
			makeListFn("competition-type")($scope, $http);
	}));
})();