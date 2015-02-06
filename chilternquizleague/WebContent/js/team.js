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

			viewService.list("Venue", function(venueList){
				
				var venues = {}
				
				for(idx in venueList){
					venues[venueList[idx].id] = venueList[idx]
				}
				
				var contents = generateICalContent(team.extras.fixtures, venues);

				var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
						+ ".ics";

				var blob = new Blob([ contents ], {
					type : "text/calendar;charset=utf-8"
				});

				saveAs(blob, filename);
				
			})
			


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
	
	mainApp.controller("TeamCharts",["$scope", 'viewService',"$filter", function($scope,viewService, $filter){
		$scope.setCurrentItem();
		
		function mapToProperty(arr,propName){
			var retval = [];
			for(idx in arr){
				retval.push(arr[idx][propName]);
			}
			
			return retval;
		}
		
		function dateToLabel(dates){
			var retval = [];
			for(idx in dates){
				var d = new Date(dates[idx]);
				retval.push($filter("date")(d,"dd MMM"));
			}
			
			return retval;
		}
		
		$scope.positionOptions={
				scaleStartValue:10,
				scaleStepWidth:-1,
				scaleOverride:true,
				scaleSteps:9,
				datasetFill : false,
				bezierCurve : true
				};
		
		$scope.options={
				datasetFill : true,
				bezierCurve : true,
				scaleBeginAtZero : false,
				legendTemplate : '<ul class="chart-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>',
			    datasetStroke : true,	
			};
		
		$scope.differenceOptions={
				datasetFill : false,
				bezierCurve : true,
				scaleBeginAtZero : false,
			    datasetStroke : true,	
			};
		
		function loadStats(){
			if ($scope.team && $scope.season && $scope.season.id ){
			viewService.view("team-statistics",{
								seasonId : $scope.season.id,
								teamId : $scope.team.id
							}, function(stats){
								
								var weekStats = stats.weekStats
								weekStats.sort(function(s1,s2){return s1.date - s2.date;});
								var dateLabels = dateToLabel(mapToProperty(weekStats, "date").sort());
								
								$scope.positionData = {
										labels: dateLabels,
										datasets: [{
											label:$scope.team.shortName,
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats, "leaguePosition") 
										}]
								};
								
								$scope.pointsData = {
										labels: dateLabels,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
											pointColor : "rgba(220,220,220,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats, "pointsFor") 
										},{
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
											label:"Against",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats, "pointsAgainst") 
										}]	
								};
								
								$scope.cumPointsData = {
										labels: dateLabels,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats, "cumuPointsFor") 
										},{
											label:"Against",
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats, "cumuPointsAgainst") 
										}]	
										
								};
								
								$scope.cumPointsDifferenceData = {
										labels: dateLabels,
										datasets: [{
											label:"Difference",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",

											data: mapToProperty(weekStats, "cumuPointsDifference") 
										}]	
										
								};
								

							});
				}
			}
		
		$scope.$watchGroup(["team","season"], loadStats);
	}]);

})();
