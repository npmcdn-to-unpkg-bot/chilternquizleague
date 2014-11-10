(function(){
	
	mainApp.config([ '$stateProvider',function($stateProvider) {
		$stateProvider
		.state("competitions", {
			
			url:"/competitions",
			templateUrl : '/competition/competitions.html'
			
		})
		.state("competitions.league", {
			
			url:"/LEAGUE",
			templateUrl : '/competition/league.html'
			
		})
		.state("competitions.beer", {
			
			url:"/BEER",
			templateUrl : '/competition/beer-leg.html'
			
		})
		
		.state("competitions.cup", {
			
			url:"/CUP",
			templateUrl : '/competition/cup.html'
			
		})
		.state("competitions.plate", {
			
			url:"/PLATE",
			templateUrl : '/competition/plate.html'
			
		})
		
		.state("competitions.buzzer", {
			
			url:"/BUZZER",
			templateUrl : '/competition/team-buzzer.html'
			
		})
		
		.state("competitions.results", {
			
			url:"/:type/results",
			templateUrl : '/competition/results.html'
			
		})
		.state("competitions.fixtures", {
			
			url:"/:type/fixtures",
			templateUrl : '/competition/fixtures.html'
			
		})

	 } ]);	
	
	
	
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
		'viewService','$stateParams', function($scope, $location, viewService, $stateParams) {

	function getForTypeName(name){
		
		for(idx in $scope.competitions){
			if($scope.competitions[idx].type.name == name){
				return $scope.competitions[idx];
			}			
		}
		
		return null;
	}
	
	$scope.$watch("season.id", function(season){season ? console.log(season): null;})
	$scope.setCompetition = function(comp){$scope.competition = comp;};
	$scope.setCompetitionByType = function(type){
		
		if($scope.competitions){
			$scope.setCompetition(getForTypeName(type));
		} 
		else{
			var dereg = $scope.$watchCollection("competitions",function(competitions){
				if(competitions && competitions.length > 0){
					$scope.setCompetition(getForTypeName(type));
					dereg();
			    }
			});
			
		}
	}
		
	
	var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
	
	$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					
					loadSeasons(currentSeasonId);
				}
			});
	
    
	
	$scope.$watchCollection("competitions",function(competitions){
	    	
		
		if(competitions && competitions.length > 0){
		    var first = competitions.sort(function(c1,c2){c1.type.name.localeCompare(c2.type.name);})[0];	
	    	$scope.setCompetition(first);}

	    });

		$scope.$watch("season", function(season) {
			if(season){
			$scope.competitions = viewService.view("competitions-view", {
				id : season.id,
				isArray : true
			});
			}
		});
		
		$scope.showOther = function(name){
			
			$scope.templateName=name;
			
		};

		} ]);

	mainApp.controller('CompetitionLeagueTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				

		      loadTable($scope,viewService,"leaguetable");

			} ]);

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {
				$scope.setCompetitionByType("LEAGUE");
			} ]);
	

		mainApp.controller('CompetitionBeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				
		      loadTable($scope,viewService,"beertable");
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {
			$scope.setCompetitionByType("BEER");
				
			} ]);
		

		mainApp.controller('CupCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				$scope.setCompetitionByType("CUP");
				
			} ]);
		

		mainApp.controller('PlateCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

			$scope.setCompetitionByType("PLATE");

			} ]);
		mainApp.controller('CompetitionAllResults', [
   			'$scope',
   			'$stateParams',
   			function($scope, $stateParams) {
   				
   				$scope.setCompetitionByType($stateParams.type);
		                                   				
		}]);
		
		mainApp.controller('CompetitionResultsTable', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				$scope.$watch("competition", function(competition){competition && loadResults($scope, viewService,competition.type.name);});
				
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
					competition && loadFixtures($scope, viewService, competition.type.name);
				});

			} ]);
		
		
})();