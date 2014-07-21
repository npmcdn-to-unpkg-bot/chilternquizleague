(function() {

	var maintainApp = angular.module('maintainApp', [ "ngRoute", "ngAnimate" ])
			.factory('entityService', ENTITY_SERVICE_DEFN);
	;

	maintainApp.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/venues', {
			templateUrl : 'maintain/venue/venue-list.html',
			controller : 'VenueListCtrl'
		}).when('/venues/:venueId', {
			templateUrl : 'maintain/venue/venue-detail.html',
			controller : 'VenueDetailCtrl'
		}).when('/teams', {
			templateUrl : 'maintain/team/team-list.html',
			controller : 'TeamListCtrl'
		}).when('/teams/:teamId', {
			templateUrl : 'maintain/team/team-detail.html',
			controller : 'TeamDetailCtrl'
		}).when('/users', {
			templateUrl : 'maintain/user/user-list.html',
			controller : 'UserListCtrl'
		}).when('/users/:userId', {
			templateUrl : 'maintain/user/user-detail.html',
			controller : 'UserDetailCtrl'
		}).when('/seasons', {
			templateUrl : 'maintain/season/season-list.html',
			controller : 'SeasonListCtrl'
		}).when('/seasons/:seasonId', {
			templateUrl : 'maintain/season/season-detail.html',
			controller : 'SeasonDetailCtrl'
		}).when('/seasons/:seasonId/LEAGUE', {
			templateUrl : 'maintain/competition/league-detail.html',
			controller : 'LeagueCompCtrl'
		}).when('/seasons/:seasonId/:compType/fixtures', {
			templateUrl : 'maintain/competition/fixtures.html',
			controller : 'FixturesCtrl'
		}).when('/seasons/:seasonId/LEAGUE/results', {
			templateUrl : 'maintain/competition/results.html',
			controller : 'ResultsCtrl'
		}).when('/seasons/:seasonId/:compType/tables', {
			templateUrl : 'maintain/competition/tables.html',
			controller : 'LeagueTablesCtrl'
		}).when('/global/current', {
			templateUrl : 'maintain/global/global-detail.html',
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

	function makeListFn(typeName, config) {

		return function($scope, entityService) {
			var collectionName = typeName + "s";
			config = config ? config : {};

			if (config.entityName && config.bindName) {
				collectionName = (config.collName ? config.collName
						: collectionName);
				$scope.$watch(config.entityName, function(entity) {
					syncToListItem($scope, entity, $scope[collectionName],
							config.bindName);
				});
				$scope.$watch(collectionName, function(collection) {
					syncToListItem($scope, $scope[config.entityName],
							collection, config.bindName);
				});

			}
			entityService.loadList(typeName, function(ret) {
				$scope[collectionName] = config.sort ? ret.sort(config.sort)
						: ret;
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

	maintainApp.controller('VenueListCtrl', getCommonParams(makeListFn("venue",
			{
				sort : function(venue1, venue2) {
					return venue1.name.localeCompare(venue2.name);
				}
			})));

	maintainApp.controller('VenueDetailCtrl',
			getCommonParams(makeUpdateFn("venue")));

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

	});

	maintainApp.controller('SeasonDetailCtrl', seasonBody);

	maintainApp.controller('LeagueCompCtrl', getCommonParams(function($scope,
			entityService, $routeParams, $rootScope, $location) {

		var seasonId = $routeParams.seasonId;
		$scope.seasonId = seasonId;
		makeUpdateFnWithCallback(
				"season",
				null,
				function(ret) {
					if (!ret.competitions.LEAGUE) {
						$scope.$watch("leagueCompetition", function(comp) {

							$scope.masterSeason.competitions.LEAGUE = comp;
						});
						makeUpdateFnWithCallback("leagueCompetition")($scope,
								entityService, $routeParams, $rootScope,
								$location);
					} else {
						$scope.masterLeagueCompetion = ret.competitions.LEAGUE;
						$scope.leagueCompetition = angular
								.copy(ret.competitions.LEAGUE);
					}
				})($scope, entityService, $routeParams, $rootScope, $location);

		$scope.addLeagueCompetition = function(competition) {
			$scope.masterSeason.competitions.LEAGUE = $scope.leagueCompetition;
			$location.url("/seasons/" + seasonId);
		};

	}));

	function UsedTeamsControl(teams, $scope) {

		this.teams = teams;
		this.dateMap = {};
		this.$scope = $scope;
		this.makeKey = function(date) {
			return "D" + date.getYear() + date.getMonth() + date.getDate();
		};
		this.currentKey = this.setDate(new Date());

	}

	UsedTeamsControl.prototype.setDate = function(date) {
		this.currentKey = this.makeKey(date);

		this.$scope.unusedTeams = this.dateMap[this.currentKey] = this.dateMap
				.hasOwnProperty(this.currentKey) ? this.dateMap[this.currentKey]
				: angular.copy(this.teams);
	};

	UsedTeamsControl.prototype.getUnused = function() {

		return this.$scope.unusedTeams;
	};

	UsedTeamsControl.prototype.add = function(team1, team2) {

		var teams = this.getUnused();

		function removeTeam(team) {
			removeFromListById(teams, team);
		}

		removeTeam(team1);
		team2 ? removeTeam(team2) : null;
	};

	UsedTeamsControl.prototype.remove = function(date, team1, team2) {
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

						$scope.currentDate = new Date();
						function resolveExistingUnusedTeams(fixturesList) {

							if (fixturesList && $scope.teams) {

								var utc = $scope.usedTeamsControl = new UsedTeamsControl(
										$scope.teams, $scope);

								for (idx in fixturesList) {

									utc.setDate(fixturesList[idx].start);

									for (idx2 in fixturesList[idx].fixtures) {
										var fixture = fixturesList[idx].fixtures[idx2];

										utc.add(fixture.home, fixture.away);
									}
									;
								}

								var date = $scope.currentDate ? $scope.currentDate
										: new Date();
								utc.setDate(date);
							}

						}

						makeUpdateFnWithCallback(
								"season",
								null,
								function(season) {
									$scope.fixturesList = season.competitions[compType].fixtures;
									for (idx in $scope.fixturesList) {
										$scope.fixturesList[idx].date = new Date(
												$scope.fixturesList[idx].date);
									}
									$scope.currentDate = $scope.fixturesList[0] ? $scope.fixturesList[0].date
											: $scope.currentDate;
									resolveExistingUnusedTeams($scope.fixturesList);
								})($scope, entityService, $routeParams,
								$rootScope, $location);

						makeListFn(
								"team",
								{
									sort : function(team1, team2) {
										return team1.shortName
												.localeCompare(team2.shortName);
									}
								})($scope, entityService);

						$scope.$watch("teams", function(teams) {
							resolveExistingUnusedTeams($scope.fixturesList);
							$scope.$watch("currentDate", function(date) {

								if (date && $scope.usedTeamsControl) {
									$scope.usedTeamsControl.setDate(date);
								}
							});
							$scope.fixture = {};
						});

						function resolveCurrentFixtures(date) {

							if (!$scope.fixtures
									|| ($scope.fixtures && $scope.fixtures.start != date)) {

								var fixtures = null;

								for (index in $scope.fixturesList) {
									if ($scope.fixturesList[index].start
											.toDateString() == date
											.toDateString()) {
										fixtures = $scope.fixturesList[index];
										break;
									}
								}
								if (!fixtures) {
									fixtures = {
										fixtures : [],
										description : $scope.season.competitions[compType].description,
										start : new Date(date.getFullYear(), date.getMonth(), date.getUTCDate(),$scope.season.competitions[compType].startTime.substring(0,2),$scope.season.competitions[compType].startTime.substring(3)), 

										end : new Date(date.getFullYear(), date.getMonth(), date.getUTCDate(),$scope.season.competitions[compType].endTime.substring(0,2),$scope.season.competitions[compType].endTime.substring(3)) 
									};
									$scope.fixturesList.push(fixtures);
								}

								$scope.fixturesList = $scope.fixturesList
										.sort(function(fxs1, fxs2) {
											return fxs1.start.getTime()
													- fxs2.start.getTime();
										});
								$scope.fixtures = fixtures;

							}
						}

						$scope.advanceDate = function() {
							$scope.currentDate = new Date($scope.currentDate
									.getTime()
									+ (7 * 60 * 60 * 24 * 1000));
						};

						$scope.setCurrentDate = function(date) {
							$scope.currentDate = date;
						};

						$scope.addFixture = function(fixture) {

							resolveCurrentFixtures($scope.currentDate);

							fixture.start = $scope.fixtures.start;
							fixture.end = $scope.fixtures.end;
							$scope.fixtures.fixtures.push(fixture);
							$scope.fixture = {};

							$scope.usedTeamsControl.add(fixture.home,
									fixture.away);
						};
						$scope.removeFixture = function(fixture) {
							var date = new Date(fixture.start).toDateString();
							for (idx in $scope.fixturesList) {
								var fixtures = $scope.fixturesList[idx];
								if (date == fixtures.start.toDateString()) {
									for (idx2 in fixtures.fixtures) {
										var listFixture = $scope.fixturesList[idx].fixtures[idx2];
										if (fixture.home.id == listFixture.home.id
												&& fixture.away.id == listFixture.away.id) {
											fixtures.fixtures.splice(idx2, 1);
											$scope.usedTeamsControl.remove(
													fixtures.start,
													fixture.home, fixture.away);
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

	maintainApp
			.controller(
					'LeagueTablesCtrl',
					getCommonParams(function($scope, entityService,
							$routeParams, $rootScope, $location) {

						var compType = $scope.compType = $routeParams.compType;
						var seasonId = $scope.seasonId = $routeParams.seasonId;

						makeUpdateFnWithCallback(
								"season",
								null,
								function(ret) {

									$scope.leagueTables = angular
											.copy(ret.competitions[compType].leagueTables);
								})($scope, entityService, $routeParams,
								$rootScope, $location);

						makeListFn("team", {
							bindName : "currentSeason",
							entityName : "global"
						})($scope, entityService);

						$scope.addTable = function(leagueTables) {

							entityService.load("leagueTable", "new", function(
									ret) {
								leagueTables.push(angular.copy(ret));
							});

						};

						$scope.addRow = function(table) {
							entityService.load("leagueTableRow", "new",
									function(ret) {
										table.rows.push(angular.copy(ret));
									});
						};

						$scope.update = function(leagueTables) {
							$scope.masterSeason.competitions[compType].leagueTables = leagueTables;
							$location.url("seasons/" + seasonId + "/"
									+ compType);
						};

						function setTeams() {

							if ($scope.leagueTables && $scope.teams) {
								for (idx in $scope.leagueTables) {

									for (idx2 in $scope.leagueTables[idx].rows) {
										syncToListItem(
												$scope,
												$scope.leagueTables[idx].rows[idx2],
												$scope.teams, "team");
									}
								}
							}
						}

						$scope.$watch("teams", setTeams);
						$scope.$watch("leagueTables", setTeams);

					}));

})();