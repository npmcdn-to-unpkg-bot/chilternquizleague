(function() {
	var teamApp = angular.module('teamApp', [ "ngRoute" ]).factory('viewService',
			VIEW_SERVICE_DEFN);

	teamApp.filter('afterNow', function() {
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

	teamApp
			.controller(
					'TeamsController',
					[
							'$scope',
							'$http',
							'$interval',
							'$routeParams',
							'viewService',
							'$location',
							function($scope, $http, $interval, $routeParams, viewService,
									$location) {

								var promise = null;
								var teamId = $location.path().substr(1);
								$scope.$watch("team", function(team) {

									if (team) {
										$location.path("/" + team.id);
										if ($scope.global && !team.extras) {
											viewService.view("team-extras", {
												seasonId : $scope.global.currentSeasonId,
												teamId : team.id
											}, function(extras) {

												for (idx in extras.fixtures) {

													extras.fixtures[idx].date = new Date(fixtures[idx].date);
												}

												team.extras = extras;
											});
										}
									}
								});
								viewService.view("globaldata", {}, function(globalData) {
									$scope.global = globalData;
									$scope.leagueName = globalData.leagueName;
								});

								$scope.setTeam = function(team) {

									$interval.cancel(promise);
									$scope.team = team;
								};

								viewService
										.list(
												"teams",
												function(teams) {
													$scope.teams = teams.sort(function(team1, team2) {
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

							} ]);

})();