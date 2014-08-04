(function(){
	
	var templates = {
			
			LEAGUE:"league.html",
			BEER:"beer-leg.html",
			CUP:"cup.html",
			PLATE:"plate.html"	
	};

mainApp.controller('CompetitionsController', [ '$scope', '$location',
		'viewService', function($scope, $location, viewService) {
	
	var type = $location.path().substr(1);
	
	$scope.getTemplate = function(type){return templates[type];};
	$scope.setCompetition = function(comp){$scope.competition = comp;$location.path(comp.type.name);};
	
	$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {

					$scope.headerText = viewService.text("league-comp", $scope.global);
					
					var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
					
					loadSeasons(currentSeasonId);
				}
			});
	
	function getForTypeName(name){
		
		for(idx in $scope.competitions){
			if($scope.competitions[idx].type.name == name){
				return $scope.competitions[idx];
			}			
		}
		
		return null;
	}    
	
	$scope.$watch("competitions[0]",function(first){
	    	
	    	if(first){
	    		if(type){
	    			$scope.setCompetition(getForTypeName(type));
	    			
	    		}else{
	    	$scope.setCompetition(first);}
	    	}
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

	mainApp.controller('LeagueTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				function loadTable(season) {

					if (season) {

						$scope.leagueTable = viewService.view("leaguetable", {
							id : $scope.season.id
						});
					}
				}

				loadTable($scope.season);

				$scope.$watch("season", loadTable);
			} ]);

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				$scope.$watch("global.currentSeasonId", function(
						currentSeasonId) {
					if (currentSeasonId) {

						$scope.headerText = viewService.text("league-comp",
								$scope.global);
					}
				});

			} ]);
	

		mainApp.controller('BeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				function loadTable(season) {

					if (season) {

						$scope.leagueTable = viewService.view("beertable", {
							id : $scope.season.id
						});
					}
				}

				loadTable($scope.season);

				$scope.$watch("season", loadTable);
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
					if (currentSeasonId) {

						$scope.headerText = viewService.text("beer-comp", $scope.global);
					}
				});

			} ]);
})();