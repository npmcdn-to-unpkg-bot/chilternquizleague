(function() {

	var maintainApp = angular
			.module('maintainApp', [ "ngRoute" ])
			.factory(
					'entityService',
					[
							"$http",
							function($http) {
								var cacheHolder = {};
								function makeEntryKey(type, id) {
									return type + (id ? id : "new");
								}
								var cache = {
									add : function(type, entity, id) {
										cacheHolder[makeEntryKey(type, id ? id
												: entity.id)] = entity;
										return entity;
									},

									remove : function(type, id) {

										var ret = service.get(type, id);
										cacheHolder[makeEntryKey(type, id)] = null;
										return ret;
									},

									flush : function() {
										cacheHolder = {};
									},

									get : function(type, id) {

										var key = makeEntryKey(type, id);
										return cacheHolder.hasOwnProperty(key) ? cacheHolder[key]
												: null;
									}

								};

								function cacheCallbackFactory(type, callback,
										id) {
									return function(ret) {
										cache.add(type, ret, id ? id : ret.id);
										callback ? callback(ret) : null;
									};
								}

								function loadFromServer(type, id, callback) {

									$http.get("jaxrs/" + type + "/" + id, {
										"responseType" : "json"
									}).success(callback).error(cache.flush);
								}

								function saveToServer(type, entity, callback) {
									$http.post("jaxrs/" + type, entity)
											.success(callback).error(cache.flush);

								}

								var service = {

									load : function(type, id, callback) {
										var entity = cache.get(type, id);

										entity ? (callback ? callback(entity)
												: null) : loadFromServer(type,
												id, cacheCallbackFactory(type,
														callback, id));
									},

									save : function(type, entity, callback) {
										saveToServer(type, entity,
												cacheCallbackFactory(type,
														callback));
									},
									
									put : function(type,entity,id){
										return cache.add(type, entity, id);
									},
									
									remove : function(type, id){
										return cache.remove(type, id);
									},
									
									loadList : function(type, callback){$http.get("jaxrs/" + type + "-list", {
										"responseType" : "json"
									}).success(callback).error(cache.flush);}
								};
								return service;
							} ]);
	;

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
		}).when('/seasons/LEAGUE/:leagueCompetitionId', {
			templateUrl : 'competition/league-detail.html',
			controller : 'LeagueCompCtrl'
		}).when('/league/:leagueCompetitionId/fixtures', {
			templateUrl : 'competition/fixtures.html',
			controller : 'FixturesCtrl'
		}).when('/league/:leagueCompetitionId/results', {
			templateUrl : 'competition/results.html',
			controller : 'ResultsCtrl'
		}).when('/league/:leagueCompetitionId/tables', {
			templateUrl : 'competition/tables.html',
			controller : 'TablesCtrl'
		}).otherwise({
			redirectTo : ''
		});
	} ]);

	function makeUpdateFn(typeName, noRedirect) {
		return makeUpdateFnWithCallback(typeName, noRedirect ? null : function(ret, $location){$location.url("/" + typeName + "s");});
	}

	function makeUpdateFnWithCallback(typeName, callback) {

		var camelName = typeName.charAt(0).toUpperCase() + typeName.substr(1);

		var masterName = "master" + camelName;
		var resetName = "reset" + camelName;

		return function($scope, entityService, $routeParams, $rootScope, $location) {

			var id = $routeParams[typeName + "Id"];


			$scope[resetName] = function() {
				$scope[typeName] = angular.copy($scope[masterName]);
			};

			entityService.load(typeName,id, function(ret){
				$scope[masterName] = ret;
				$scope[resetName]();
			});
			


			$scope["update" + camelName] = function(entity) {

				$scope[masterName] = angular.copy(entity);
				entityService.remove(typeName,id);
				entityService.save(typeName, entity, function(ret){callback ? callback(ret, $location) : null;});

			};

		};

	}

	function makeListFn(typeName) {

		return function($scope, entityService) {

			entityService.loadList(typeName,function(ret) {
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

		return [ '$scope', 'entityService', '$routeParams', '$rootScope', '$location',
				 constructorFn ];
	}

	maintainApp.controller('VenueListCtrl',
			getCommonParams(makeListFn("venue")));

	maintainApp.controller('VenueDetailCtrl',
			getCommonParams(makeUpdateFn("venue")));

	maintainApp.controller('TeamListCtrl', getCommonParams(makeListFn("team")));

	maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope, entityService,
			$http, $routeParams, $rootScope, $location) {
		makeUpdateFn("team")($scope,entityService, $http, $routeParams, $rootScope,
				$location);
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

	var seasonBody = getCommonParams(function($scope,entityService, $http, $routeParams,
			$rootScope, $location) {
		var seasonId = $routeParams.seasonId;
		$scope.addCompType = {};
		makeUpdateFn("season")($scope, entityService,$http, $routeParams, $rootScope,
				$location);
		makeListFn("competitionType")($scope, $http);
		$scope.updateEndYear = function(startYear) {
			$scope.season.endYear = parseInt(startYear) + 1;
		};
		$scope.addCompetition = function(type) {
			entityService.put("season", $scope.season, "current");
			$location.url("/seasons/" + type.name + "/new");
		};

		$rootScope
				.$on(
						'addCompetition',
						function(event, args) {
							$scope.season.competitions[args.competition.type] = args.competition;
							entityService.put("season", $scope.season);
							entityService.remove("season", "current");
							$location.url("/seasons/" + seasonId);
						});
	});

	maintainApp.controller('SeasonDetailCtrl', seasonBody);

	maintainApp.controller('LeagueCompCtrl', getCommonParams(function($scope,entityService,
			$http, $routeParams, $rootScope, $location) {

		makeUpdateFnWithCallback("leagueCompetition", null, true)($scope,entityService,
				$http, $routeParams, $rootScope, $location);

		$scope.addLeagueCompetition = function(competition) {
			$rootScope.$emit('addCompetition', {
				competition : competition
			});
		};

		$rootScope.$on("addFixtures", function(event, args) {
			$scope.leagueCompetition.fixtures = args.fixtures;

		});

	}));

	maintainApp.controller('FixturesCtrl', getCommonParams(function($scope,
			$http, $routeParams, $rootScope, $location) {
		function asDate(num) {
			return new Date(num);
		}
		;
		function toKey(date) {
			return "D" + date.getYear() + date.getMonth() + date.getDate();
		}
		;
		$scope.currentDate = new Date();
		$scope.fixtures = {};
		$scope.fixtures[toKey($scope.currentDate)] = [];

		makeUpdateFnWithCallback("fixture", function(fixture) {
			$scope.fixtures[toKey(asDate(fixture.date))].push(fixture);
			;
		}, true)($scope, $http, $routeParams, $rootScope, $location);
		makeListFn("team")($scope, $http);
		$scope.usedTeams = {};
		$scope.advanceDate = function() {
			$scope.currentDate = new Date($scope.currentDate.getTime()
					+ (7 * 60 * 60 * 24 * 1000));

			var key = toKey($scope.currentDate);
			usedTeams[key] = usedTeams[key] ? usedTeams[key] : [];
			$scope.fixtures[key] = $scope.fixtures[key] ? $scope.fixtures[key]
					: [];

		};
		$scope.addFixture = function(fixture) {
			fixture.date = $scope.currentDate.getTime();
			$scope.updateFixture(fixture);
			$scope.fixture = {};
		};
		$scope.removeFixture = function(fixture) {
			removeFromListById(fixtures[toKey(asDate(fixture.date))], fixture);
		};

		$scope.updateFixtures = function(fixtures) {
			$rootScope.$emit("addFixtures", {
				"fixtures" : fixtures
			});
		};
	}));
})();