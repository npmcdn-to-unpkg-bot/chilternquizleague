(function(){
	
	mainApp.config([ '$stateProvider',function($stateProvider) {
		$stateProvider
		.state("competitions", {
			
			url:"/competitions",
			templateUrl : '/competition/competitions.html'
			
		})
		.state("competitions.league", {
			
			url:"/LEAGUE",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/league.html'
				}
			}
			
		})
		.state("competitions.beer", {
			
			url:"/BEER",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/beer-leg.html'
				}
			}
			
		})
		
		.state("competitions.cup", {
			
			url:"/CUP",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/cup.html'
				}
			}
			
		})
		.state("competitions.plate", {
			
			url:"/PLATE",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/plate.html'
				}
			}
			
		})
		
		.state("competitions.buzzer", {
			
			url:"/BUZZER",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/team-buzzer.html'
				}
			}
			
		})
		
		.state("competitions.individual", {
			
			url:"/INDIVIDUAL",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/individual.html'
				}
			}
			
		})
		
		.state("competitions.results", {
			
			url:"/:type/results",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/results.html'
				}
			}
			
		})
		.state("competitions.fixtures", {
			
			url:"/:type/fixtures",
			views:{
				menu:{
					templateUrl : '/competition/competitions-menu.html'
				},
				content:{
					templateUrl : '/competition/fixtures.html'
				}
			}
			
		});

	 } ]);	
	
	
	
	function loadTable($scope,viewService, tableName){
		 function func(season) {
			if (season) {
				$scope.leagueTable = viewService.view(tableName, {
					id : $scope.season.id
				});
			}
		};
		
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
		
		$scope.$watch("season", func);
		
	}

mainApp.controller('CompetitionsController', [ '$scope', '$location',
		'viewService','$stateParams', function($scope, $location, viewService, $stateParams) {

	function getForTypeName(name){
		
		var comps = $scope.competitions.filter(function(comp){return comp.type.name == name})
		
		return comps.length > 0 ? comps.pop():$scope.competitions[0]
	}
	
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
	};
	

	
	$scope.$watch("global.currentSeason",function(season){
		$scope.season = season;
	});
	

	$scope.$watchCollection("competitions",function(competitions){
	    	
		
		if(competitions && competitions.length > 0){
		    var compType = $scope.competition ? $scope.competition.type.name : "LEAGUE"
		    	//competitions.sort(function(c1,c2){c1.type.name.localeCompare(c2.type.name);})[0].type;	
	    	$scope.setCompetitionByType(compType);}

	    });
	
	
		$scope.$watch("season", function(season) {
			if(season){

			$scope.competitions = viewService.view("competitions-view", {
				id : season.id,
				isArray : true
			});
			}
		});
		
		$scope.$on("season",function(evt, season){
			$scope.season = season;
		});
		
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
		
		mainApp.controller('BuzzerCompetitionController', [ '$scope', function($scope) {

				$scope.setCompetitionByType("BUZZER");

			} ]);
		mainApp.controller('IndividualCompetitionController', [ '$scope', function($scope) {

			$scope.setCompetitionByType("INDIVIDUAL");

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
				
			} ]);
		

		mainApp.controller('FixturesTable', [ '$scope', '$location', 'viewService',
		function($scope, $location, viewService) {

			$scope.$watch("competition", function(competition) {
				competition && loadFixtures($scope, viewService, competition.type.name);
			});

		} ]);
		
		
})();