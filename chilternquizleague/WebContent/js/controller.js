var mainApp = angular.module('mainApp', ["ngAnimate",'ngMaterial','ui.router',"tc.chartjs"]).factory(
		'viewService',
		[
				"$http","$rootScope",
				function($http,$rootScope) {
					function loadFromServer(type, params, callback, isArray) {
						
						var paramString = "";
						for (name in params) {

							paramString = paramString + name + "=" + params[name] + "&";
						}

						paramString = paramString.length > 0 ? ("?" + paramString.slice(0,
								-1)) : "";

						return doLoad(type, paramString, callback, isArray);
					}

					function doLoad(type, paramString, callback, isArray) {
						
						$rootScope.$broadcast("progress", true);
						
						var retval = isArray ? [] : {};
						
						function callbackWrapper(item){
							
							callback && item ? callback(item):null;
							angular.copy(item,retval);
							
							$rootScope.$broadcast("progress", false);

						}
						
						$http.get("/view/" + type + paramString, {
							"responseType" : "json"
						}).success(callbackWrapper);
						
						return retval;
					}
					
					var textCache = {};

					var service = {

						load : function(type, id, callback) {

							return doLoad(type, "/" + id, callback);
						},

						view : function(type, params, callback) {

							var isArray = false;
							
							if(params){
								isArray = params.isArray;
								delete params.isArray;
							}

							return loadFromServer(type, params, callback,isArray);
						},

						list : function(type, callback) {

							return doLoad(type + "-list", "", callback, true);
						},

						post : function(type, payload, callback) {
							return $http.post("/view/" + type, payload).success(callback);
						},
						
						text : function(name) {
							var textHolder = textCache[name] = textCache.hasOwnProperty(name) ? textCache[name] : {};
							
							if(!textHolder.text){
								
								this.view("text",{name:name}, function(text){
									textHolder.text = text;
								});
							}
							
							return textHolder;
						}
					};
					return service;
				} ]);

mainApp.run([ '$rootScope', '$state', '$stateParams', '$mdDialog', 'viewService',
		function($rootScope, $state, $stateParams, $mdDialog, viewService) {

			$rootScope.$state = $state;
			$rootScope.$stateParams = $stateParams;

			$rootScope.closeDialog = function(){
				
				$mdDialog.hide();
			};

			$rootScope.global = viewService.view("globaldata");
			
			$rootScope.seasons = viewService.list("season-views");
			
			var dereg = $rootScope.$watchGroup(["global.currentSeasonId","seasons.length"], function(){
				
				if($rootScope.global.currentSeasonId && $rootScope.seasons.length > 1){
					
					for(idx in $rootScope.seasons){
						if($rootScope.seasons[idx].id == $rootScope.global.currentSeasonId){
							$rootScope.global.currentSeason = $rootScope.seasons[idx];
							dereg();
							break;
						}
					}

				}
			});
			
			$rootScope.showContactForm = function(title, recipient){
				
				$mdDialog.show({
					
					templateUrl : "/common/contact-dialog.html",
					clickOutsideToClose:false,
					locals : {title:title, recipient:recipient},
					controller: ['$scope',"title","recipient", function($scope, title, recipient) { 
						    $scope.title = title;
						    $scope.recipient = recipient;
						    $scope.sender = "";
						    $scope.text = "";
						    
						    $scope.closeDialog = function(){
						    	$mdDialog.hide();
						    };
						    
						    $scope.send = function(){
						    	
						    	viewService.post("submit-contact",{
						    		recipient:$scope.recipient,
						    		sender:$scope.sender,
						    		text:$scope.text
						    	});
						    	
						    	$mdDialog.hide();
						    	
						    };
						    
						  }]
					
				});
			};
		} ]);

mainApp.config(['$stateProvider', '$urlRouterProvider','$locationProvider',
	             function ($stateProvider,   $urlRouterProvider, $locationProvider){
	
	
	$stateProvider.state("home", {
    url: "/",
    templateUrl: '/indexContents.html'

  })
  .state("rules", {
  	url:"/rules",
  	templateUrl : "/rules/rules.html"
  })
  .state("contact", {
  	url:"/contact",
  	templateUrl : "/contact/contact.html"
  })
  .state("links", {
  	url:"/links",
  	templateUrl : "/links/links.html"
  })
  
  
  ;
	
$locationProvider.html5Mode(true);
}]);


mainApp.controller('MainController', [ '$scope', '$interval', 'viewService', '$mdSidenav',
		function($scope, $interval, viewService, $mdSidenav) {
			
		  $scope.toggleRight = function() {
		    $mdSidenav('right').toggle();
		    $mdSidenav('left').close();
		  };
		  
		  $scope.toggleLeft = function() {
		    $mdSidenav('left').toggle();
		  };
		  
		  $scope.closeLeft = function() {
			    $mdSidenav('left').close();
			  };
			  
		  $scope.openLeft = function() {
			    $mdSidenav('left').open();
			    $mdSidenav('right').close();
			  };
			  
			$scope.$on("progress", function(ev,value){$scope.progress = value;});

		} ]);

mainApp.controller('SeasonBroadcastController',['$scope','$rootScope', function($scope, $rootScope){
	
	$scope.$watch("season", function(season){
		
		if(season && season.id){
			$rootScope.$broadcast("season", season);
		}

		
	});
	
	
}]);
