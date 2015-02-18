function teamCompetitionControllerFactory() {

	return function($scope, entityService, $routeParams, $rootScope, $location) {

		var ucName = $routeParams.compType.toUpperCase();
		name = $routeParams.compType.toLowerCase();
		var competitionName = "competition";
		var masterName = "masterCompetition";
		var addName = "addCompetition";
		$scope[competitionName] = {}
		$scope[masterName] = {}

		$scope.tinymceOptions = tinymceOptions;

		$scope.$watch("season", function(season) {

			if (season) {
				
				var competition = season.competitions[ucName]
				
				if(!competition){
					entityService.load(ucName,"new", function(competition){
						$scope[masterName] = competition;
						$scope[competitionName] = angular.copy(competition);
					})
				}
				
				$scope[masterName] = competition;
				$scope[competitionName] = angular.copy(competition);

				$scope[addName] = function(competition) {
					season.competitions[ucName] = $scope[competitionName];
					$location.url("/maintain/seasons/" + season.id);
				};

				$scope.resetCompetition = function() {
					$scope[competitionName] = angular.copy($scope[masterName])
				}
				$scope.subFilter = function(value, index) {
					return value.comp.subsidiary

				}
			}
		})

	};

}

maintainApp.controller('CompCtrl',
		getCommonParams(teamCompetitionControllerFactory()));

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

					$scope.fixturesList = []
					$scope.setCurrentFixtures = function(fixtures){
						$scope.fixtures = fixtures
						$scope.usedTeamsControl.setDate(fixtures.start)
					}

					function resolveExistingUnusedTeams(fixturesList) {

						if (fixturesList && $scope.teams) {

							var utc = $scope.usedTeamsControl = new UsedTeamsControl(
									filter($scope.teams, function(team) {
										return !team.retired
									}), $scope);

							for (idx in fixturesList) {

								utc.setDate(fixturesList[idx].start);

								for (idx2 in fixturesList[idx].fixtures) {
									var fixture = fixturesList[idx].fixtures[idx2];

									utc.add(fixture.home, fixture.away);
								}
								
							}

						}

					}
					
					function initialiseUI(){
						if($scope.fixturesList.length > 0){
							
							resolveExistingUnusedTeams($scope.fixturesList)
							
							$scope.setCurrentFixtures($scope.fixturesList[0])
						}
						else{
							$scope.addFixtures()
						}
					}

					$scope
							.$watch(
									"competition",
									function(competition) {

										if (competition && competition.id) {

											for(idx in competition.fixtures){
												var f = competition.fixtures[idx]
												f.start = new Date(f.start)
												f.end = new Date(f.end)
											}
											
											$scope.masterFixtures = competition.fixtures
											$scope.fixturesList = filter(
													angular
															.copy(competition.fixtures),
													function(val) {
														return val != null
													})

											initialiseUI()		
										}

									});

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
					});

					function makeDateWithTime(baseDate, timeString) {

						var date = new Date(baseDate);

						return new Date(date.getFullYear(), date.getMonth(),
								date.getDate(), timeString.substring(0, 2),
								timeString.substring(3));

					}


					$scope.addFixture = function(fixture) {

						fixture.start = $scope.fixtures.start;
						fixture.end = $scope.fixtures.end;
						$scope.fixtures.fixtures.push(fixture);
						$scope.fixture = {};

						$scope.usedTeamsControl.add(fixture.home, fixture.away);
					};
					$scope.removeFixture = function(fixture) {
						
						function compareTeams(t1,t2){
							var t1Id = t1 ? t1.id : ""
							var t2Id = t2 ? t2.id : ""
							
							return t1Id == t2Id
						}
						
						for(idx in $scope.fixtures.fixtures){
							var fix = $scope.fixtures.fixtures[idx]
							if(compareTeams(fix.home,fixture.home) && compareTeams(fix.away, fixture.away)){	
								 $scope.fixtures.fixtures.splice(idx, 1)
								 $scope.usedTeamsControl.remove(fixture.start,fixture.home,fixture.away)
								 break
							}
							
						}
					};
					
					$scope.addFixtures= function(){
						var date = new Date()

						if($scope.fixturesList.length > 0){
							
							var fixturesList = angular.copy($scope.fixturesList)
							fixturesList.sort(function(a,b){return a.start > b.start})
							
							date = new Date(new Date(fixturesList.pop().start)
									.getTime()
									+ (7 * 60 * 60 * 24 * 1000))
						}
						
						var start = makeDateWithTime(date, $scope.competition.startTime)
						var end = makeDateWithTime(date, $scope.competition.endTime)
						
						entityService.load("Fixtures","new" , function(f){
							
							var fixtures = angular.copy(f)
							
							fixtures.competitionType = $scope.competition.type
							fixtures.start = start
							fixtures.end = end
							fixtures.description = $scope.competition.description
							
							$scope.fixturesList.push(fixtures)
							$scope.setCurrentFixtures(fixtures)
							
						})
						

						
					}

					$scope.updateFixtures = function(fixtures) {
						$scope.competition.fixtures = fixtures;
						$location.url("/maintain/seasons/" + $scope.season.id
								+ "/competition/" + $scope.competition.type);
					};

					$scope.resetFixtures = function() {
						$scope.fixturesList = angular
								.copy($scope.masterFixtures)
						initialiseUI()
					}

				}));

maintainApp.controller('LeagueTablesCtrl', getCommonParams(function($scope,
		entityService, $routeParams, $rootScope, $location) {



	$scope.$watch("competition", function(competition){
		
		$scope.masterLeagueTables = competition.leagueTables
		$scope.leagueTables = angular.copy(competition.leagueTables);
	})

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

	$scope.updateTables = function(leagueTables) {
		$scope.competition.leagueTables = leagueTables;
		$location.url("/maintain/seasons/" + $scope.season.id + "/competition/" + $scope.competition.type);
	};
	
	$scope.resetTables = function(){
		$scope.leagueTables = angular.copy($scope.masterLeagueTables)
	}

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

maintainApp.controller('ResultsCtrl',
		getCommonParams(function($scope, entityService, $routeParams,
				$rootScope, $location) {

			$scope.resultsList = []

			$scope.setCurrentResults = function(results){$scope.results = results}
			
			$scope.filterEmptyReports = function(report){
				return report.text.text != null && report.text.text != ""
			}
			
			$scope.editReports = function(result){
				$scope.currentResult = result == $scope.currentResult ? null : result
				
			}
			
			$scope.$watch("competition", function(competition) {

				for(idx in competition.results){
					var f = competition.results[idx]
					f.date = new Date(f.date)
				}
				
				$scope.masterResults = competition.results
				$scope.resultsList = filter(angular.copy(competition.results),
						function(val) {
							return val != null
						});
				
				if($scope.resultsList.length > 0){
					$scope.results = $scope.resultsList[0]
				}

			})

			function makeDateWithTime(baseDate, timeString) {

				var date = new Date(baseDate);

				return new Date(date.getFullYear(), date.getMonth(), date
						.getDate(), timeString.substring(0, 2), timeString
						.substring(3));

			}

			$scope.updateResults = function(results) {
				$scope.competition.results = results;
				$location.url("/maintain/seasons/" + $scope.season.id
						+ "/competition/" + $scope.competition.type);
			};
			
			$scope.resetResults = function(){
				$scope.resultsList = angular.copy($scope.masterResults)
			}

			// $scope.setCurrentDate(new Date());
		}));