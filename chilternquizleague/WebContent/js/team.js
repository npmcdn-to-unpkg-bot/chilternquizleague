(function() {
	var teamApp = angular.module('teamApp', [ "ngRoute" ]).factory(
			'viewService', VIEW_SERVICE_DEFN);

	teamApp.filter('afterNow', function() {
		return function(input) {
			function makeDateString(date){
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

	var promise;

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
							function($scope, $http, $interval, $routeParams,
									viewService, $location) {

								var teamId = $location.path().substr(1);
								$scope.$watch("team", function(team){
									
									//$location.path(team.id);
									if($scope.global){
									viewService.view("team-fixtures", {
										seasonId : $scope.global.currentSeasonId,
										teamId : team.id
									}, function(fixtures) {

										for (idx in fixtures) {

											fixtures[idx].date = new Date(fixtures[idx].date);
										}

										$scope.fixtures = fixtures;
									});}

							});
								viewService.view("globaldata",{}, function(
										globalData) {
									$scope.global = globalData;
									$scope.leagueName = globalData.leagueName;
								});

								$scope.setTeam = function(team){
									
									$interval.cancel(promise);
									$scope.team = team;
								};
								
								viewService
										.list(
												"teams",
												function(teams) {
													$scope.teams = teams;
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

	teamApp.controller('TeamController', [
			'$scope',
			'$http',
			'$interval',
			'$routeParams',
			'viewService',
			'$location',
			function($scope, $http, $interval, $routeParams, viewService,
					$location) {

				!$routeParams.cycle && promise ? $interval.cancel(promise)
						: null;

				var teamId = $routeParams.teamId;

				
				$scope.$on("team", function(team){
					

					viewService.view("team-fixtures", {
						seasonId : globalData.currentSeasonId,
						teamId : team.id
					}, function(fixtures) {

						for (idx in fixtures) {

							fixtures[idx].date = new Date(fixtures[idx].date);
						}

						$scope.fixtures = fixtures;
					});

			});
				
				viewService.load("team", teamId, function(team) {
					$scope.team = team;
				});

				viewService.view("globaldata", function(globalData) {

					$scope.global = globalData;

					viewService.view("team-fixtures", {
						seasonId : globalData.currentSeasonId,
						teamId : teamId
					}, function(fixtures) {

						for (idx in fixtures) {

							fixtures[idx].date = new Date(fixtures[idx].date);
						}

						$scope.fixtures = fixtures;
					});
				});

			} ]);
})();