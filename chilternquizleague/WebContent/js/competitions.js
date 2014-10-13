(function(){
	
	mainApp.config([ '$routeProvider', '$locationProvider',
	         		function($routeProvider, $locationProvider) {
	         			$routeProvider.when('/competitions/:comp', {
	         				templateUrl : '/competition/competitions.html'
	         			});
	         		} ]);	
	
	var templates = {
			
			LEAGUE:"/competition/league.html",
			BEER:"/competition/beer-leg.html",
			CUP:"/competition/cup.html",
			PLATE:"/competition/plate.html",
			RESULTS:"/competition/results.html",
			FIXTURES:"/competition/fixtures.html",
			BUZZER:"/competition/team-buzzer.html"
	};
	
	
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
		'viewService','$routeParams', function($scope, $location, viewService, $routeParams) {
	
	var type = $routeParams.comp;
	
	$scope.$watch("season.id", function(season){season ? console.log(season): null;})
	
	$scope.getTemplate = function(type){return templates[type];};
	$scope.setCompetition = function(comp){$scope.competition = comp;$scope.templateName=comp.type.name;};
	
	var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
	
	$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
				if (currentSeasonId) {
					
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
	
	$scope.$watchCollection("competitions",function(competitions){
	    	
		
		if(competitions && competitions.length > 0){
		    var first = competitions.sort(function(c1,c2){c1.type.name.localeCompare(c2.type.name);})[0];	
			
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
		
		$scope.showOther = function(name){
			
			//$location.path($location.path() + "/" + name);
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
				
			} ]);
	

		mainApp.controller('CompetitionBeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				
		      loadTable($scope,viewService,"beertable");
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				
			} ]);
		

		mainApp.controller('CupCompetitionController', [
			'$scope',
			'$location',
			'viewService',
			function($scope, $location, viewService) {

				
				
			} ]);
		

		mainApp.controller('PlateCompetitionController', [ '$scope', '$location',
			'viewService', function($scope, $location, viewService) {

				

			} ]);
		
		mainApp.controller('CompetitionResultsTable', [
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
		
		
})();