(function() {

	function nowOrBefore(input) {
		function makeDateString(date) {
			return new Date(date).toISOString();
		}

		var now = makeDateString(new Date());
		var ret = [];
		for (idx in input) {
			if (makeDateString(input[idx].start) <= now) {
				ret.push(input[idx]);
			}
		}

		return ret.sort(function(item1, item2) {
			return item1.start - item2.start;
		});
	}

	mainApp.controller('ResultsSubmitController', [ '$scope', 'viewService',
			'$location', function($scope, viewService, $location) {

				$scope.fixturesForEmail = function(email) {

					viewService.view("fixtures-for-email", {
						email : email,
						seasonId : $scope.global.currentSeasonId,
						isArray : true
					}, function(fixtures) {

						fixtures = nowOrBefore(fixtures);

						$scope.fixture = null;

						for (idx in fixtures) {

							var fixture = fixtures[idx].fixtures.pop();

							$scope.fixture = fixture;
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

				$scope.setCurrentResults = function(results) {
					$scope.currentResults = results;
				};

				$scope.setSeason = function(season) {

					var results = [];
					if (season) {

						for (idx in season.competitions) {

							results = results.concat(season.competitions[idx].results);

						}
					}
					$scope.allResults = results;
					results.length > 0 ? $scope.setCurrentResults(results[0]) : null;

					$scope.season = season;

				};

				viewService.list("seasons", function(seasons) {
					$scope.seasons = seasons;

					for (idx in seasons) {

						if (seasons[idx].id == $scope.global.currentSeasonId) {
							$scope.setSeason(seasons[idx]);
							return;
						}

						$scope.setSeason(seasons ? seasons[0] : null);
					}

				});

			} ]);

})();
