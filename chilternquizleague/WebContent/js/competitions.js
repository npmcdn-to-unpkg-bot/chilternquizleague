(function(){
	
	var templates = {
			
			LEAGUE:"league.html",
			BEER:"beer-leg.html",
			CUP:"cup.html",
			PLATE:"plate.html"	
	};
	
	function fetchHeaderText($scope, viewService, textKey){
		
		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
			if (currentSeasonId) {

				$scope.headerText = viewService.text(textKey, $scope.global);
			}
		});
	}
	
	function loadTable($scope,viewService, tableName){
		
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
	
	function loadResults($scope, viewService, type){
		
		function func(season) {
			if (season) {
				$scope.results = viewService.view("competition-results", {
					id : $scope.season.id,
					type : type,
					isArray:true
				});
			}
		};
		
		func($scope.season);
		$scope.$watch("season", func);
		
	}
	
	function loadFixtures($scope, viewService, type){
		
		function func(season) {
			if (season) {
				$scope.fixtures = viewService.view("competition-fixtures", {
					id : $scope.season.id,
					type : type,
					isArray:true
				});
			}
		};
		
		func($scope.season);
		$scope.$watch("season", func);
		
	}

mainApp.controller('CompetitionsController', [ '$scope', '$location',
		'viewService', function($scope, $location, viewService) {
	
	var type = $location.path().substr(1);
	
	$scope.getTemplate = function(type){return templates[type];};
	$scope.setCompetition = function(comp){$scope.competition = comp;$location.path(comp.type.name);};
	
	$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {


					
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
				

		      loadTable($scope,viewService,"leaguetable");

			} ]);

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {
				fetchHeaderText($scope, viewService, "league-comp");
			} ]);
	

		mainApp.controller('BeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				
		      loadTable($scope,viewService,"beertable");
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				fetchHeaderText($scope,viewService, "beer-comp");
			} ]);
		

		mainApp.controller('CupCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				fetchHeaderText($scope,viewService, "cup-comp");
				
			} ]);
		

		mainApp.controller('PlateCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				fetchHeaderText($scope, viewService, "plate-comp");

			} ]);
		
		mainApp.controller('ResultsTable', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				$scope.$watch("competition", function(competition){loadResults($scope, viewService,competition.type.name);});
				
				$scope.showReports = function(results, result){
					
					$scope.reportsData = {results:results,result:result};
					$scope.popupclass="popup";
					
				};
				
				$scope.closeWindow = function() {
					$scope.popupclass = "popdown";
					$scope.reports = null;
				};
				
			} ]);
		

			mainApp.controller('FixturesTable', [ '$scope', '$location', 'viewService',
			function($scope, $location, viewService) {

				$scope.$watch("competition", function(competition) {
					loadFixtures($scope, viewService, competition.type.name);
				});

			} ]);
		
		mainApp.controller("ReportsController", [ '$scope', '$interval',
			'viewService', '$location',
			function($scope, $interval, viewService, $location) {

				$scope.$watch("reportsData", function(reportsData) {
					if (reportsData) {
						$scope.reports = viewService.view("reports", {
							resultsKey : reportsData.results.key,
							homeTeamId : reportsData.result.fixture.home.id
						});
					}
				});
			} ]);
		
})();