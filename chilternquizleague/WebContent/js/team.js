(function() {

	mainApp.config([ '$stateProvider','$urlRouterProvider', function($stateProvider,$urlRouterProvider) {

		 $urlRouterProvider
		 //route from old-style to new-style team urls 
	        .when('/teams/team/:id', '/teams/:id');
		
		$stateProvider.state("teams", {

			url : "/teams",
			templateUrl:"/team/teams.html"


		}).state("teams.all", {

			url : "/all",
			views : {
				menu:{templateUrl:"/team/teams-menu.html"},
				content: {templateUrl:"/team/teams-content.html"}
		
			}

		}).state("teams.start", {

			url : "/start-team",
			views : {
				menu:{templateUrl:"/team/start-team-menu.html"},
				content: {templateUrl : '/team/start-team.html'}
		
			}

		}).state("teams.id", {

			url : "/:itemId",
			views : {
				menu:{templateUrl:"/team/team-menu.html"},
				content: {templateUrl:"/team/team-details.html"}
		
			}
		}).state("teams.results", {

			url : "/:itemId/results",
			views : {
				menu:{templateUrl:"/team/team-menu.html"},
				content: {templateUrl:"/team/team-results.html"}
		
			}
		}).state("teams.fixtures", {

			url : "/:itemId/fixtures",
			views : {
				menu:{templateUrl:"/team/team-menu.html"},
				content: {templateUrl : '/team/team-fixtures.html'}
		
			}

		}).state("teams.charts", {

			url : "/:itemId/charts",
			views : {
				menu:{templateUrl:"/team/team-menu.html"},
				content: {templateUrl:"/team/team-charts.html"}
		
			}
		});

	} ]);

	function extraStuff($scope, $interval, viewService, $location, $stateParams) {

		$scope.makeICal = function(team) {

			var contents = generateICalContent(team.extras.fixtures);

			var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
					+ ".ics";

			var blob = new Blob([ contents ], {
				type : "text/calendar;charset=utf-8"
			});

			saveAs(blob, filename);

		};
		
		$scope.season = {};
		
		$scope.$watch("global.currentSeason", function(currentSeason) {
			$scope.season = currentSeason;
		});
	}

	mainApp.controller('FindTeams', [ '$scope', 'viewService',
			function($scope, viewService) {

			} ]);

	mainApp.controller('TeamsController', [ '$scope', '$interval',
			'viewService', '$location', '$stateParams',
			 listControllerFactory("team", extraStuff)]);

	mainApp.controller('TeamController', [ '$scope', 
			function($scope) {
		$scope.setCurrentItem();
	} ]);

	mainApp.controller("TeamExtrasController", [
			'$scope',
			'$interval',
			'viewService',
			'$location',
			function($scope, $interval, viewService, $location) {
				$scope.setCurrentItem();
				function teamExtras() {

					if ($scope.team && $scope.season && $scope.season.id ) {
						if(!($scope.team.extras && ($scope.team.extras.id == $scope.team.id) && ($scope.team.extras.seasonId == $scope.season.id))){ 

						
						viewService.view("team-extras", {
							seasonId : $scope.season.id,
							teamId : $scope.team.id
						}, function(extras) {
							extras.seasonId = $scope.season.id;
							
							if ($scope.team.id == extras.id) {
								$scope.team.extras = extras;
							}
						});
						}

					}
				}

				$scope.$watchGroup(["team","season"], teamExtras);

			} ]);
	
	mainApp.controller("TeamCharts",["$scope", function($scope){
		$scope.setCurrentItem();
		var randomScalingFactor = function(){ return Math.round(Math.random()*100)};
		var lineChartData = {
		labels : ["January","February","March","April","May","June","July"],
		datasets : [
		{
		label: "My First dataset",
		fillColor : "rgba(220,220,220,0.2)",
		strokeColor : "rgba(220,220,220,1)",
		pointColor : "rgba(220,220,220,1)",
		pointStrokeColor : "#fff",
		pointHighlightFill : "#fff",
		pointHighlightStroke : "rgba(220,220,220,1)",
		data : [randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor()]
		},
		{
		label: "My Second dataset",
		fillColor : "rgba(151,187,205,0.2)",
		strokeColor : "rgba(151,187,205,1)",
		pointColor : "rgba(151,187,205,1)",
		pointStrokeColor : "#fff",
		pointHighlightFill : "#fff",
		pointHighlightStroke : "rgba(151,187,205,1)",
		data : [randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor()]
		}
		]
		};
		$scope.data = lineChartData;
		
		
	}]);

})();
