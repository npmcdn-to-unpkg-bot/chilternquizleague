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
		 {path: '/:type/results',    name: 'CompetitionResults',   component: 'competitionResults'},
		 {path: '/:type/fixtures',    name: 'CompetitionFixtures',   component: 'competitionFixtures'},

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
		controller : "PlateCompetitionController",
		require : {"competitions" : "^competitions"}
	})
	.component('competitionCup', {
		templateUrl:"/competition/cup.html",
		controller : "CupCompetitionController",
		require : {"competitions" : "^competitions"}
	})
	.component('competitionIndividual', {
		templateUrl:"/competition/individual.html",
		controller : "IndividualCompetitionController",
		require : {"competitions" : "^competitions"}
	})
	.component('competitionBuzzer', {
		templateUrl:"/competition/team-buzzer.html",
		controller : "BuzzerCompetitionController",
		require : {"competitions" : "^competitions"}
		
	})
	.component('competitionResults', {
		templateUrl:"/competition/results.html",
		controller : "CompetitionAllResults",
		require : {"competitions" : "^competitions"}
		
	})
	.component('competitionFixtures', {
		templateUrl:"/competition/fixtures.html",
		controller : "CompetitionAllResults",
		require : {"competitions" : "^competitions"}
		
	})
	.directive('competitionsMenu', function(){ return{
		templateUrl:"/competition/competitions-menu.html",
		scope : {type : "<"}
	}})
	.directive('competitionsSidenav', function(){return{
		templateUrl:"/competition/sidenav.html",
		controller : "CompetitionTypesController",
		scope : {season : "="}
	}})
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
	
			$scope.$watch("season", function(season){
				season && viewService.viewP("competition-types", {id:season.id}).then(function(types){$scope.competitionTypes = types})
			})
	 
}])

mainApp.controller("CompetitionsController", ["$scope",function($scope){
	this.watch = function(name,ln){$scope.$watch(name,ln)}
	$scope.$on("season", function(evt, season){$scope.season = season})
}])
	

	mainApp.controller('CompetitionLeagueTableController', [ '$scope', 
			'viewService', function($scope, viewService) {
				
		      loadTable($scope,viewService);

			} ]);
	
	function competitionControllerFactory(type){
		return function($scope, viewService) {
			 
					this.$onInit = function() {
				  	
						var parent = this.competitions
						
			  		$scope.season = parent.season
			  		parent.watch("season", function(season){$scope.season = season})
				    $scope.$watch("competition", function(competition){competition && (parent.competition = competition)})  
				    };
				   this.$onDestroy = function(){
				  	 //this.competitions.unwatch("season")
				   }
				   
				   $scope.$watch("season", function(season){
				  	 if(season){
				  		 viewService.view("competition", {seasonId:season.id, type:type},function(comp){$scope.competition = comp})
				  	 }
				   })
				   
				   
				}
	}

	mainApp.controller('LeagueCompetitionController', [
			'$scope',
			'viewService',
			competitionControllerFactory("LEAGUE") ]);
	

		mainApp.controller('BeerCompetitionController', [ '$scope', 'viewService', competitionControllerFactory("BEER") ]);
		

		mainApp.controller('CupCompetitionController', [
			'$scope',
			'viewService',
			competitionControllerFactory("CUP") ]);
		

		mainApp.controller('PlateCompetitionController', [
			'$scope',
			'viewService',
			competitionControllerFactory("PLATE") ]);
		
		mainApp.controller('BuzzerCompetitionController', [
 			'$scope',
			'viewService',
			competitionControllerFactory("BUZZER") ]);
		
		mainApp.controller('IndividualCompetitionController', [
 			'$scope',
			'viewService',
			competitionControllerFactory("INDIVIDUAL") ]);
		
		mainApp.controller('CompetitionAllResults', [
   			'$scope', 'viewService',
   			 			function($scope, viewService) {
   				var $ctrl = this
   				
   				this.$routerOnActivate = function(next, previous) {
   				  // Get the hero identified by the route parameter
   				  $ctrl.type = next.params.type;
   				}
   				
					this.$onInit = function() {
				  	
						var parent = this.competitions
						
			  		$scope.season = parent.season
			  		parent.watch("season", function(season){$scope.season = season})
				    $scope.$watch("competition", function(competition){competition && (parent.competition = competition)})  
				    };
				   this.$onDestroy = function(){
				  	 //this.competitions.unwatch("season")
				   }
				   
				   $scope.$watch("season", function(season){
				  	 if(season){
				  		 viewService.view("competition", {seasonId:season.id, type:$ctrl.type},function(comp){$scope.competition = comp})
				  	 }
				   })
		                                   				
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