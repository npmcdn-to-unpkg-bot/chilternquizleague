function teamCompetitionControllerFactory(name){
	
	var ucName = name.toUpperCase();
	name = name.toLowerCase();
	var competitionName = name + "Competition";
	var masterName = "master" + competitionName.charAt(0).toUpperCase() + competitionName.substr(1);
	var addName = "add" + competitionName.charAt(0).toUpperCase() + competitionName.substr(1);
	
	return function($scope, entityService, $routeParams,
			$rootScope, $location) {

		var seasonId = $routeParams.seasonId;
		$scope.seasonId = seasonId;
		makeUpdateFnWithCallback(
				"season",
				null,
				function(ret) {
					if (!ret.competitions[ucName]) {

						$scope
								.$watch(
										competitionName,
										function(comp) {

											$scope.masterSeason.competitions[ucName] = comp;
										});
						makeUpdateFnWithCallback(
								competitionName)($scope,
								entityService, $routeParams,
								$rootScope, $location);
					} else {
						$scope[masterName] = ret.competitions[ucName];
						$scope[competitionName] = angular
								.copy(ret.competitions[ucName]);
					}
				})($scope, entityService, $routeParams, $rootScope,
				$location);

		$scope[addName] = function(competition) {
			$scope.masterSeason.competitions[ucName] = $scope[competitionName];
			$location.url("/seasons/" + seasonId);
		};

	};
	
	
}


maintainApp
		.controller(
				'LeagueCompCtrl',
				getCommonParams(teamCompetitionControllerFactory("LEAGUE")));

maintainApp
.controller(
		'BeerCompCtrl',
		getCommonParams(teamCompetitionControllerFactory("BEER")));

maintainApp
.controller(
		'CupCompCtrl',
		getCommonParams(teamCompetitionControllerFactory("CUP")));

maintainApp
.controller(
		'PlateCompCtrl',
		getCommonParams(teamCompetitionControllerFactory("PLATE")));

function UsedTeamsControl(teams, $scope) {

	this.teams = teams;
	this.dateMap = {};
	this.$scope = $scope;
	this.makeKey = function(date) {
		date = new Date(date);
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
				getCommonParams(function($scope, entityService, $routeParams,
						$rootScope, $location) {

					var compType = $routeParams.compType;
					var seasonId = $routeParams.seasonId;

					
					$scope.$watch("currentDate", function(date){
						if(date && $scope.season){
							
							date = new Date(date.getTime()); 
							var newDate = makeDateWithTime(
										date,
										$scope.season.competitions[compType].startTime);
							 
							 if(date.toUTCString() != newDate.toUTCString()){
								 $scope.currentDate = newDate;
							 }
						}
						
					});
					
					$scope.setCurrentDate = function(date) {
						$scope.currentDate = new Date(date);
					};

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

								$scope
										.setCurrentDate($scope.fixturesList[0] ? makeDateWithTime(
												$scope.fixturesList[0].start,
												season.competitions[compType].startTime)
												: makeDateWithTime(
														new Date(),
														season.competitions[compType].startTime));
								resolveExistingUnusedTeams($scope.fixturesList);
							})($scope, entityService, $routeParams, $rootScope,
							$location);

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

					function makeDateWithTime(baseDate, timeString) {

						var date = new Date(baseDate);

						return new Date(date.getFullYear(), date.getMonth(),
								date.getDate(), timeString.substring(0, 2),
								timeString.substring(3));

					}

					function resolveCurrentFixtures(date) {

						var time = new Date(date).getTime();

						if (!$scope.fixtures
								|| ($scope.fixtures && $scope.fixtures.start != time)) {

							var fixtures = null;

							for (index in $scope.fixturesList) {
								if ($scope.fixturesList[index].start == time) {
									fixtures = $scope.fixturesList[index];
									break;
								}
							}
							if (!fixtures) {
								fixtures = {
									fixtures : [],
									description : $scope.season.competitions[compType].description,
									start : makeDateWithTime(
											date,
											$scope.season.competitions[compType].startTime)
											.getTime(),

									end : makeDateWithTime(
											date,
											$scope.season.competitions[compType].endTime)
											.getTime(),
									competitionType : compType
								};
								$scope.fixturesList.push(fixtures);
							}

							$scope.fixturesList = $scope.fixturesList
									.sort(function(fxs1, fxs2) {
										return fxs1.start - fxs2.start;
									});
							$scope.fixtures = fixtures;

						}
					}

					$scope.advanceDate = function() {
						$scope.setCurrentDate(new Date($scope.currentDate
								.getTime()
								+ (7 * 60 * 60 * 24 * 1000)));
					};

					$scope.addFixture = function(fixture) {

						resolveCurrentFixtures($scope.currentDate);

						fixture.start = $scope.fixtures.start;
						fixture.end = $scope.fixtures.end;
						$scope.fixtures.fixtures.push(fixture);
						$scope.fixture = {};

						$scope.usedTeamsControl.add(fixture.home, fixture.away);
					};
					$scope.removeFixture = function(fixture) {
						var date = new Date(fixture.start).getTime();
						for (idx in $scope.fixturesList) {
							var fixtures = $scope.fixturesList[idx];
							if (date == fixtures.start) {
								for (idx2 in fixtures.fixtures) {
									var listFixture = $scope.fixturesList[idx].fixtures[idx2];
									if (fixture.home.id == listFixture.home.id
											&& fixture.away.id == listFixture.away.id) {
										fixtures.fixtures.splice(idx2, 1);
										$scope.usedTeamsControl.remove(
												fixtures.start, fixture.home,
												fixture.away);
									}
								}
							}
						}
					};

					$scope.updateFixtures = function(fixtures) {
						$scope.masterSeason.competitions[compType].fixtures = fixtures;
						$location.url("seasons/" + seasonId + "/" + compType);
					};

					// $scope.setCurrentDate(new Date());
				}));

maintainApp.controller('LeagueTablesCtrl', getCommonParams(function($scope,
		entityService, $routeParams, $rootScope, $location) {

	var compType = $scope.compType = $routeParams.compType;
	var seasonId = $scope.seasonId = $routeParams.seasonId;

	makeUpdateFnWithCallback(
			"season",
			null,
			function(ret) {

				$scope.leagueTables = angular
						.copy(ret.competitions[compType].leagueTables);
			})($scope, entityService, $routeParams, $rootScope, $location);

	makeListFn("team", {
		bindName : "currentSeason",
		entityName : "global"
	})($scope, entityService);

	$scope.addTable = function(leagueTables) {

		entityService.load("leagueTable", "new", function(ret) {
			leagueTables.push(angular.copy(ret));
		});

	};

	$scope.addRow = function(table) {
		entityService.load("leagueTableRow", "new", function(ret) {
			table.rows.push(angular.copy(ret));
		});
	};

	$scope.update = function(leagueTables) {
		$scope.masterSeason.competitions[compType].leagueTables = leagueTables;
		$location.url("seasons/" + seasonId + "/" + compType);
	};

	function setTeams() {

		if ($scope.leagueTables && $scope.teams) {
			for (idx in $scope.leagueTables) {

				for (idx2 in $scope.leagueTables[idx].rows) {
					syncToListItem($scope, $scope.leagueTables[idx].rows[idx2],
							$scope.teams, "team");
				}
			}
		}
	}

	$scope.$watch("teams", setTeams);
	$scope.$watch("leagueTables", setTeams);

}));