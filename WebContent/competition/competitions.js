(function(){
	
		
	mainApp.component('competitions', {
	  templateUrl:"/competition/competitions.html",
	  controller : "CompetitionsController",
	  $routeConfig: [
		 {path: '/LEAGUE',    name: 'CompetitionLeague',   component: 'competitionLeague', useAsDefault: true},
		 {path: '/BEER',    name: 'CompetitionBeerLeg',   component: 'competitionBeer'},
		 {path: '/PLATE',    name: 'Plate',   component: 'competitionPlate'},
		 {path: '/CUP',    name: 'Cup',   component: 'competitionCup'},
		 {path: '/INDIVIDUAL',    name: 'Individual',   component: 'competitionIndividual'},
		 {path: '/BUZZER',    name: 'Buzzer',   component: 'competitionBuzzer'},
		 ]
	})
	.component('competitionLeague', {
		templateUrl:"/competition/league.html",
		controller : "LeagueCompetitionController",
		bindings:{
			"onUpdate":"&",
			},
		require : {"competitions" : "^competitions"}
	})
	.component('competitionBeer', {
		templateUrl:"/competition/beer-leg.html",
		controller : "BeerCompetitionController",
		require : {"competitions" : "^competitions"}
	})
	.component('competitionPlate', {
		templateUrl:"/competition/plate.html",
	})
	.component('competitionCup', {
		templateUrl:"/competition/cup.html",
	})
	.component('competitionIndividual', {
		templateUrl:"/competition/individual.html",
	})
	.component('competitionBuzzer', {
		templateUrl:"/competition/team-buzzer.html",
	})
	.component('competitionsMenu', {
		templateUrl:"/competition/competitions-menu.html"
	})
	.component('competitionsSidenav', {
		templateUrl:"/competition/sidenav.html",
		controller : "CompetitionTypesController",
		require : {"competitions" : "^competitions"}

	})
	.directive("competitionLatestResults", function() {
	  return {
		templateUrl : "/competition/latest-results.html",
		controller : "CompetitionResultsTable",
		restrict: 'E',
    scope: {
      competition: '='
    }}
	})
	.directive("competitionNextFixtures", function() {
	  return {
		templateUrl : "/competition/next-fixtures.html",
		controller : "CompetitionFixturesTable",
		restrict: 'E',
    scope: {
      competition: '='
    }}
	})
	.directive("competitionLeagueTables", function() {
	  return {
		templateUrl : "/competition/league-table.html",
		controller : "CompetitionLeagueTableController",
		restrict: 'E',
    scope: {
      competition: '='
    }}
	})

	
	
	function loadTable($scope,viewService, tableName){
		 function func(competition) {
			if (competition) {
				$scope.leagueTable = viewService.view("tablesForCompetition", {
					id : competition.id
				});
			}
		};
		
		$scope.$watch("competition", func);
	}
	
	function loadResults($scope, viewService){
		function func(competition) {
			if (competition) {
				$scope.results = viewService.view("competition-results", {
					id : competition.id,
					isArray:true
				});
			}
		};
		
		$scope.$watch("competition", func);
		
	}
	
	function loadFixtures($scope, viewService){
		function func(competition) {
			if (competition) {
				$scope.fixtures = viewService.view("competition-fixtures", {
					id : competition.id,
					isArray:true
				});
			}
		};
		
		$scope.$watch("competition", func);
		
	}

mainApp.controller('CompetitionTypesController', [ '$scope', 'viewService', 'seasonService',
    function($scope, viewService, seasonService) {
			seasonService.getSeason().then(function(season){$scope.season = season})
			
			$scope.$watch("season", function(season){
				season && viewService.viewP("competition-types", {id:season.id}).then(function(types){$scope.competitionTypes = types})
			})
			
			$scope.$on("season", function(evt,season){$scope.season = season})
		 
}])

mainApp.controller("CompetitionsController", ["$scope",function($scope){
	this.watchers = []	
	this.watch = function(name,ln){this.watchers.push({name:name, fn:ln})}
	this.fire = function(name,value){
		this.watchers.filter(function(i){return i && i.name == name}).forEach(function(i){i.fn(value)})
	}
	this.unwatch = function(name){this.watchers = this.watchers.filter(function(i){return i.name != name})}
	
	$scope.$watch("season", function(season){$scope.$ctrl.fire("season", season)})
	$scope.$on("season", function(evt, season){$scope.season = season})
}])
	
mainApp.controller('OldCompetitionsController', [ '$scope', '$location',
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

	mainApp.controller('CompetitionLeagueTableController', [ '$scope', 
			'viewService', function($scope, viewService) {
				
		      loadTable($scope,viewService);

			} ]);
	
	function competitionControllerFactory(type){
		return function($scope, viewService) {
			  $scope.type = type 
					this.$onInit = function() {
				  	 this.competitions.watch("season", function(season){$scope.season = season})
				      
				    };
				   this.$onDestroy = function(){
				  	 this.competitions.unwatch("season")
				   }
				   
				   $scope.$watch("season", function(season){
				  	 if(season){
				  		 viewService.view("competition", {seasonId:season.id, type:$scope.type},function(comp){$scope.competition = comp})
				  	 }
				   })
				   
				   
				}
	}

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'viewService',
			competitionControllerFactory("LEAGUE") ]);
	

		mainApp.controller('CompetitionBeerTableController', [ '$scope', '$interval',
			'viewService', function($scope, $interval, viewService) {
				
		      loadTable($scope,viewService,"beertable");
			} ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', 'viewService', competitionControllerFactory("BEER") ]);
		

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
			'viewService',
			
			function($scope, viewService) {
				loadResults($scope, viewService)
				
			} ]);
		

		mainApp.controller('CompetitionFixturesTable', [ '$scope', 'viewService',
		function($scope, viewService) {

			loadFixtures($scope, viewService)

		} ]);
		
		
})();