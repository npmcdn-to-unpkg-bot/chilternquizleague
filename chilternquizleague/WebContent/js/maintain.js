(function() {

	var maintainApp = angular.module('maintainApp', [ 'ngRoute' ]);

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
		}).when('/seasons/:seasonId/league/:competitionId', {
			templateUrl : 'competition/league-detail.html',
			controller : 'LeagueCompCtrl'
		}).when('/league/:competitionId/fixtures', {
			templateUrl : 'competition/fixtures.html',
			controller : 'FixturesCtrl'
		}).when('/league/:competitionId/results', {
			templateUrl : 'competition/results.html',
			controller : 'ResultsCtrl'
		}).when('/league/:competitionId/tables', {
			templateUrl : 'competition/tables.html',
			controller : 'TablesCtrl'
		}).otherwise({
			redirectTo : ''
		});
	} ]);

	maintainApp.run(function($rootScope) {
		/*
		 * Receive emitted message and broadcast it. Event names must be
		 * distinct or browser will blow up!
		 */
		$rootScope.$on('addedCompetition', function(event, args) {
			$rootScope.$broadcast('addCompetition', args);
		});
	});

	function makeUpdateFn(typeName, backAfterUpdate) {

		var camelName = typeName.charAt(0).toUpperCase()
				+ typeName.substr(1).toLowerCase();

		return function($scope, $http, $routeParams) {

			$scope.master = {};

			$http.get(
					"jaxrs/" + typeName + "/" + $routeParams[typeName + "Id"],
					{
						"responseType" : "json"
					}).success(function(ret) {
				$scope["master" + camelName] = ret;
				$scope["reset" + camelName]();
			});

			$scope["update" + camelName] = function(entity) {

				$scope["master" + camelName] = angular.copy(entity);
				$http.post("jaxrs/" + typeName, $scope["master" + camelName]);
				if (backAfterUpdate) {
					document.location = "#" + typeName + "s";
				}
			};

			$scope["reset" + camelName] = function() {
				$scope[typeName] = angular.copy($scope["master" + camelName]);
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

	function getCommonParams(constructorFn) {

		return [ '$scope', '$http', '$routeParams', constructorFn ];
	}

	maintainApp.controller('VenueListCtrl',
			getCommonParams(makeListFn("venue")));

	maintainApp.controller('VenueDetailCtrl',
			getCommonParams(makeUpdateFn("venue")));

	maintainApp.controller('TeamListCtrl', getCommonParams(makeListFn("team")));

	maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope,
			$http, $routeParams) {
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
	}));

	maintainApp.controller('UserListCtrl', getCommonParams(makeListFn("user")));

	maintainApp.controller('UserDetailCtrl',
			getCommonParams(makeUpdateFn("user")));

	maintainApp.controller('SeasonListCtrl',
			getCommonParams(makeListFn("season")));

	maintainApp.controller('SeasonDetailCtrl', getCommonParams(function($scope,
			$http, $routeParams) {
		$scope.season = {};
		$scope.addCompType = {};
		makeUpdateFn("season")($scope, $http, $routeParams);
		makeListFn("competitionType")($scope, $http);
		$scope.updateEndYear = function(startYear) {
			$scope.season.endYear = parseInt(startYear) + 1;
		};
		$scope.addCompetition = function(type) {

		};

		$scope.$on('addCompetition', function(event, args) {
			$scope.season = args;
		});
	}));

	maintainApp.controller('LeagueCompCtrl', getCommonParams(function($scope,
			$http, $routeParams) {
		makeUpdateFn("leagueCompetition");
		$scope.addCompetition 
		  $rootScope.$on('added', function(event, args) {
		        $rootScope.$broadcast('handleBroadcast', args);
		    });
	}));

	maintainApp.controller('FixturesCtrl',
			getCommonParams(function($scope, $http, $routeParams) {
				$scope.currentDate = new Date();
				makeUpdateFn("competition")($scope, $http, $routeParams);
				makeListFn("team")($scope, $http);
				$scope.usedTeams = {};
				$scope.advanceDate = function() {
					$scope.currentDate.setDate($scope.currentDate.getDate()
							+ (7 * 60 * 60 * 24));
					usedTeams[currentDate.toDateString()] = [];
				};
				$scope.addFixture = function(fixture) {
					fixture.date = currentDate();
					$scope.competition.fixtures[date.toDateString()] = fixture;
					$scope.newFixture = {};
				};
				$scope.removeFixture = function(fixture) {
					removeFromListById(competition.fixtures[fixture.date
							.toDateString()], fixture);
				};
			}));
})();