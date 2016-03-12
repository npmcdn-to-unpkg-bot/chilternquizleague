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

		}).state("teams.logon",{
			url:"/logon",
			views : {
				menu:{templateUrl:"/team/teams-menu.html"},
				content: {templateUrl:"/team/team-logon.html"}
		
			}

		})
		.state("teams.edit",{
			url:"/edit/:itemId",
			views : {
				menu:{templateUrl:"/team/team-edit-menu.html"},
				content: {templateUrl:"/team/team-edit.html"}
		
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
		})
		;

	} ]);

	function extraStuff($scope, $interval, viewService, seasonService, $location, $stateParams) {

		$scope.copyToClipboard = function(text){
			clipboard.copy(document.baseURI + "calendar/" + text);
		}
		
		$scope.season = {}
		seasonService.getSeason().then(function(season){$scope.season = season})
				
	}

	mainApp.controller('FindTeams', [ '$scope', 'viewService',
			function($scope, viewService) {

			} ]);

	mainApp.controller('TeamsController', [ '$scope', '$interval',
			'viewService', 'seasonService','$location', '$stateParams',
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
			return arr.map(function(i){return i==null ? null : i[propName]});
		}
		
		function dateToLabel(dates){
			return dates.map(function(d){return $filter("date")(new Date(d),"dd MMM")});
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
		
		function loadStats(newValues){
			
			if(newValues && newValues.length > 1 && newValues[0] && newValues[1]){
				
				doLoadStats(newValues[0], newValues[1])
			}
			
		}
		
		function doLoadStats(team, season){
			if (season.id ){
			viewService.view("team-statistics",{
								seasonId : season.id,
								teamId : team.id
							}, function(stats){
								
								var weekStats = stats.weekStats
								weekStats.sort(function(s1,s2){return s1.date - s2.date;});
								var dateLabels = dateToLabel(mapToProperty(weekStats, "date").sort());
								
								$scope.positionData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:team.shortName,
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats, "leaguePosition") 
										}]
								};
								
								function nullIgnorables(i){return i.ignorable ? null : i}
								
								$scope.pointsData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
											pointColor : "rgba(220,220,220,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "pointsFor")
										},{
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
											label:"Against",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "pointsAgainst") 
										}]	
								};
								
								$scope.cumPointsData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"For",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsFor") 
										},{
											label:"Against",
											strokeColor: "rgba(205,50,50,1)",
											fillColor: "rgba(151,187,205,0.2)",
											pointColor : "rgba(205,50,50,1)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(205,50,50,1)",
											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsAgainst") 
										}]	
										
								};
								
								$scope.cumPointsDifferenceData = {
										labels: dateLabels,
										responsive:true,
										datasets: [{
											label:"Difference",
											strokeColor: "rgba(220,220,220,1)",
											pointColor : "rgba(220,220,220,1)",
											fillColor: "rgba(220,220,220,0.2)",
								            pointStrokeColor: "#fff",
								            pointHighlightFill: "#fff",
								            pointHighlightStroke: "rgba(220,220,220,1)",

											data: mapToProperty(weekStats.map(nullIgnorables), "cumuPointsDifference") 
										}]	
										
								};
								

							});
				}
			}
		
		$scope.$watchGroup(["team","season"], loadStats);
	}]);
	
	mainApp.controller("TeamLogon", ["$scope","viewService","secureService","$state", function($scope,viewService,secureService, $state){
		
		$scope.logon = function(password,email){
			
			secureService.logon(password,email,function(teamId){$state.go("teams.edit",{"itemId":teamId})})
			
		}
		
		$scope.cookieEnabled = navigator.cookieEnabled
		
		$scope.authenticate = function(email){
			
			viewService.view("request-logon", {"email":email}, function(res){
				$scope.authenticated = res.result
			})
		}
		
	}])
	
		mainApp.controller("TeamEdit", ["$scope","secureService","$stateParams","$mdDialog", function($scope,secureService,$stateParams, $mdDialog){
		
		$scope.team = secureService.load("team",$stateParams.itemId)
		$scope.users = secureService.list("user")
		$scope.tinymceOptions={ 
				plugins: "link, image, autolink, table, code, charmap, searchreplace, contextmenu",
				menubar:true,
			    toolbar: "undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist | link image"
		}
		$scope.saveTeam = function(team){
			secureService.post("team",team, function(){ $mdDialog.show(
		      $mdDialog.alert()
	         .clickOutsideToClose(true)
	        .content('Team Details Saved.')
	        .ariaLabel('Team Saved')
	        .ok('Ok')
	    );})    	
		}
		
		$scope.matchUsers = function(users,text){
			
			return users.filter(function(user){return user.name.toLowerCase().indexOf(text.toLowerCase()) > -1})
			
		}
		
		$scope.newUserForm = function(ev){
			
			function DialogController($scope, $mdDialog){
				$scope.cancel = function(){$mdDialog.cancel()};
				$scope.add = function(user){$mdDialog.hide(user)}; 
				$scope.user = {}
				$scope.users = secureService.list("user")
				$scope.inUsers = function(user){return $scope.users.find(function(user1){return user.email == user1.email})!== undefined}
				
			}
						
			$mdDialog.show({
	      templateUrl: '/team/new-user-dialog.html',
	      parent: angular.element(document.body),
	      targetEvent: ev,
	      clickOutsideToClose:false,
	      controller:DialogController
	    })
	        .then(function(user) {
	        	secureService.post("user", user, function(user){$scope.team.users.push(user)})
	        	
	        	
	        });
	  
		}
			
		
		
	}])

})();
