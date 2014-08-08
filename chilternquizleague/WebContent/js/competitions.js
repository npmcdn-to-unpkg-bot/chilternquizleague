(function(){
	
	var templates = {
			
			LEAGUE:"league.html",
			BEER:"beer-leg.html",
			CUP:"cup.html",
			PLATE:"plate.html"	
	};
	
	function fetchHeaderText($scope,textKey){
		
		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
			if (currentSeasonId) {

				$scope.headerText = viewService.text(textKey, $scope.global);
			}
		});
	}
	
	function loadTable($scope, tableName){
		
		 function func(season) {
			if (season) {
				$scope.leagueTable = viewService.view(tableName, {
					id : $scope.season.id
				});
			}
		};
		
		func($scope.season);
		$scope.$watch("season", func);
	}
	
	function loadResults($scope, type){
		
		function func(season) {
			if (season) {
				$scope.results = viewService.view("competition-results", {
					id : $scope.season.id,
					type : type
				});
			}
		};
		
		func($scope);
		$scope.$watch("season", func);
		
	}

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
				

		      loadTable($scope,"leaguetable");

			} ]);

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {
				fetchHeaderText($scope, "league-comp");
			} ]);
	

		mainApp.controller('BeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				
		      loadTable($scope,"beertable");
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				fetchHeaderText($scope, "beer-comp");
			} ]);
		

		mainApp.controller('CupCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				fetchHeaderText($scope, "cup-comp");
				
			} ]);
		
		mainApp.controller('ResultsController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				fetchHeaderText($scope, "cup-comp");

				$scope.$watch("competition", function(competition){loadResults($scope, competition.type.name);});
				
			} ]);
		
})();