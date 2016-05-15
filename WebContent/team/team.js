(function() {

	mainApp.component('teams', {
	  templateUrl:"/team/teams.html",
	  controller : "TeamsController",
	  $routeConfig: [
		 {path: '/',    name: 'TeamsPage',   component: 'teamsPage', useAsDefault: true},
		 {path: '/:id',    name: 'Team',   component: 'team'},
		 {path: '/team/:id',    name: 'Team',   component: 'team'},
		 {path: '/:id/results',    name: 'TeamResults',   component: 'teamResults'},
		 {path: '/:id/fixtures',    name: 'TeamFixtures',   component: 'teamFixtures'},
		 {path: '/:id/charts',    name: 'TeamCharts',   component: 'teamCharts'},
		 {path: '/start-team',    name: 'TeamStart',   component: 'teamStart'},
		 {path: '/logon',    name: 'TeamLogon',   component: 'teamLogon'},
		 ]
	})
	.component('teamsPage', {
		templateUrl:"/team/teams-content.html",
		require : {"teams" : "^teams"},
		controller : "TeamContentController"
	})
	.component('team', {
		templateUrl:"/team/team-details.html",
		controller : "TeamController",
		require : {"teams" : "^teams"}
	})
	.component('teamResults', {
		templateUrl:"/team/team-results.html",
		controller : "TeamController",
		require : {"teams" : "^teams"}
	})
	.component('teamFixtures', {
		templateUrl:"/team/team-fixtures.html",
		controller : "TeamController",
		require : {"teams" : "^teams"}
	})
	.component('teamCharts', {
		templateUrl:"/team/team-charts.html",
		controller : "TeamController",
		require : {"teams" : "^teams"}
	})
	.component('teamStart', {
		templateUrl:"/team/start-team.html",
	})
	.component('teamLogon', {
		templateUrl:"/team/team-logon.html",
		controller : "TeamLogon",
		bindings: { $router: '<' }
	})
	.component('teamEdit', {
		templateUrl:"/team/team-edit.html",
		controller : "TeamEdit"
	})
	.directive('teamsSidenav', function(){return{
		templateUrl:"/team/sidenav.html",
		scope : {teams : "<"}
	}})
	.directive('teamsMenu', function(){return{
		templateUrl:"/team/teams-menu.html",
		scope : {team : "<"}
	}})
	.directive('teamMenu', ["$rootScope",function($rootScope){return{
		templateUrl:"/team/team-menu.html",
		scope : {team : "<"},
		link  : function(scope){
			scope.showContactForm = $rootScope.showContactForm
		}
	}}])
	
	
	


	mainApp.controller('FindTeams', [ '$scope', 'viewService',
			function($scope, viewService) {

			} ]);

	mainApp.controller('TeamsController', [ '$scope', 
			'viewService',
			function($scope, viewService){
		
				COMMON.configureGroupController("team", this, $scope, viewService)
		}]);
	
	mainApp.controller('TeamContentController', [ '$scope',
			function($scope){
				
				var ctrl = this
				var deregs = []
				this.$onInit = function(){
					deregs.push(ctrl.teams.watch("teams", function(teams){$scope.teams = teams}))
				}
				this.onDestroy = function(){
					deregs.forEach(function(i){i()})
					deregs = []
				}
		}]);

	mainApp.controller('TeamController', [ '$scope', 'seasonService',
			function($scope, seasonService) {
				COMMON.configureItemController("team", this, $scope)
								$scope.season = {}
				seasonService.getSeason().then(function(season){$scope.season = season})
				
				$scope.copyToClipboard = function(text){
					clipboard.copy(document.baseURI + "calendar/" + text);
				}
	} ]);

	mainApp.controller("TeamExtrasController", [
			'$scope',
			'$interval',
			'viewService',
			'$location',
			function($scope, $interval, viewService, $location) {
				//$scope.setCurrentItem();
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
	

	
	mainApp.controller("TeamLogon", ["$scope","viewService","secureService","$rootRouter",function($scope,viewService,secureService,$rootRouter){
		
		var ctrl = this
		
		$scope.logon = function(password,email){
			
			secureService.logon(password,email,function(teamId){$rootRouter.navigate(["TeamEdit",{"id":teamId}])})
			
		}
		
		$scope.cookieEnabled = navigator.cookieEnabled
		
		$scope.authenticate = function(email){
			
			viewService.view("request-logon", {"email":email}, function(res){
				$scope.authenticated = res.result
			})
		}
		
	}])
	
		mainApp.controller("TeamEdit", ["$scope","secureService","$mdDialog", function($scope,secureService, $mdDialog){
		
			this.$routerOnActivate = function(next, previous) {
				$scope.team = secureService.load("team", next.params.id)
			}
			
		
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
