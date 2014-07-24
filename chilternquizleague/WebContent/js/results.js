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

						//loop once only
						for (idx in fixtures) {

							var fixture = fixtures[idx].fixtures.pop();
							$scope.fixtures = fixtures[idx];
							$scope.fixture = fixture;
							$scope.mainResult = {
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
						result : $scope.mainResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType :  $scope.fixtures.competitionType

					}, {
						result : $scope.beerResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType : "BEER_LEG"

					} ]);

					$scope.fixture = null;
				};
			} ]);

	mainApp.controller("AllResultsController", [
			'$scope',
			'viewService',
			'$location',
			function($scope, viewService, $location) {

				$scope.setCurrentResults = function(results) {
					$scope.currentResults = results;
				};

				$scope.setSeason = function(season) {

					$scope.allResults = season ? viewService.view("all-results",{id:season.id,isArray:true},function(results){
						
						results.sort(function(results1,results2){return results2.date -results1.date;});
					}): [];
					
					$scope.season = season;
					
					$scope.$watch("allResults.length",function(length){
						$scope.setCurrentResults($scope.allResults && $scope.allResults.length > 0 ? $scope.allResults[0] : null);});

				};

				viewService.list("season-views", function(seasons) {
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

	mainApp.controller("TeamResultsController", [ '$scope','$interval' ,'viewService',
			'$location',  cyclingListControllerFactory("team", function(team1, team2) {
				return team1.shortName.localeCompare(team2.shortName);
			}, function($scope, $interval,viewService, $location) {
				
				$scope.setSeason = function(season){$scope.season = season;};
				
				$scope.$watch("global.currentSeasonId", function(currentSeasonId){
					viewService.list("season-views", function(seasons) {
				
					$scope.seasons = seasons;

					for (idx in seasons) {

						if (seasons[idx].id == $scope.global.currentSeasonId) {
							$scope.setSeason(seasons[idx]);
							return;
						}

						$scope.setSeason(seasons ? seasons[0] : null);
					}});});

				function teamExtras(){
					
					if($scope.team && $scope.season){
						$scope.team.extras = viewService.view("team-extras", {
							seasonId : $scope.season.id,
							teamId : $scope.team.id
						});
						
					}
				}
				
				$scope.$watch("season", teamExtras);
				$scope.$watch("team.id", teamExtras);
				
				
			}) ]);

})();
