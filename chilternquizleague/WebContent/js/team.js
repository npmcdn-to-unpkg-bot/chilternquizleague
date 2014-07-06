mainApp.filter('afterNow', function() {
	return function(input) {
		function makeDateString(date) {
			return date.toISOString();
		}

		var now = makeDateString(new Date());
		var ret = [];
		for (idx in input) {
			if (makeDateString(input[idx].date) >= now) {
				ret.push(input[idx]);
			}
		}

		return ret;
	};
});

mainApp
		.controller(
				'TeamsController',
				[
						'$scope',
						'$http',
						'$interval',
						'viewService',
						'$location',
						function($scope, $http, $interval, viewService, $location) {

							var promise = null;
							var teamId = $location.path().substr(1);

							$scope.$watch("global", function(global) {

								if (global) {
									$scope.$watch("team", function(team) {

										if (team) {
											$location.path("/" + team.id);
											if (!team.extras) {
												viewService.view("team-extras", {
													seasonId : global.currentSeasonId,
													teamId : team.id
												}, function(extras) {

													for (idx in extras.fixtures) {

														extras.fixtures[idx].date = new Date(
																extras.fixtures[idx].date);
													}

													team.extras = extras;
												});
											}
										}
									});

								}
							});

							$scope.setTeam = function(team) {

								$interval.cancel(promise);
								$scope.team = team;
							};

							viewService
									.list(
											"teams",
											function(teams) {
												$scope.teams = teams
														.sort(function(team1, team2) {
															return team1.shortName
																	.localeCompare(team2.shortName);
														});
												if (teamId) {
													for (idx in teams) {
														if (teams[idx].id == teamId) {
															$scope.team = teams[idx];
														}
													}
												} else {
													var teamIndex = 0;
													$scope.team = teams[0];
													promise = $interval(
															function() {

																$scope.team = $scope.teams[teamIndex = teamIndex >= (teams.length - 1) ? 0
																		: (teamIndex + 1)];
															}, 5000);
												}

											});
							$scope.makeICal = function(team) {

								var contents = generateICalContent(team.extras.fixtures);

								var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
										+ ".ics";

								var blob = new Blob([ contents ], {
									type : "text/calendar;charset=utf-8"
								});

								saveAs(blob, filename);

							};

						} ]);
