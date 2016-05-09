var mainApp = angular.module('mainApp', ["ngAnimate",'ngMaterial','ngCookies','ui.router',"tc.chartjs","ui.tinymce","ngComponentRouter"]).factory(
		'viewService',
		[
				"$http","$rootScope",
				function($http,$rootScope) {
					function loadFromServer(type, params, callback, isArray) {
						return doLoad(type, params, callback, isArray);
					}
					
					function get(type, params){
						
						return $http.get("/view/" + type, {
							"responseType" : "json","params":params
						}).then(function(response){return response.data})
						
					}

					function doLoad(type, params, callback, isArray) {
							
						var retval = isArray ? [] : {};
						
						function callbackWrapper(item){
							
							try
							{
								callback && item ? callback(item):null;
								angular.copy(item,retval);
							}
							finally{
								$rootScope.$broadcast("progress", false);
							}
						}
						$rootScope.$broadcast("progress", true);
						get(type,params).then(callbackWrapper);
						
						return retval;
					}
					
					var textCache = {};

					var service = {

						load : function(type, id, callback) {

							return doLoad(type, "/" + id, callback);
						},
						loadP : function(type, id) {

							return get(type, "/" + id);
						},

						view : function(type, params, callback) {

							var isArray = false;
							
							if(params){
								isArray = params.isArray;
								delete params.isArray;
							}

							return loadFromServer(type, params, callback,isArray);
						},
						
						viewP : function(type, params, callback) {

							var isArray = false;
							
							if(params){
								isArray = params.isArray;
								delete params.isArray;
							}

							return get(type, params);
						},


						list : function(type, callback) {

							return doLoad(type + "-list", "", callback, true);
						},
						
						listP : function(type, callback) {

							return get(type + "-list", "");
						},

						post : function(type, payload, callback) {
							return $http.post("/view/" + type, payload).then(function(response){callback && callback(response.data)});
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
				} ])
				
				.factory("secureService",["$http","$rootScope","$cookies", function($http, $rootScope, $cookies){
					
					var session = {}
					
					function decrypt(password,payload){
						
						var dt = Aes.Ctr.decrypt(payload,password,256)
						
						var obj = angular.fromJson(angular.fromJson(dt))
						
						return obj
					}
					
					function encrypt(password, item){
						return Aes.Ctr.encrypt(angular.toJson(item),password,256)
					}
					
					function loadFromServer(type, params, callback, isArray) {
						return doLoad(type, params, callback, isArray);
					}

					function doLoad(type, params, callback, isArray) {
						
						$rootScope.$broadcast("progress", true);
						
						var retval = isArray ? [] : {};
						
						function callbackWrapper(response){
							
							var item = decrypt(session.password,response.data.text)
							
							callback && item ? callback(item):null;
							angular.copy(item,retval);
							
							$rootScope.$broadcast("progress", false);

						}
						
						params.sessionId = session.id
						
						$http.get("/secure/" + type, {
							"responseType" : "json","params":params}).then(callbackWrapper);
						
						return retval;
					}

					
					var service = {

						load : function(type, id, callback) {

							return doLoad(type + "/" + id,{}, callback);
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

							
							return $http.post("/secure/" + type, {
								"text":encrypt(session.password,payload)
								}).then(
									function(response){
										var res = decrypt(session.password,response.data.text)
										callback(res)
									}
									
									);
						},
						logon : function(password, email, callback){
							$http.post("/secure/logon", {"text":encrypt(password,{"email":email,"password":password})}).then(function(response){

								var res = decrypt(password,response.data.text)
								session = res
								callback(res.teamId)
								
							});
						}
						
					};
					return service;
				}])
				;

mainApp.factory("seasonService", ["viewService","$q",function(viewService,$q){
	
	var service = {
			seasons : [],
			season : null,
			global : null
	}
	service.getGlobal = function(){
				var promise = $q(function(resolve, reject){
					if(service.global){
						resolve(service.global)
					}
					else
					{
						viewService.viewP("globaldata",{}).then(function(global){return service.global = global},reject).then(resolve)
					}
				})
				
				return promise;
			}
			
	service.getSeasons = function(){
				var promise = $q(function(resolve, reject){
					if(service.seasons.length > 0){
						resolve(service.seasons)
					}
					else{
						viewService.listP("season-views", {}).then(function(seasons){
							return service.seasons = seasons},reject).then(resolve);
					}
				})
				
				return promise
			}
			
	service.getSeason = function(){
				var promise = $q(function(resolve, reject){
					if(service.season){
						resolve(service.season)
					}
					else{
						$q.all({global:service.getGlobal(), seasons:service.getSeasons()}).then(
								function(values){
									resolve(service.season = values.seasons.reduce(function(prev,curr){
										return prev ? prev : (curr.id == values.global.currentSeasonId ? curr : null)},null))
								}
						)
						
					}
				})
				return promise
			}


	return service
}]
		)
		
mainApp.value('$routerRootComponent', 'app')


mainApp.run([ '$rootScope', '$state', '$stateParams', '$mdDialog', 'viewService','seasonService',
		function($rootScope, $state, $stateParams, $mdDialog, viewService, seasonService) {

			$rootScope.$state = $state;
			$rootScope.$stateParams = $stateParams;

			seasonService.getGlobal().then(function(global){$rootScope.global = global})
			
			$rootScope.closeDialog = function(){
				
				$mdDialog.hide();
			};

			$rootScope.showInfo = function($event,content){
				
				$mdDialog.show(
			      $mdDialog.alert()
			        .parent($event)
			        .textContent(content))
			}
			
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

mainApp.config(function($mdThemingProvider) {

	  $mdThemingProvider.theme('amber')
	    .primaryPalette('amber');
	  $mdThemingProvider.theme('lime')
	    .primaryPalette('lime');
	  $mdThemingProvider.theme('yellow')
	    .primaryPalette('yellow');
	  $mdThemingProvider.theme('green')
	    .primaryPalette('green');
	  $mdThemingProvider.theme('cyan')
	    .primaryPalette('cyan');
	  $mdThemingProvider.theme('indigo')
	    .primaryPalette('indigo');
	  $mdThemingProvider.theme('red')
	    .primaryPalette('red');
	  $mdThemingProvider.theme('grey')
	    .primaryPalette('grey');
	  $mdThemingProvider.theme('purple')
	    .primaryPalette('purple');
	  $mdThemingProvider.theme('deep-orange')
	    .primaryPalette('deep-orange');
	  $mdThemingProvider.theme('light-blue')
	    .primaryPalette('light-blue');
	  $mdThemingProvider.theme('blue-grey')
	    .primaryPalette('blue-grey');
	  
	  
	  
	  
	});


mainApp.controller('MainController', [ '$scope', '$interval', 'viewService', '$mdSidenav','$mdMedia', 'seasonService',
		function($scope, $interval, viewService, $mdSidenav, $mdMedia, seasonService) {
			
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
			$scope.$on("progress", function(ev,value){
				$scope.sponsor = value?"hide-sponsor":"show-sponsor";});
			
			$scope.$mdMedia = $mdMedia

		} ]);

mainApp.controller('SeasonBroadcastController',['$scope','$rootScope', function($scope, $rootScope){
	
	$scope.$watch("season", function(season){
		
		if(season && season.id){
			$rootScope.$broadcast("season", season);
		}

		
	});
	
	
}]);
