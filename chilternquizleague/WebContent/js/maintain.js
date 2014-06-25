(function() {

	var maintainApp = angular
			.module('maintainApp', [ "ngRoute","ngAnimate" ])
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

										var ret = cache.get(type, id);
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
											.success(callback).error(
													cache.flush);

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

									put : function(type, entity, id) {
										return cache.add(type, entity, id);
									},

									remove : function(type, id) {
										return cache.remove(type, id);
									},

									loadList : function(type, callback) {
										$http.get("jaxrs/" + type + "-list", {
											"responseType" : "json"
										}).success(callback).error(cache.flush);
									}
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
		}).when('/seasons/:seasonId/LEAGUE', {
			templateUrl : 'competition/league-detail.html',
			controller : 'LeagueCompCtrl'
		}).when('/seasons/:seasonId/:compType/fixtures', {
			templateUrl : 'competition/fixtures.html',
			controller : 'FixturesCtrl'
		}).when('/seasons/:seasonId/LEAGUE/results', {
			templateUrl : 'competition/results.html',
			controller : 'ResultsCtrl'
		}).when('/seasons/:seasonId/LEAGUE/tables', {
			templateUrl : 'competition/tables.html',
			controller : 'TablesCtrl'
		}).when('/global/current', {
			templateUrl : 'global/global-detail.html',
			controller : 'GlobalDetailCtrl'
		}).otherwise({
			redirectTo : ''
		});
	} ]);

	function makeUpdateFn(typeName, noRedirect) {
		return makeUpdateFnWithCallback(typeName, (noRedirect ? null
				: function(ret, $location) {
					$location.url("/" + typeName + "s");
				}));
	}

	function makeUpdateFnWithCallback(typeName, saveCallback, loadCallback) {

		var camelName = typeName.charAt(0).toUpperCase() + typeName.substr(1);

		var masterName = "master" + camelName;
		var resetName = "reset" + camelName;

		return function($scope, entityService, $routeParams, $rootScope,
				$location) {

			var id = $routeParams[typeName + "Id"];

			$scope[resetName] = function() {
				$scope[typeName] = angular.copy($scope[masterName]);
			};

			entityService.load(typeName, id, function(ret) {
				$scope[masterName] = ret;
				$scope[resetName]();
				loadCallback ? loadCallback(ret) : null;
			});

			$scope["update" + camelName] = function(entity) {

				$scope[masterName] = angular.copy(entity);
				entityService.remove(typeName, id);
				entityService.save(typeName, entity, function(ret) {
					saveCallback ? saveCallback(ret, $location) : null;
				});

			};

		};

	}

	function makeListFn(typeName, bindingConfig) {

		return function($scope, entityService) {
			var collectionName = typeName + "s";

			if (bindingConfig) {
				collectionName = (bindingConfig.collName ? bindingConfig.collName
						: collectionName);
				$scope.$watch(bindingConfig.entityName, function(entity) {
					syncToListItem($scope, entity, $scope[collectionName],
							bindingConfig.bindName);
				});
				$scope.$watch(collectionName, function(collection) {
					syncToListItem($scope, $scope[bindingConfig.entityName],
							collection, bindingConfig.bindName);
				});

			}
			entityService.loadList(typeName, function(ret) {
				$scope[collectionName] = ret;
			});
		};
	}

	function syncToListItem($scope, entity, collection, propName) {

		if (entity && entity[propName] && collection) {

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

		return [ '$scope', 'entityService', '$routeParams', '$rootScope',
				'$location', constructorFn ];
	}

	maintainApp.controller('VenueListCtrl',
			getCommonParams(makeListFn("venue")));

	maintainApp.controller('VenueDetailCtrl',
			getCommonParams(makeUpdateFn("venue")));

	maintainApp.controller('TeamListCtrl', getCommonParams(makeListFn("team")));

	maintainApp.controller('TeamDetailCtrl', getCommonParams(function($scope,
			entityService, $routeParams, $rootScope, $location) {
		makeUpdateFn("team")($scope, entityService, $routeParams, $rootScope,
				$location);
		makeListFn("venue", {
			entityName : "team",
			bindName : "venue"
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

	maintainApp.controller('UserListCtrl', getCommonParams(makeListFn("user")));

	maintainApp.controller('UserDetailCtrl',
			getCommonParams(makeUpdateFn("user")));

	maintainApp.controller('SeasonListCtrl',
			getCommonParams(makeListFn("season")));

	var seasonBody = getCommonParams(function($scope, entityService,
			$routeParams, $rootScope, $location) {
		var seasonId = $routeParams.seasonId;
		$scope.seasonId = seasonId;
		$scope.addCompType = {};
		makeUpdateFn("season")($scope, entityService, $routeParams, $rootScope,
				$location);
		makeListFn("competitionType")($scope, entityService);
		$scope.updateEndYear = function(startYear) {
			$scope.season.endYear = parseInt(startYear) + 1;
		};
		$scope.addCompetition = function(type) {
			entityService.put("season", $scope.season, "current");
			$location.url("/seasons/" + seasonId + "/" + type.name);
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

	maintainApp.controller('LeagueCompCtrl', getCommonParams(function($scope,
			entityService, $routeParams, $rootScope, $location) {

		var seasonId = $routeParams.seasonId;
		$scope.seasonId = seasonId;
		makeUpdateFnWithCallback("season",null, function(ret) {
			if (!ret.competitions.LEAGUE) {
				$scope.$watch("leagueCompetition", function(comp){
					$scope.masterSeason.competitions.LEAGUE = comp;
				});
				makeUpdateFn("leagueCompetition")($scope, entityService,
						$routeParams, $rootScope, $location);
			} else {
				$scope.masterLeagueCompetion = ret.competitions.LEAGUE;
				$scope.leagueCompetition = angular
						.copy(ret.competitions.LEAGUE);
			}
		})($scope, entityService, $routeParams, $rootScope,
				$location);
		
		$scope.addLeagueCompetition = function(competition) {
			$scope.masterSeason.competitions.LEAGUE = $scope.leagueCompetition;
			$location.url("/seasons/" + seasonId);
		};

	}));
	
	function UsedTeamsControl(teams, $scope){
		
		this.teams = teams;
		this.dateMap = {};
		this.$scope = $scope;
		this.makeKey = function(date) {
			return "D" + date.getYear()+date.getMonth()+date.getDate();
		};
		this.currentKey = this.setDate(new Date());

	}
	
	UsedTeamsControl.prototype.setDate = function(date){
		this.currentKey = this.makeKey(date);
		
		this.$scope.unusedTeams = this.dateMap[this.currentKey] = this.dateMap.hasOwnProperty(this.currentKey) ? this.dateMap[this.currentKey] : angular.copy(this.teams);
	};
	
	UsedTeamsControl.prototype.getUnused = function(){
		
		return this.$scope.unusedTeams;
	};
	
	UsedTeamsControl.prototype.add = function(team1,team2){
		
		var teams = this.getUnused();
		
		function removeTeam(team){
			removeFromListById(teams,team);
		}
		
		removeTeam(team1);
		team2 ? removeTeam(team2) : null;
	};
	
	UsedTeamsControl.prototype.remove = function(date,team1,team2){
		var teams = this.dateMap[this.makeKey(date)];
		
		teams.push(team1);
		team2 ? teams.push(team2) : null;
		
	};

	maintainApp
			.controller(
					'FixturesCtrl',
					getCommonParams(function($scope, entityService,
							$routeParams, $rootScope, $location) {

						var compType = $routeParams.compType;
						var seasonId = $routeParams.seasonId;
						
						function resolveExistingUnusedTeams(fixturesList){
							
							var utc = $scope.usedTeamsControl = new UsedTeamsControl($scope.teams, $scope);
							
							for(idx in fixturesList){
								
								utc.setDate(fixturesList[idx].date);
								
								for( idx2 in fixturesList[idx].fixtures)
								{
									var fixture = fixturesList[idx].fixtures[idx2];
									
									utc.add(fixture.home, fixture.away);
								};
								}
							
							var date = $scope.currentDate ? $scope.currentDate : new Date();
							utcl.setDate(date);

						}

						makeUpdateFnWithCallback(
								"season",null,
								function(season) {
									$scope.fixturesList = season.competitions[compType].fixtures;
									for(idx in $scope.fixturesList){
										$scope.fixturesList[idx].date = new Date($scope.fixturesList[idx].date);
									}
									
									resolveExistingUnusedTeams($scope.fixturesList);
								})($scope, entityService, $routeParams,
								$rootScope, $location);

						makeListFn("team")($scope, entityService);

						$scope
								.$watch(
										"teams",
										function(teams) {
											$scope
													.$watch(
															"currentDate",
															function(date) {

																if (date && $scope.usedTeamsControl) {
																	$scope.usedTeamsControl.setDate(date);
																}
															});
											$scope.currentDate = new Date();
											$scope.fixture = {};
										});
					
						function resolveCurrentFixtures(date) {

							if (!$scope.fixtures || ($scope.fixtures
									&& $scope.fixtures.date != date)) {

								var fixtures = null;

								for (index in $scope.fixturesList) {
									if ($scope.fixturesList[index].date
											.toDateString() == date
											.toDateString()) {
										fixtures = $scope.fixturesList[index];
										break;
									}
								}
								if (!fixtures) {
									fixtures = {
										date : date,
										fixtures : []
									};
									$scope.fixturesList.push(fixtures);
								}

								$scope.fixtures = fixtures;
								

							}
						}
						
						$scope.advanceDate = function() {
							$scope.currentDate = new Date($scope.currentDate
									.getTime()
									+ (7 * 60 * 60 * 24 * 1000));
						};
						$scope.addFixture = function(fixture) {
							
							resolveCurrentFixtures($scope.currentDate); 
							
							fixture.date = $scope.currentDate;
							$scope.fixtures.fixtures.push(fixture);
							$scope.fixture = {};

							$scope.usedTeamsControl.add(fixture.home, fixture.away);
						};
						$scope.removeFixture = function(fixture) {
							var date = new Date(fixture.date).toDateString();
							for(idx in $scope.fixturesList){
								var fixtures = $scope.fixturesList[idx];
								if(date == fixtures.date.toDateString()){
									for(idx2 in fixtures.fixtures){
										var listFixture = $scope.fixturesList[idx].fixtures[idx2];
										if(fixture.home.id == listFixture.home.id && fixture.away.id == listFixture.away.id){
											fixtures.fixtures.splice(idx2,1);
											$scope.usedTeamsControl.remove(fixtures.date, fixture.home, fixture.away);
										}
									}
								}
							}
						};

						$scope.updateFixtures = function(fixtures) {
							$scope.masterSeason.competitions[compType].fixtures = fixtures;
							$location.url("seasons/" + seasonId + "/"
									+ compType);
						};
					}));

	maintainApp.controller('GlobalDetailCtrl', getCommonParams(function($scope,
			entityService, $routeParams, $rootScope, $location) {
		makeUpdateFnWithCallback("global", function(ret, $location) {
			$location.url("/maintain.html");
		})($scope, entityService, $routeParams, $rootScope, $location);
		makeListFn("season", {
			bindName : "currentSeason",
			entityName : "global"
		})($scope, entityService);

	}));
})();