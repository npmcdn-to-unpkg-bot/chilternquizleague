(function() {

	function nowOrBefore(input) {
		function makeDateString(date) {
			return date.toISOString();
		}

		var now = makeDateString(new Date());
		var ret = [];
		for (idx in input) {
			input[idx].date = new Date(input[idx].date);
			if (makeDateString(input[idx].date) <= now) {
				ret.push(input[idx]);
			}
		}

		return ret.sort(function(item1, item2) {
			return item1.date.getTime() - item2.date.getTime();
		});
	}

	mainApp.controller('ResultsSubmitController', [ '$scope', 'viewService',
			'$location', function($scope, viewService, $location) {

				$scope.fixturesForEmail = function(email) {

					viewService.view("fixtures-for-email", {
						email : email,
						seasonId : $scope.global.currentSeasonId
					}, function(fixtures) {

						fixtures = nowOrBefore(fixtures);

						$scope.fixture = null;

						for (idx in fixtures) {

							var fixture = fixtures.pop();

							$scope.fixture = {
								date : fixture.date,
								home : fixture.home,
								away : fixture.away
							};
							$scope.leagueResult = {
								fixture : $scope.fixture,
								reports : [ {
									text : ""
								} ]
							};
							$scope.beerResult = {
								fixture : $scope.fixture
							};
							break;
						}

					});
				};

				$scope.submitResults = function() {

					viewService.post("submit-results", [ {
						result : $scope.leagueResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType : "LEAGUE"

					}, {
						result : $scope.beerResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType : "BEER_LEG"

					} ]);

				};
			} ]);

	mainApp.controller("AllResultsController", [ '$scope', 'viewService',
			'$location', function($scope, viewService, $location) {

				$scope.setSeason = function(season) {

					var results = [];
					if (season) {

						for (idx in season.competitions) {

						
							results = results.concat(season.competitions[idx].results);

						}
					}
					$scope.allResults = results;

					$scope.season = season;

				};

				viewService.list("seasons", function(seasons) {
					$scope.seasons = seasons;

					for (idx in seasons) {

						if (seasons[idx].id == $scope.global.currentSeasonId) {
							$scope.setSeason(seasons[idx]);
							return;
						}
						
						$scope.setSeason(seasons ? seasons[0]:null);
					}

				});

			} ]);

})();
