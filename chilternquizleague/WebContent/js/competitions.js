mainApp.controller('CompetitionsController', [ '$scope', '$location',
		'viewService', function($scope, $location, viewService) {

	var templates = {
			
			LEAGUE:"league.html"
	};
	
	$scope.getTemplate = function(type){return templates[type];};
	$scope.setCompetition = function(comp){$scope.competition = comp;};
	
	$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {

					viewService.list("season-views", function(seasons) {
						$scope.seasons = seasons;

						for (idx in seasons) {

							if (seasons[idx].id == currentSeasonId) {
								$scope.season = seasons[idx];
								return;
							}

							$scope.season = seasons ? seasons[0] : null;
						}

					});

				}
			});
	
	    $scope.$watch("competitions.length",function(length){
	    	
	    	if(length > 0){}
	    	$scope.setCompetition($scope.competitions[0]);
	    	
	    });

			$scope.$watch("season", function(season) {
				if(season){
				$scope.competitions = viewService.view("competitions-view", {
					id : season.id,
					isArray : true
				});
				}
			});

		} ]);